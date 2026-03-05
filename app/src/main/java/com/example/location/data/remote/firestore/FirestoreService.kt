package com.example.location.data.remote.firestore

import com.example.location.data.model.UserLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getLocationsInRoom(roomId: String): Flow<List<UserLocation>> = callbackFlow {
        val listener: ListenerRegistration = firestore
            .collection("rooms")
            .document(roomId)
            .collection("locations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val locations = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { UserLocation.fromMap(it) }
                } ?: emptyList()
                trySend(locations)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateLocation(roomId: String, location: UserLocation): Result<Unit> {
        return try {
            firestore.collection("rooms")
                .document(roomId)
                .collection("locations")
                .document(location.userId)
                .set(location.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeLocation(roomId: String, userId: String): Result<Unit> {
        return try {
            firestore.collection("rooms")
                .document(roomId)
                .collection("locations")
                .document(userId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
