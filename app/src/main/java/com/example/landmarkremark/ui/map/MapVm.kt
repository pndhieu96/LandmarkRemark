package com.example.landmarkremark.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.landmarkremark.base.BaseViewModel
import com.example.landmarkremark.base.Event
import com.example.landmarkremark.base.NetworkResult
import com.example.landmarkremark.data.models.Note
import com.example.landmarkremark.data.models.User
import com.example.landmarkremark.data.repositories.FirebaseRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapVm @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : BaseViewModel() {
    private var _user = MutableLiveData<Event<User>>()
    val user : LiveData<Event<User>>
        get() = _user

    private var _notes = MutableLiveData<Event<List<Note>>>()
    val notes : LiveData<Event<List<Note>>>
        get() = _notes

    fun logout() {
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.logout()
            _user.postValue(Event(result.data))
        }
    }

    fun getNotes() {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.getNotes()
            _notes.postValue(Event(result.data))
        }
        registerJobFinish()
    }

    fun getNotesByEmailOrContent(query: String) {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.getNotes()
            val notes = result.data.filter { note ->
                note.userEmail.contains(query, ignoreCase = true) || note.content.contains(query, ignoreCase = true)
            }
            _notes.postValue(Event(notes))
        }
        registerJobFinish()
    }

    fun getNotesByCoordinator(latLng: LatLng) {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.getNotesByCoordinate(latLng)
            _notes.postValue(Event(result.data))
        }
        registerJobFinish()
    }

    fun getUser() {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.getUser()
            if(result is NetworkResult.Success) {
                _user.postValue(Event(result.data))
            }
        }
        registerJobFinish()
    }
}