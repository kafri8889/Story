package com.anafthdev.story.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.anafthdev.story.data.model.UserCredential
import com.anafthdev.story.data.model.body.LoginRequestBody
import com.anafthdev.story.data.repository.UserCredentialRepository
import com.anafthdev.story.foundation.common.EmailValidator
import com.anafthdev.story.foundation.common.PasswordValidator
import com.anafthdev.story.foundation.worker.Workers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userCredentialRepository: UserCredentialRepository,
    private val workManager: WorkManager
): ViewModel() {

    private val _loginWorkId = MutableLiveData<UUID>()
    val loginWorkId: LiveData<UUID> = _loginWorkId

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _showPassword = MutableLiveData(false)
    val showPassword: LiveData<Boolean> = _showPassword

    private val _enableLoginButton = MutableLiveData(false)
    val enableLoginButton: LiveData<Boolean> = _enableLoginButton

    private val _rememberMe = MutableLiveData(false)
    val rememberMe: LiveData<Boolean> = _rememberMe

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private fun validateEmailAndPassword() {
        val isEmailValid = EmailValidator().validate(email.value ?: "").isSuccess
        val isPasswordValid = PasswordValidator().validate(password.value ?: "").isSuccess
        _enableLoginButton.value = isEmailValid && isPasswordValid
    }

    fun setEmail(email: String) {
        _email.value = email
        validateEmailAndPassword()
    }

    fun setPassword(password: String) {
        _password.value = password
        validateEmailAndPassword()
    }

    fun setShowPassword(show: Boolean) {
        _showPassword.value = show
    }

    fun setRememberMe(remember: Boolean) {
        _rememberMe.value = remember
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun saveCredential(credential: UserCredential, onCompleted: () -> Unit) {
        viewModelScope.launch {
            userCredentialRepository.setAll(credential)
        }.invokeOnCompletion { onCompleted() }
    }

    fun login() {
        workManager.beginWith(
            Workers.login(
                LoginRequestBody(
                    email = email.value!!,
                    password = password.value!!
                )
            ).also { workRequest ->
                _loginWorkId.value = workRequest.id
            }
        ).enqueue()
    }
}