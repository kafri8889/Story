package com.anafthdev.story.ui.new_story

import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.anafthdev.story.databinding.FragmentNewStoryBinding
import com.anafthdev.story.foundation.util.UriUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewStoryFragment : Fragment() {

    private lateinit var binding: FragmentNewStoryBinding

    private val viewModel: NewStoryViewModel by viewModels()

    /**
     * Temp uri yang digunakan untuk mengambil gambar dari kamera yang akan ditampilkan ke selected image
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
        if (success) UriUtil.uriToBitmap(requireContext(), tempUri)?.let(viewModel::setSelectedImageBitmap)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedImageBitmap.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                binding.ivStory.visibility = View.VISIBLE
                binding.tvSelectAnImage.visibility = View.GONE
                binding.buttonRotateLeft.isEnabled = true
                binding.buttonRotateRight.isEnabled = true

                Glide.with(requireContext())
                    .load(bitmap)
                    .into(binding.ivStory)
            }
        }

        initView()
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
            val uri = UriUtil.getUriFromCache(requireContext(), "story_${System.currentTimeMillis()}")

            viewModel.selectedImageBitmap.value?.let { bitmap ->
                requireContext().contentResolver.openOutputStream(uri)?.let { os ->
                    bitmap.compress(CompressFormat.JPEG, 100, os)

                    // TODO: Use uri for upload to server
                }
            }
        }
    }
}