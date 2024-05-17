package com.anafthdev.story.foundation.worker

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import com.anafthdev.story.data.model.body.LoginRequestBody
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginWorkerTest {

    private lateinit var context: Context

    @get:Rule
    var wmRule = WorkManagerTestRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testLoginWorker() {
        val request = Workers.login(
            LoginRequestBody(
                email = "luyuxiao@gmail.com",
                password = "LUyuxiao@1"
            )
        )

        wmRule.workManager.enqueue(request).result.get()
        wmRule.workManager.getWorkInfoById(request.id).get().let { result ->
            MatcherAssert.assertThat(result.state, `is`(WorkInfo.State.ENQUEUED))
        }
    }

}
