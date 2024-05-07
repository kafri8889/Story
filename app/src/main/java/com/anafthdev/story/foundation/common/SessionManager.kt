package com.anafthdev.story.foundation.common

import com.anafthdev.story.data.repository.UserCredentialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sesi manager yang digunakan untuk mendapatkan token.
 */
class SessionManager @Inject constructor(
    private val userCredentialRepository: UserCredentialRepository
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var _token: String? = null
    val token: String?
        get() = _token

    init {
        scope.launch {
            userCredentialRepository.userCredential.collectLatest { credential ->
                _token = credential.token
            }
        }
    }

}