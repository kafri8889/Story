package com.anafthdev.story.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anafthdev.story.R
import com.anafthdev.story.databinding.FragmentOnboardingBinding
import com.anafthdev.story.foundation.extension.viewBinding

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding: FragmentOnboardingBinding by viewBinding(FragmentOnboardingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_onboarding_fragment_to_register_fragment)
        }

        binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_onboarding_fragment_to_loginFragment)
        }
    }
}