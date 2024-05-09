package com.anafthdev.story.ui.new_story

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import com.anafthdev.story.foundation.worker.Workers
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewStoryViewModel @Inject constructor(
    private val workManager: WorkManager
): ViewModel() {

    private val _uploadWorkId = MutableLiveData<UUID>()
    val uploadWorkId: LiveData<UUID> = _uploadWorkId

    /**
     * Bitmap/gambar yang akan ditampilkan pada ImageView
     */
    private val _selectedImageBitmap = MutableLiveData<Bitmap?>(null)
    val selectedImageBitmap: MutableLiveData<Bitmap?> = _selectedImageBitmap

    /**
     * Status loading saat mengupload story
     */
    private val _isLoading = MutableLiveData(false)
    val isLoading: MutableLiveData<Boolean> = _isLoading

    fun setSelectedImageBitmap(bitmap: Bitmap) {
        _selectedImageBitmap.value = bitmap
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun upload(uri: Uri, description: String) {
        _isLoading.value = true

        workManager.enqueue(
            Workers.postStory(uri, description).also {
                _uploadWorkId.value = it.id
            }
        )
    }

}