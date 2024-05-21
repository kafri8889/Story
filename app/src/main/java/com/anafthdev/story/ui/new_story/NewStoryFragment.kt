package com.anafthdev.story.ui.new_story

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.exifinterface.media.ExifInterface
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
import com.anafthdev.story.foundation.util.PermissionUtil
import com.anafthdev.story.foundation.util.UriUtil
import com.anafthdev.story.foundation.worker.WorkerUtil
import com.anafthdev.story.ui.maps.MapsFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: NewStoryViewModel by viewModels()
    private val binding: FragmentNewStoryBinding by viewBinding(FragmentNewStoryBinding::bind)

    /**
     * Temp uri yang digunakan untuk menampung gambar yang diambil dari kamera yang akan ditampilkan ke selected image
     */
    private var tempUri: Uri = Uri.EMPTY

    /**
     * Berfungsi untuk mengetahui apakah popBackStack() sudah pernah dipanggil atau belum
     */
    private var hasPopped = false

    private val pickImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            UriUtil.uriToBitmap(requireContext(), uri)?.let(viewModel::setSelectedImageBitmap)
            requireContext().contentResolver.openInputStream(uri)?.let { stream ->
                ExifInterface(stream).latLong?.let {
                    viewModel.setLatLng(LatLng(it[0], it[1]))
                }
            }
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
                ExifInterface(stream).latLong?.let {
                    viewModel.setLatLng(LatLng(it[0], it[1]))
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (
            !PermissionUtil.checkPermissionGranted(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) initFusedLocation(context)

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

                        // Mencegah pemanggilan popBackStack() lebih dari satu kali
                        if (!hasPopped) {
                            findNavController().popBackStack()
                            hasPopped = true
                        }
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

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<LatLng>(MapsFragment.ARG_LATLNG)
            ?.observe(viewLifecycleOwner) { latlng ->
                viewModel.setLatLng(LatLng(latlng.latitude, latlng.longitude))
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

        viewModel.latLng.observe(viewLifecycleOwner) { latLng ->
            binding.tvLatitude.text = requireContext().getString(
                R.string.latitude,
                latLng?.latitude?.toString() ?: requireContext().getString(R.string.unknown)
            )
            binding.tvLongitude.text = requireContext().getString(
                R.string.longitude,
                latLng?.longitude?.toString() ?: requireContext().getString(R.string.unknown)
            )
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

        switchEnableLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val latLng = viewModel.latLng.value

                rlLocationInfo.visibility = View.VISIBLE
                binding.tvLatitude.text = requireContext().getString(
                    R.string.latitude,
                    latLng?.latitude?.toString() ?: requireContext().getString(R.string.unknown)
                )
                binding.tvLongitude.text = requireContext().getString(
                    R.string.longitude,
                    latLng?.longitude?.toString() ?: requireContext().getString(R.string.unknown)
                )
            } else {
                rlLocationInfo.visibility = View.GONE
            }
        }

        buttonChangeLocation.setOnClickListener {
            findNavController().navigate(
                NewStoryFragmentDirections.actionNewStoryFragmentToMapsFragment(
                    action = MapsFragment.ACTION_PICK,
                    storyIds = emptyArray()
                )
            )
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

            if (switchEnableLocation.isChecked && viewModel.latLng.value == null) {
                requireContext().toast(requireContext().getString(R.string.please_pick_a_location))
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

    @SuppressLint("MissingPermission")
    private fun initFusedLocation(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context).apply {
            getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location ->
                    // In some rare situations location can be null
                    if (location != null) {
                        viewModel.setLatLng(LatLng(location.latitude, location.longitude))
                    }
                }
        }
    }
}