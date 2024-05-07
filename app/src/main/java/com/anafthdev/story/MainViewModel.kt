package com.anafthdev.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.story.data.model.UserCredential
import com.anafthdev.story.data.repository.UserCredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userCredentialRepository: UserCredentialRepository
): ViewModel() {

    private val _userCredential = MutableLiveData<UserCredential?>(null)
    val userCredential: LiveData<UserCredential?> = _userCredential

    init {
        viewModelScope.launch {
            userCredentialRepository.userCredential.collectLatest { cred ->
                Timber.i("User credential: $cred")
                _userCredential.postValue(cred)
            }
        }
    }

}