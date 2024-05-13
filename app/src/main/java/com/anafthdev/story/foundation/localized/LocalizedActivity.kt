package com.anafthdev.story.foundation.localized

import android.content.res.Resources
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.anafthdev.story.data.Language
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
abstract class LocalizedActivity: AppCompatActivity() {

	private val viewModel: LocalizedViewModel by viewModels()
	private var currentLocale: Locale? = null
	private var listener: OnLocaleChangedListener? = null

	init {
		lifecycleScope.launch {
			lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
				viewModel.language
					.filterNotNull()
					.collectLatest { language ->
						val lang = checkLanguage(language)
						val newLocale = Locale(lang.code)

						Timber.d("Language changed from $language to $lang")

						if (lang != language && lang != Language.Undefined) {
							viewModel.setLanguage(lang)
						}

						if (newLocale.language != currentLocale?.language) {
							currentLocale = newLocale
							LocalizationUtil.applyLanguageContext(
								context = this@LocalizedActivity,
								locale = currentLocale
							)

							listener?.onChanged()
						}
					}
			}
		}
	}

	private fun checkLanguage(language: Language): Language {
		if (language == Language.Undefined) {
			// Get system primary language
			var locale = Resources.getSystem().configuration.locales[0]
			var lang = Language.English

			if (Language.contains(locale.language)) {
				lang = Language.fromCode(locale.language)!!
			} else locale = Locale(lang.code)

			currentLocale = locale

			return lang
		}

		return language
	}

	fun setOnLocaleChangedListener(mListener: OnLocaleChangedListener) {
		this.listener = mListener
	}

	fun removeOnLocaleChangedListener() {
		this.listener = null
	}

}