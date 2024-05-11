package com.anafthdev.story.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.story.data.repository.UserCredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userCredentialRepository: UserCredentialRepository
): ViewModel() {


    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            userCredentialRepository.clear()
            onLogout()
        }
    }
}