package com.anafthdev.story.ui.new_story

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewStoryViewModel @Inject constructor(
    private val workManager: WorkManager
): ViewModel() {

    /**
     * Bitmap/gambar yang akan ditampilkan pada ImageView
     */
    private val _selectedImageBitmap = MutableLiveData<Bitmap?>(null)
    val selectedImageBitmap: MutableLiveData<Bitmap?> = _selectedImageBitmap

    fun setSelectedImageBitmap(bitmap: Bitmap) {
        _selectedImageBitmap.value = bitmap
    }

}