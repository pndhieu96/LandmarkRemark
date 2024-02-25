package com.example.landmarkremark.data.models

import com.google.firebase.auth.FirebaseUser

data class User (
    var uid: String = "",
    var email: String = ""
) {

    // convert a firebaseUser object to user object used in the app
    companion object {
        fun firebaseUserToUser(firebaseUser: FirebaseUser?): User {
            return User(
                uid = firebaseUser?.uid ?: "",
                email = firebaseUser?.email ?: ""
            )
        }
    }
}