package com.example.landmarkremark.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.landmarkremark.data.models.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

abstract class BaseViewModel : ViewModel() {
    private var _exception = MutableLiveData<Event<String>>()
    val exception : LiveData<Event<String>>
        get() = _exception

    var isLoading = MutableLiveData<Event<Boolean>>()
        protected set

    var parentJob: Job? = null
        protected set

    val handler = CoroutineExceptionHandler { _, exception ->
        _exception.postValue(Event<String>(exception.message ?: "An error occurred"))
    }

    protected fun showLoading(isShow: Boolean) {
        isLoading.postValue(Event(isShow))
    }

    protected fun registerJobFinish(){
        parentJob?.invokeOnCompletion {
            showLoading(false)
        }
    }
}