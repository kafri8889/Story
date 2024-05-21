package com.anafthdev.story.foundation.ui

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anafthdev.story.R
import com.anafthdev.story.extension.launchFragmentInHiltContainer
import com.anafthdev.story.foundation.util.EspressoIdlingResourceUtil
import com.anafthdev.story.ui.login.LoginFragment
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResourceUtil.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResourceUtil.countingIdlingResource)
    }

    @Test
    fun performLogin() {
        launchFragmentInHiltContainer<LoginFragment>()

        onView(withId(R.id.tf_email))
            .perform(ViewActions.typeText("jujingyi@gmail.com"))

        onView(withId(R.id.tf_password))
            .perform(ViewActions.typeText("JUjingyi@1"))

        closeSoftKeyboard()

        onView(withId(R.id.button_login))
            .perform(ViewActions.click())
    }

}