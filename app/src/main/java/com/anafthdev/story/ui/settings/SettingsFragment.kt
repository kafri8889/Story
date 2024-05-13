package com.anafthdev.story.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.anafthdev.story.R
import com.anafthdev.story.data.Language
import com.anafthdev.story.databinding.ToolbarBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val root = ((listView.parent as FrameLayout).parent as LinearLayout)

        ToolbarBinding
            .inflate(LayoutInflater.from(requireContext()), root, false)
            .apply {
                toolbar.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
            .root
            .let { root.addView(it, 0) }

        viewModel.language.observe(viewLifecycleOwner) { language ->
            findPreference<ListPreference>("language")?.value = if (language == Language.Undefined) {
                Language.English.code
            } else language.code
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("logout")?.apply {
            setOnPreferenceClickListener {
                viewModel.logout {
                    findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToOnboardingFragment())
                }

                return@setOnPreferenceClickListener true
            }
        }

        findPreference<ListPreference>("language")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                var language = Language.Undefined
                val languages = resources.getStringArray(R.array.language_values)

                for (i in languages.indices) {
                    if (languages[i] == newValue.toString()) {
                        language = Language.entries[i]
                        break
                    }
                }

                viewModel.setLanguage(language)
                true
            }
        }
    }
}