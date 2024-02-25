package com.example.landmarkremark.data.repositories

import com.example.landmarkremark.base.NetworkResult
import com.example.landmarkremark.data.models.Note
import com.example.landmarkremark.data.service.FirebaseService
import com.example.landmarkremark.di.IoDispatcher
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseService: FirebaseService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun signIn(
        email: String,
        password: String
    ) = withContext(dispatcher) {
        when(val result = firebaseService.signIn(email, password)) {
            is NetworkResult.Success -> {
                result
            }
            is NetworkResult.Error -> {
                throw result.exception
            }
        }
    }

    suspend fun logout() = withContext(dispatcher) {
        val result = firebaseService.logout()
        result as NetworkResult.Success
    }

    suspend fun getUser() = withContext(dispatcher) {
        firebaseService.getUser()
    }

    suspend fun getNotes() = withContext(dispatcher) {
        when(val result = firebaseService.getNotes()) {
            is NetworkResult.Success -> {
                result
            }
            is NetworkResult.Error -> {
                throw result.exception
            }
        }
    }

    suspend fun createNote(note: Note) = withContext(dispatcher) {
        when(val result = firebaseService.createNote(note)) {
            is NetworkResult.Success -> {
                result
            }
            is NetworkResult.Error -> {
                throw result.exception
            }
        }
    }

    suspend fun editNote(note: Note) = withContext(dispatcher) {
        when(val result = firebaseService.editNote(note)) {
            is NetworkResult.Success -> {
                result
            }
            is NetworkResult.Error -> {
                throw result.exception
            }
        }
    }

    suspend fun deleteNote(note: Note) = withContext(dispatcher) {
        when(val result = firebaseService.deleteNote(note)) {
            is NetworkResult.Success -> {
                result
            }
            is NetworkResult.Error -> {
                throw result.exception
            }
        }
    }

    suspend fun getNotesByCoordinate(latLng: LatLng) = withContext(dispatcher) {
        when(val result = firebaseService.getNotesByCoordinate(latLng)) {
            is NetworkResult.Success -> {
                result
            }
            is NetworkResult.Error -> {
                throw result.exception
            }
        }
    }
}