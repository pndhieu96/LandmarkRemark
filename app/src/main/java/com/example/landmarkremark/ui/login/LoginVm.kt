package com.example.landmarkremark.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.landmarkremark.base.BaseViewModel
import com.example.landmarkremark.base.Event
import com.example.landmarkremark.data.models.User
import com.example.landmarkremark.data.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVm @Inject constructor(
    private val authenticationFirebaseRepository: FirebaseRepository
) : BaseViewModel() {
    private var _user = MutableLiveData<Event<User>>()
    val user : LiveData<Event<User>>
        get() = _user

    fun login(
        email: String,
        password: String
    ) {
        showLoading(true)
        parentJob = viewModelScope.launch(handler) {
            val result = authenticationFirebaseRepository.signIn(email, password)
            _user.postValue(Event<User>(result.data))
        }
        registerJobFinish()
    }
}