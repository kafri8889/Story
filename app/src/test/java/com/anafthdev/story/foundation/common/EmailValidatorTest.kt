package com.anafthdev.story.foundation.common

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmailValidatorTest {

    @Test
    fun emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(EmailValidator().validate("john.mclean@examplepetstore.com").isSuccess)
        assertTrue(EmailValidator().validate("jujingyi@gmail.com").isSuccess)
        assertTrue(EmailValidator().validate("anafthdev@student.gunadarma.ac.id").isSuccess)
        assertTrue(EmailValidator().validate("charlie123@school.edu").isSuccess)
        assertTrue(EmailValidator().validate("bob.builder@my-work.net").isSuccess)
        assertTrue(EmailValidator().validate("jane-doe_123@sub.domain.co").isSuccess)
        assertTrue(EmailValidator().validate("username@sub_domain.com").isSuccess)
    }

    @Test
    fun emailValidator_WrongEmailSimple_ReturnsFalse() {
        assertFalse(EmailValidator().validate("username@domain..com").isSuccess)
        assertFalse(EmailValidator().validate("special&char@domain.com").isSuccess)
        assertFalse(EmailValidator().validate("spaces in address@domain.com").isSuccess)
        assertFalse(EmailValidator().validate("username@domain-with-too-long-extension.corporation").isSuccess)
        assertFalse(EmailValidator().validate("invalid-characters-in-domain@domain!com").isSuccess)
        assertFalse(EmailValidator().validate("@domain.com").isSuccess)
        assertFalse(EmailValidator().validate("missing-at-symbol.com").isSuccess)
    }

}