package com.example.landmarkremark.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
/**
 * Class provides network dependencies required by the app
 * */
class NetworkModule {

    @Provides
    @Singleton
    /**
     * Create a singleton authentication service of firebase
     * */
    fun provideFirebaseAuth() : FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    /**
     * Create a singleton firestore instance of firebase
     * */
    fun provideFirebaseFirestore() : FirebaseFirestore {
        return Firebase.firestore
    }
}