package com.anafthdev.story.foundation.localized

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.story.data.Language
import com.anafthdev.story.data.repository.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalizedViewModel @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository
): ViewModel() {

    private val _language = MutableStateFlow(Language.Undefined)
    val language: StateFlow<Language> = _language

    init {
        viewModelScope.launch {
            userPreferenceRepository.userPreferences.collectLatest { pref ->
                _language.update { pref.language }
            }
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            userPreferenceRepository.setLanguage(language)
        }
    }

}