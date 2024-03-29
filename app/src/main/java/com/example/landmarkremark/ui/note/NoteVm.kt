package com.example.landmarkremark.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.landmarkremark.base.BaseViewModel
import com.example.landmarkremark.base.Event
import com.example.landmarkremark.base.NetworkResult
import com.example.landmarkremark.data.models.Note
import com.example.landmarkremark.data.models.User
import com.example.landmarkremark.data.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteVm @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : BaseViewModel() {
    private var _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() = _user

    private var _saveNoteSuccess = MutableLiveData<Event<Note>>()
    val saveNoteSuccess : LiveData<Event<Note>>
        get() = _saveNoteSuccess

    fun getUser() {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.getUser()
            if(result is NetworkResult.Success) {
             _user.postValue(result.data)
            }
        }
        registerJobFinish()
    }

    fun createNote(note: Note) {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.createNote(note)
            _saveNoteSuccess.postValue(Event(note))
        }
        registerJobFinish()
    }

    fun editNote(note: Note) {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.editNote(note)
            _saveNoteSuccess.postValue(Event(note))
        }
        registerJobFinish()
    }

    fun deleteNote(note: Note) {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = firebaseRepository.deleteNote(note)
            _saveNoteSuccess.postValue(Event(Note()))
        }
        registerJobFinish()
    }
}