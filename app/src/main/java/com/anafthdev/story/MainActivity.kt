package com.anafthdev.story

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.anafthdev.story.data.model.UserCredential
import com.anafthdev.story.databinding.ActivityMainBinding
import com.anafthdev.story.foundation.extension.viewBinding
import com.anafthdev.story.foundation.localized.LocalizedActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: LocalizedActivity() {

    private lateinit var navController: NavController
    private lateinit var splashScreen: SplashScreen

    private val viewModel: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen = installSplashScreen().apply {
            setKeepOnScreenCondition { true }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                    getColor(R.color.md_theme_surfaceContainer),
                    getColor(R.color.md_theme_surfaceContainer
                )
            ),
            navigationBarStyle = SystemBarStyle.auto(
                getColor(R.color.md_theme_background),
                getColor(R.color.md_theme_background)
            )
        )

        setContentView(binding.root)

        actionBar?.hide()

        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).findNavController()

        checkCredential()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setOnLocaleChangedListener {
            when (navController.currentDestination?.id) {
                R.id.onboarding_fragment, R.id.settingsFragment -> {
                    // Refresh fragment jika bahasa berubah
                    val id = navController.currentDestination?.id
                    navController.popBackStack(id!!,true)
                    navController.navigate(id)
                }
            }
        }
    }

    /**
     * Cek apakah credential user valid atau tidak.
     *
     * Fungsi ini hanya dipanggil sekali
     */
    private fun checkCredential() {
        viewModel.userCredential.observe(this, object: Observer<UserCredential?> {
            override fun onChanged(value: UserCredential?) {
                if (value != null) {
                    // Saat user masuk ke aplikasi, cek apakah sudah pernah login sebelumnya
                    // Jika sudah, maka cek apakah saat ini berada di halaman onboarding (start destination)
                    // Jika iya, maka langsung navigate ke halaman home
                    // Jika tidak, biarkan user ada di onboarding screen, supaya login ulang
                    if (value.isValid) {
                        if (navController.currentDestination?.id == R.id.onboarding_fragment) {
                            navController.navigate(R.id.action_onboarding_fragment_to_homeFragment)
                        }
                    }

                    viewModel.userCredential.removeObserver(this)
                    splashScreen.setKeepOnScreenCondition { false }
                }
            }
        })
    }
}