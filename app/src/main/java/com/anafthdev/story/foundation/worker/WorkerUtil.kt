package com.anafthdev.story.foundation.worker

import androidx.work.ListenableWorker
import androidx.work.workDataOf
import timber.log.Timber
import java.net.SocketTimeoutException

object WorkerUtil {

    const val EXTRA_ERROR_MESSAGE = "error_message"
    const val EXTRA_OUTPUT = "output"

    /**
     * Try to work in a try-catch block.
     *
     * Catched exception:
     * - [SocketTimeoutException] => [ListenableWorker.Result.retry]
     * - [Exception] => [ListenableWorker.Result.failure]
     */
    inline fun tryWork(
        extraErrorMsg: String,
        block: () -> ListenableWorker.Result
    ): ListenableWorker.Result {
        return try {
            block()
        } catch (e: SocketTimeoutException) {
            Timber.e(e, e.message)
            ListenableWorker.Result.retry()
        } catch (e: Exception) {
            Timber.e(e, e.message)
            ListenableWorker.Result.failure(
                workDataOf(
                    extraErrorMsg to (e.message ?: "")
                )
            )
        }
    }

}