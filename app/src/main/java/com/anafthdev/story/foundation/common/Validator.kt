package com.anafthdev.story.foundation.common

import android.content.Context
import androidx.annotation.StringRes

fun interface Validator<T> {

    fun validate(input: T): ValidatorResult

    open class ValidatorResult {

        val isSuccess: Boolean
            get() = this is Success

        val isFailure: Boolean
            get() = this is Failure

        val errMsg: String
            get() = if (this is Failure) msg else ""

        data object Success: ValidatorResult()
        data class Failure(val msg: String = ""): ValidatorResult()

        fun parseErrorMessage(context: Context): String {
            return if (errMsg.contains(AS_STRING_RES_ID)) {
                val split = errMsg.split('|')
                val resId = split[1].toInt()

                context.getString(resId)
            } else errMsg
        }

        companion object {
            private const val AS_STRING_RES_ID = "as_string_res_id"

            /**
             * If you want to display message from string resource
             */
            fun asStringResource(
                @StringRes resId: Int
            ): String {
                return "$AS_STRING_RES_ID|$resId"
            }
        }

    }


}