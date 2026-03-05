package com.example.location.data.remote.firestore

import android.util.Log
import com.example.location.data.model.Message
import com.example.location.data.model.Room
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val TAG = "RoomService"

    suspend fun createRoom(name: String, creatorId: String, code: String): Result<Room> {
        Log.d(TAG, "createRoom: name=$name, creatorId=$creatorId, code=$code")
        return try {
            val roomData = hashMapOf(
                "name" to name,
                "code" to code,
                "creatorId" to creatorId,
                "members" to listOf(creatorId),
                "createdAt" to System.currentTimeMillis()
            )
            val docRef = firestore.collection("rooms").add(roomData).await()
            Log.d(TAG, "createRoom: Success, docId=${docRef.id}")
            val room = Room(
                id = docRef.id,
                name = name,
                code = code,
                creatorId = creatorId,
                members = listOf(creatorId),
                createdAt = System.currentTimeMillis()
            )
            Result.success(room)
        } catch (e: Exception) {
            Log.e(TAG, "createRoom: Error", e)
            Result.failure(e)
        }
    }

    suspend fun joinRoom(code: String, userId: String): Result<Room> {
        Log.d(TAG, "joinRoom: Attempting to join with code='$code', userId=$userId")
        return try {
            val snapshot = firestore.collection("rooms")
                .whereEqualTo("code", code)
                .get()
                .await()

            if (snapshot.documents.isEmpty()) {
                Log.e(TAG, "joinRoom: Room NOT found for code='$code'")
                return Result.failure(Exception("Không tìm thấy phòng với mã '$code'. Vui lòng kiểm tra lại."))
            }

            val doc = snapshot.documents.first()
            Log.d(TAG, "joinRoom: Room found, docId=${doc.id}. Current data: ${doc.data}")
            
            // Perform the update
            try {
                firestore.collection("rooms")
                    .document(doc.id)
                    .update("members", FieldValue.arrayUnion(userId))
                    .await()
                Log.d(TAG, "joinRoom: Membership updated in Firestore")
            } catch (updateError: Exception) {
                Log.e(TAG, "joinRoom: Failed to update members in Firestore", updateError)
                return Result.failure(Exception("Lỗi quyền truy cập: Bạn không thể tham gia phòng này. Lỗi: ${updateError.message}"))
            }

            val roomData = doc.data
            val room = roomData?.let { Room.fromMap(doc.id, it) }
                ?: run {
                    Log.e(TAG, "joinRoom: Invalid room data in doc ${doc.id}")
                    return Result.failure(Exception("Dữ liệu phòng không hợp lệ"))
                }

            Log.d(TAG, "joinRoom: Successfully joined room ${room.name}")
            Result.success(room.copy(members = (room.members + userId).distinct()))
        } catch (e: Exception) {
            Log.e(TAG, "joinRoom: Fatal error", e)
            Result.failure(e)
        }
    }

    suspend fun leaveRoom(roomId: String, userId: String): Result<Unit> {
        Log.d(TAG, "leaveRoom: roomId=$roomId, userId=$userId")
        return try {
            firestore.collection("rooms")
                .document(roomId)
                .update("members", FieldValue.arrayRemove(userId))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "leaveRoom: Error", e)
            Result.failure(e)
        }
    }

    suspend fun deleteRoom(roomId: String): Result<Unit> {
        Log.d(TAG, "deleteRoom: roomId=$roomId")
        return try {
            firestore.collection("rooms")
                .document(roomId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "deleteRoom: Error", e)
            Result.failure(e)
        }
    }

    fun getRoomById(roomId: String): Flow<Room?> = callbackFlow {
        val listener: ListenerRegistration = firestore
            .collection("rooms")
            .document(roomId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val room = snapshot?.data?.let { Room.fromMap(snapshot.id, it) }
                trySend(room)
            }
        awaitClose { listener.remove() }
    }

    fun getRoomsByUser(userId: String): Flow<List<Room>> = callbackFlow {
        val listener: ListenerRegistration = firestore
            .collection("rooms")
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Room.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(rooms)
            }
        awaitClose { listener.remove() }
    }

    fun getMessages(roomId: String): Flow<List<Message>> = callbackFlow {
        val listener: ListenerRegistration = firestore
            .collection("rooms")
            .document(roomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Message.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(roomId: String, message: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("rooms")
                .document(roomId)
                .collection("messages")
                .add(message)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
