package com.anafthdev.story.ui.new_story

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.anafthdev.story.R
import com.anafthdev.story.databinding.FragmentNewStoryBinding
import com.anafthdev.story.databinding.LoadingDialogBinding
import com.anafthdev.story.foundation.extension.getRotatedBitmap
import com.anafthdev.story.foundation.extension.reduceSize
import com.anafthdev.story.foundation.extension.toast
import com.anafthdev.story.foundation.extension.viewBinding
import com.anafthdev.story.foundation.util.UriUtil
import com.anafthdev.story.foundation.worker.WorkerUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NewStoryFragment : Fragment(R.layout.fragment_new_story) {

    @Inject lateinit var workManager: WorkManager

    private lateinit var progressDialog: Dialog

    private val viewModel: NewStoryViewModel by viewModels()
    private val binding: FragmentNewStoryBinding by viewBinding(FragmentNewStoryBinding::bind)

    /**
     * Temp uri yang digunakan untuk menampung gambar yang diambil dari kamera yang akan ditampilkan ke selected image
     */
    private var tempUri: Uri = Uri.EMPTY

    private val pickImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            UriUtil.uriToBitmap(requireContext(), uri)?.let(viewModel::setSelectedImageBitmap)
        }
    }

    private val takePictureResultLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            requireContext().contentResolver.openInputStream(tempUri)?.let { stream ->
                UriUtil.uriToBitmap(requireContext(), tempUri)?.let { bitmap ->
                    viewModel.setSelectedImageBitmap(bitmap.getRotatedBitmap(stream))
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        progressDialog = Dialog(context).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(
                LoadingDialogBinding.inflate(layoutInflater).apply {
                    tvTitle.text = context.getString(R.string.uploading_story)
                    tvText.text = context.getString(R.string.please_wait)
                }.root
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        // Observe upload worker, saat user mengklik upload,
        // work info akan di observe dengan kode dibawah
        lifecycleScope.launch {
            viewModel.uploadWorkId.asFlow().flatMapLatest { uuid ->
                workManager.getWorkInfoByIdFlow(uuid)
            }.filterNotNull().collectLatest { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        viewModel.setLoading(false)

                        findNavController().popBackStack()
                    }
                    WorkInfo.State.FAILED -> {
                        viewModel.setLoading(false)
                        context?.toast(
                            message = workInfo.outputData.getString(WorkerUtil.EXTRA_ERROR_MESSAGE) ?: "",
                            length = Toast.LENGTH_LONG
                        )
                    }
                    WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> {
                        viewModel.setLoading(true)
                    }
                    WorkInfo.State.CANCELLED -> {
                        viewModel.setLoading(false)
                        requireContext().toast(requireContext().getString(R.string.cancelled))
                    }
                    else -> {}
                }
            }
        }

        viewModel.selectedImageBitmap.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                binding.ivStory.visibility = View.VISIBLE
                binding.tvSelectAnImage.visibility = View.GONE
                binding.buttonRotateLeft.isEnabled = true
                binding.buttonRotateRight.isEnabled = true
                binding.materialCardView.strokeWidth = 0

                Glide.with(requireContext())
                    .load(bitmap)
                    .into(binding.ivStory)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) progressDialog.show() else progressDialog.dismiss()
        }
    }

    private fun initView() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        buttonCamera.setOnClickListener {
            tempUri = UriUtil.getUriFromCache(requireContext(), "story_${System.currentTimeMillis()}")

            takePictureResultLauncher.launch(tempUri)
        }

        buttonGallery.setOnClickListener {
            pickImageResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        buttonRotateLeft.setOnClickListener {
            viewModel.selectedImageBitmap.value?.let { bitmap ->
                viewModel.setSelectedImageBitmap(rotateImage(bitmap, -90))
            }
        }

        buttonRotateRight.setOnClickListener {
            viewModel.selectedImageBitmap.value?.let { bitmap ->
                viewModel.setSelectedImageBitmap(rotateImage(bitmap, 90))
            }
        }

        buttonUpload.setOnClickListener {
            if (viewModel.selectedImageBitmap.value == null) {
                requireContext().toast(requireContext().getString(R.string.please_select_an_image_first))
                return@setOnClickListener
            }

            if (tfDescription.text.toString().isBlank()) {
                requireContext().toast(requireContext().getString(R.string.please_enter_a_description))
                return@setOnClickListener
            }

            val uri = UriUtil.getUriFromCache(requireContext(), "story_${System.currentTimeMillis()}")

            viewModel.selectedImageBitmap.value?.let { bitmap ->
                requireContext().contentResolver.openOutputStream(uri)?.let { os ->
                    bitmap.reduceSize(1000000, os)

                    viewModel.upload(
                        uri = uri,
                        description = tfDescription.text.toString()
                    )
                }
            }
        }
    }
}