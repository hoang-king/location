package com.example.location.di

import com.example.location.data.remote.firebase.FirebaseAuthService
import com.example.location.data.remote.firestore.FirestoreService
import com.example.location.data.remote.firestore.RoomService
import com.example.location.data.repository.ChatRepositoryImpl
import com.example.location.data.repository.LocationRepositoryImpl
import com.example.location.data.repository.RoomRepositoryImpl
import com.example.location.domain.repository.ChatRepository
import com.example.location.domain.repository.LocationRepository
import com.example.location.domain.repository.RoomRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthService(auth: FirebaseAuth): FirebaseAuthService {
        return FirebaseAuthService(auth)
    }

    @Provides
    @Singleton
    fun provideFirestoreService(firestore: FirebaseFirestore): FirestoreService {
        return FirestoreService(firestore)
    }

    @Provides
    @Singleton
    fun provideRoomService(firestore: FirebaseFirestore): RoomService {
        return RoomService(firestore)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(firestoreService: FirestoreService): LocationRepository {
        return LocationRepositoryImpl(firestoreService)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        roomService: RoomService,
        authService: FirebaseAuthService
    ): ChatRepository {
        return ChatRepositoryImpl(roomService, authService)
    }

    @Provides
    @Singleton
    fun provideRoomRepository(
        roomService: RoomService,
        authService: FirebaseAuthService
    ): RoomRepository {
        return RoomRepositoryImpl(roomService, authService)
    }
}
