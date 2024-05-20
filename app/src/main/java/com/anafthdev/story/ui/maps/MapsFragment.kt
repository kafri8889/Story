package com.anafthdev.story.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anafthdev.story.R
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.databinding.FragmentMapsBinding
import com.anafthdev.story.foundation.extension.toast
import com.anafthdev.story.foundation.extension.viewBinding
import com.anafthdev.story.foundation.util.PermissionUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson

class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val binding: FragmentMapsBinding by viewBinding(FragmentMapsBinding::bind)
    private val args: MapsFragmentArgs by navArgs()

    /**
     * for [ACTION_PICK] only, marker when user pick a location
     */
    private var targetMarker: Marker? = null
    private var googleMap: GoogleMap? = null

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (!result.values.all { it }) {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setCancelable(false)
                setTitle(R.string.permission_rationale_location_title)
                setMessage(R.string.permission_rationale_location_message)
                setNegativeButton(requireContext().getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton(requireContext().getString(R.string.open_setting)) { _, _ ->
                    startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", requireContext().packageName, null)
                        }
                    )
                }
            }.show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (args.action == ACTION_VIEW) {
            Gson().fromJson(args.story, Story::class.java).let { story ->
                val imageLocation = LatLng(story.lat, story.lon)

                map.addMarker(
                    MarkerOptions()
                        .position(imageLocation)
                        .title(requireContext().getString(R.string.image_location))
                )

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(imageLocation, 15f))
            }
        } else {
            map.setOnMapClickListener { latLng ->
                targetMarker?.remove()
                targetMarker = null
                targetMarker = map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(requireContext().getString(R.string.image_location))
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initView() = with(binding) {
        map.getFragment<SupportMapFragment>().getMapAsync(this@MapsFragment)

        fabMyLocation.apply {
            isVisible = args.action == ACTION_PICK

            setOnClickListener {
                if (
                    !PermissionUtil.checkPermissionGranted(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    requestLocationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )

                    return@setOnClickListener
                }

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener { location ->
                    val latLng = LatLng(location.latitude, location.longitude)

                    targetMarker?.remove()
                    targetMarker = null
                    targetMarker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(requireContext().getString(R.string.image_location))
                    )

                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }

        fabSave.apply {
            isVisible = args.action == ACTION_PICK

            setOnClickListener {
                findNavController().run {
                    if (targetMarker != null) {
                        previousBackStackEntry?.savedStateHandle?.set(
                            ARG_LATLNG,
                            LatLng(
                                targetMarker!!.position.latitude,
                                targetMarker!!.position.longitude
                            )
                        )

                        popBackStack()
                    } else requireContext().toast(
                        requireContext().getString(R.string.pick_a_location)
                    )
                }
            }
        }
    }

    companion object {
        /**
         * View a story on the map from [Story.lat] and [Story.lon]
         */
        const val ACTION_VIEW = "com.anafthdev.story.ui.maps.ACTION_VIEW"

        /**
         * Pick a latitude and longitude
         */
        const val ACTION_PICK = "com.anafthdev.story.ui.maps.ACTION_PICK"

        /**
         * Key yang digunakan untuk menerima data (latitude dan longitude)
         */
        const val ARG_LATLNG = "com.anafthdev.story.ui.maps.ARG_LATLNG"
    }
}