package com.anafthdev.story.foundation.common

import com.anafthdev.story.R

class EmailValidator: Validator<String> {

    override fun validate(input: String): Validator.ValidatorResult {
        if (input.isBlank()) return Validator.ValidatorResult.Failure(
            Validator.ValidatorResult.asStringResource(R.string.please_fill_in)
        )

        // ^: matches beginning of the string
        // []: matches any character in this set
        // \w: matches any word (alphanumeric and underscore)
        // -: matches "-" character
        // \\: escaped character
        // .: matches "." character
        // +: matches 1 or more of the preceding token.
        // @: matches "@" character

        return if (Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matches(input)) Validator.ValidatorResult.Success
        else Validator.ValidatorResult.Failure(Validator.ValidatorResult.asStringResource(R.string.email_not_valid))
    }
}