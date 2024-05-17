package com.anafthdev.story.foundation.common

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PasswordValidatorTest {

    @Test
    fun testValidPassword() {
        assertTrue(PasswordValidator().validate("Mypassword123").isSuccess)
        assertTrue(PasswordValidator().validate("01 Jan 2024").isSuccess)
        assertTrue(PasswordValidator().validate("Hello1@World2").isSuccess)
        assertTrue(PasswordValidator().validate("#@Password123$@3&").isSuccess)
    }

    @Test
    fun testInvalidPassword() {
        assertTrue(PasswordValidator().validate("").isFailure)
        assertTrue(PasswordValidator().validate("admin").isFailure)
        assertTrue(PasswordValidator().validate("admin123").isFailure)
        assertTrue(PasswordValidator().validate("PASSWORD123").isFailure)
        assertTrue(PasswordValidator().validate("ADMINpassword").isFailure)
    }

}