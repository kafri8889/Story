package com.anafthdev.story.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import com.anafthdev.story.data.model.body.RegisterRequestBody
import com.anafthdev.story.foundation.common.EmailValidator
import com.anafthdev.story.foundation.common.EmptyTextValidator
import com.anafthdev.story.foundation.common.PasswordValidator
import com.anafthdev.story.foundation.worker.Workers
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val workManager: WorkManager
): ViewModel() {

    private val _registerWorkId = MutableLiveData<UUID>()
    val registerWorkId: LiveData<UUID> = _registerWorkId

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email
    
    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _showPassword = MutableLiveData(false)
    val showPassword: LiveData<Boolean> = _showPassword

    private val _enableRegisterButton = MutableLiveData(false)
    val enableRegisterButton: LiveData<Boolean> = _enableRegisterButton

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private fun validateEmailPasswordAndUsername() {
        val isEmailValid = EmailValidator().validate(email.value ?: "").isSuccess
        val isPasswordValid = PasswordValidator().validate(password.value ?: "").isSuccess
        val isUsernameValid = EmptyTextValidator().validate(username.value ?: "").isSuccess
        _enableRegisterButton.value = isEmailValid && isPasswordValid && isUsernameValid
    }

    fun setUsername(username: String) {
        _username.value = username
        validateEmailPasswordAndUsername()
    }

    fun setEmail(email: String) {
        _email.value = email
        validateEmailPasswordAndUsername()
    }

    fun setPassword(password: String) {
        _password.value = password
        validateEmailPasswordAndUsername()
    }

    fun setShowPassword(show: Boolean) {
        _showPassword.value = show
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun register() {
        workManager.beginWith(
            Workers.register(
                RegisterRequestBody(
                    name = username.value!!,
                    email = email.value!!,
                    password = password.value!!
                )
            ).also { workRequest ->
                _registerWorkId.value = workRequest.id
            }
        ).enqueue()
    }
}