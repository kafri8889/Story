package com.anafthdev.story.ui.settings

import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.anafthdev.story.R
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
    }
}