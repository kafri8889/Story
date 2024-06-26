package com.anafthdev.story.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.story.data.Language
import com.anafthdev.story.data.repository.UserCredentialRepository
import com.anafthdev.story.data.repository.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userCredentialRepository: UserCredentialRepository,
    private val userPreferenceRepository: UserPreferenceRepository
): ViewModel() {

    private val _language = MutableLiveData(Language.Undefined)
    val language: LiveData<Language> = _language

    init {
        viewModelScope.launch {
            userPreferenceRepository.userPreferences.collectLatest { pref ->
                _language.postValue(pref.language)
            }
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            userPreferenceRepository.setLanguage(language)
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            userCredentialRepository.clear()
            onLogout()
        }
    }
}