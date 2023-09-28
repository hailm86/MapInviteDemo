package com.hailm.mapinvitedemo.ui.zone_create

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.databinding.FragmentCreateZoneBinding
import com.hailm.mapinvitedemo.ui.map.GeofenceData
import com.hailm.mapinvitedemo.ui.map.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateZoneFragment : BaseFragment(R.layout.fragment_create_zone), OnMapReadyCallback {

    private val mBinding by viewBinding(FragmentCreateZoneBinding::bind)
    private val createZoneViewModel: CreateZoneViewModel by viewModels()

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private val geofenceList = mutableListOf<Geofence>()
    private lateinit var geofencePendingIntent: PendingIntent
    private val markerListCenter = mutableListOf<Marker>() // Danh sách marker

    private val geofenceData = GeofenceData(
        id = "geofence_id",
        latitude = 21.028215,
        longitude = 105.723288,
        radius = MapFragment.GEOFENCE_RADIUS,
        transitionTypes = Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geofencingClient = LocationServices.getGeofencingClient(context)
        initViewInstance()
    }

    private fun initViewInstance() {
        with(mBinding){
            imgBack.setThrottleClickListener {
                findNavController().popBackStack()
            }

            createGeofenceButton.setThrottleClickListener {
                createGeofence()
            }
        }
    }

    private fun createGeofence() {
        val geofence = Geofence.Builder()
            .setRequestId(geofenceData.id)
            // Set the circular region of this geofence.
            .setCircularRegion(
                geofenceData.latitude,
                geofenceData.longitude,
                geofenceData.radius
            )
            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(geofenceData.transitionTypes)
            .build()

        geofenceList.add(geofence)

        val geofencingRequest = GeofencingRequest.Builder()
            .addGeofences(geofenceList)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()

        geofencePendingIntent = getGeofencePendingIntent()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                mBinding.statusTextView.text = "Geofence created successfully"
            }
            addOnFailureListener { e ->
                mBinding.statusTextView.text = "Failed to create geofence"
            }
        }
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(createZoneViewModel)
        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Yêu cầu quyền truy cập vị trí nếu chưa có
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true // Hiển thị nút "My Location" trên bản đồ
            moveMapToCurrentLocation()
            // Đặt vị trí trung tâm bản đồ và zoom level
            val center = LatLng(geofenceData.latitude, geofenceData.longitude)
            val zoomLevel = 14f
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel))
            handleCameraMoveCenter()

        } else {
            // Yêu cầu quyền truy cập vị trí từ người dùng
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    private fun moveMapToCurrentLocation() {
        // Lấy vị trí hiện tại của thiết bị
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("My Location"))
            }
        }
    }

    private fun handleCameraMoveCenter() {
        mMap.setOnCameraIdleListener {
            // Đây là nơi bạn có thể đặt mã cần được thực thi khi bản đồ không còn di chuyển (chuyển động)
            // Ví dụ: gửi tọa độ trung tâm mới đến một hàm xử lý
            val centerLatLng = mMap.cameraPosition.target
            val centerLatitude = centerLatLng.latitude
            val centerLongitude = centerLatLng.longitude

            // Gọi hàm xử lý với tọa độ trung tâm mới
            handleCameraMove(centerLatitude, centerLongitude)
        }
    }

    private fun handleCameraMove(centerLatitude: Double, centerLongitude: Double) {
        // Xóa tất cả các marker cũ
        clearMarkers()
        val centerLatLngCurrent = LatLng(centerLatitude, centerLongitude)
        val marker = mMap.addMarker(
            MarkerOptions().position(centerLatLngCurrent).title("$centerLatitude, $centerLongitude")
        )
        markerListCenter.add(marker!!)
    }

    private fun clearMarkers() {
        for (marker in markerListCenter) {
            marker.remove()
        }
        markerListCenter.clear()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 1000
        const val GEOFENCE_RADIUS = 4000.0f
    }
}
