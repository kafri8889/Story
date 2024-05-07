package com.anafthdev.story.foundation.common

import com.anafthdev.story.R

class EmptyTextValidator: Validator<String> {

    override fun validate(input: String): Validator.ValidatorResult {
        return if (input.isBlank()) Validator.ValidatorResult.Failure(
            Validator.ValidatorResult.asStringResource(R.string.please_fill_in)
        ) else Validator.ValidatorResult.Success
    }

}