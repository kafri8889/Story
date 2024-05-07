package com.anafthdev.story.foundation.common

import com.anafthdev.story.R
import com.anafthdev.story.foundation.extension.containsDigit
import com.anafthdev.story.foundation.extension.containsLowercase
import com.anafthdev.story.foundation.extension.containsUppercase

class PasswordValidator: Validator<String> {

    override fun validate(input: String): Validator.ValidatorResult {
        if (input.isBlank()) {
            return Validator.ValidatorResult.Failure(Validator.ValidatorResult.asStringResource(R.string.please_fill_in))
        }

        if (input.length < 8) {
            return Validator.ValidatorResult.Failure(Validator.ValidatorResult.asStringResource(R.string.below_min_length_exception_msg))
        }

        if (!input.containsDigit()) {
            return Validator.ValidatorResult.Failure(Validator.ValidatorResult.asStringResource(R.string.digit_missing_exception_msg))
        }

        if (!input.containsUppercase()) {
            return Validator.ValidatorResult.Failure(Validator.ValidatorResult.asStringResource(R.string.uppercase_missing_exception_msg))
        }

        if (!input.containsLowercase()) {
            return Validator.ValidatorResult.Failure(Validator.ValidatorResult.asStringResource(R.string.lowercase_missing_exception_msg))
        }

        return Validator.ValidatorResult.Success
    }
}