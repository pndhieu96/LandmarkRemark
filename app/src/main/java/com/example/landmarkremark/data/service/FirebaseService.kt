package com.example.landmarkremark.data.service

import android.util.Log
import com.example.landmarkremark.base.NetworkResult
import com.example.landmarkremark.data.models.Note
import com.example.landmarkremark.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Class provides methods to send requests to Firebase Authentication service
 */
class FirebaseService @Inject constructor() {
    private val TAG = "AuthenticationFirebaseService"
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var firestore: FirebaseFirestore

    /**
     * Sign in user with email and password
     */
    suspend fun signIn(
        email: String,
        password: String
    ) : NetworkResult<User> = suspendCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                val user = firebaseUser?.let {
                    User.firebaseUserToUser(it)
                }
                continuation.resume(NetworkResult.Success(user ?: User()))
            } else {
                val exception = Exception(task.exception?.message ?: "Login failed")
                continuation.resume(NetworkResult.Error(exception))
            }
        }
    }

    /**
     * Logout user
     */
    suspend fun logout() : NetworkResult<User> {
        Log.w(TAG, "AuthenticationFirebaseService: logout")
        auth.signOut()
        return NetworkResult.Success(User())
    }

    /**
     * Get user information
     */
    suspend fun getUser() : NetworkResult<User> {
        return NetworkResult.Success(User.firebaseUserToUser(auth.currentUser))
    }

    /**
     * Create a new note
     */
    suspend fun createNote(note: Note): NetworkResult<Boolean>
    = suspendCoroutine{ continuation ->
        firestore.collection("Notes")
            .add(note)
            .addOnSuccessListener {
                continuation.resume(NetworkResult.Success(true))
            }
            .addOnFailureListener{
                continuation.resume(NetworkResult.Success(false))
            }
    }

    /**
     * get list of notes order by timestamp
     */
    suspend fun getNotes(): NetworkResult<List<Note>>
            = suspendCoroutine{ continuation ->
        firestore.collection("Notes").orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val notes = mutableListOf<Note>()
                for (document in result) {
                    val noteId = document.id
                    val userId = document.getString("userId") ?: ""
                    val userEmail = document.getString("userEmail") ?: ""
                    val latitude = document.getDouble("latitude") ?: 0.0
                    val longitude = document.getDouble("longitude") ?: 0.0
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getLong("timestamp") ?: 0
                    val note = Note(noteId, userId, userEmail, latitude, longitude, content, timestamp)
                    notes.add(note)
                }
                continuation.resume(NetworkResult.Success(notes))
            }
            .addOnFailureListener{
                val exception = Exception(it?.message ?: "Failed to fetch data from Firebase")
                continuation.resume(NetworkResult.Error(exception))
            }
    }

    /**
     * Edit information of a created note
     */
    suspend fun editNote(note: Note): NetworkResult<Boolean>
            = suspendCoroutine{ continuation ->
        firestore.collection("Notes")
            .document(note.noteId)
            .set(note)
            .addOnSuccessListener {
                continuation.resume(NetworkResult.Success(true))
            }
            .addOnFailureListener{
                continuation.resume(NetworkResult.Success(false))
            }
    }

    /**
     * Delete a create note
     */
    suspend fun deleteNote(note: Note): NetworkResult<Boolean>
            = suspendCoroutine{ continuation ->
        firestore.collection("Notes")
            .document(note.noteId)
            .delete()
            .addOnSuccessListener {
                continuation.resume(NetworkResult.Success(true))
            }
            .addOnFailureListener{
                continuation.resume(NetworkResult.Success(false))
            }
    }
}