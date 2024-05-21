package com.anafthdev.story.foundation.util

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResourceUtil {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResourceUtil.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoIdlingResourceUtil.decrement() // Set app as idle.
    }
}