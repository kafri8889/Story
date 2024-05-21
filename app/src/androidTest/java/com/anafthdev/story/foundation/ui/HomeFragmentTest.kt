package com.anafthdev.story.foundation.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.anafthdev.story.R
import com.anafthdev.story.data.datasource.remote.ApiClient
import com.anafthdev.story.extension.launchFragmentInHiltContainer
import com.anafthdev.story.foundation.extension.JsonConverter
import com.anafthdev.story.foundation.util.EspressoIdlingResourceUtil
import com.anafthdev.story.ui.home.HomeFragment
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiClient.API_BASE_URL_TEST = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResourceUtil.countingIdlingResource)
    }
    @After
    fun tearDown() {
        mockWebServer.shutdown()
        ApiClient.API_BASE_URL_TEST = null
        IdlingRegistry.getInstance().unregister(EspressoIdlingResourceUtil.countingIdlingResource)
    }

    @Test
    fun testHomeFragment_Success() {
        launchFragmentInHiltContainer<HomeFragment>()

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))

        mockWebServer.enqueue(mockResponse)

        Espresso.onView(ViewMatchers.withId(R.id.rv_stories))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText("Ju Jingyi"))
                )
            )
    }

}