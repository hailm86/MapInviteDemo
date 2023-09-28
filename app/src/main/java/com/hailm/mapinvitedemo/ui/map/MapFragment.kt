package com.hailm.mapinvitedemo.ui.map

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hailm.mapinvitedemo.NavMainDirections
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.NOT_ACCEPT
import com.hailm.mapinvitedemo.databinding.FragmentMapBinding
import com.hailm.mapinvitedemo.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : BaseFragment(R.layout.fragment_map), OnMapReadyCallback {
    companion object {
        @JvmStatic
        private val TAG = HomeFragment::class.java.simpleName
        private const val PERMISSIONS_REQUEST_LOCATION = 1000
        const val GEOFENCE_RADIUS = 4000.0f
    }

    private val mBinding by viewBinding(FragmentMapBinding::bind)
    private val mapViewModel: MapViewModel by viewModels()

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
        radius = GEOFENCE_RADIUS,
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Yêu cầu quyền truy cập vị trí nếu chưa có
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // get from db
            mapViewModel.getAllUserInvite()

            mMap.isMyLocationEnabled = true // Hiển thị nút "My Location" trên bản đồ
            moveMapToCurrentLocation()

            // Đặt vị trí trung tâm bản đồ và zoom level
            val center = LatLng(geofenceData.latitude, geofenceData.longitude)
            val zoomLevel = 14f
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel))

            // Vẽ vòng tròn từ tâm với bán kính
            mMap.addCircle(
                CircleOptions()
                    .center(center)
                    .radius(geofenceData.radius.toDouble())
                    .strokeColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .fillColor(ContextCompat.getColor(context, R.color.random_6))
            )

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

    private fun initViewInstance() {
        mapViewModel.hasPhoneNumber.observe(viewLifecycleOwner) {
            when (it) {
                Constants.SUCCESS -> {
                    val phoneInvite = mBinding.edtPhoneNumber.text.toString().trim()
                    mapViewModel.checkInvitedUser(phoneInvite)
                }

                Constants.FAILURE -> {
                    Toast.makeText(
                        context,
                        "Số điện thoại này chưa được đăng kí",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        mapViewModel.hasInvited.observe(viewLifecycleOwner) {
            if (!it.first) {
                val phoneInvite = mBinding.edtPhoneNumber.text.toString().trim()
                val userInvite = hashMapOf(
                    "userOne" to it.second.toString(),
                    "userTwo" to phoneInvite,
                    "status" to NOT_ACCEPT
                )
                mapViewModel.inviteUserToFirebase(userInvite)
            } else {
                Toast.makeText(context, "Bạn đã mời số điện thoại này", Toast.LENGTH_SHORT).show()
            }
        }

        mapViewModel.inviteSuccess.observe(viewLifecycleOwner) {
            if (it == Constants.SUCCESS) {
                mBinding.edtPhoneNumber.setText("")
                Toast.makeText(context, "Mời thành công", Toast.LENGTH_SHORT).show()
            }
        }

        mapViewModel.listLatLong.observe(viewLifecycleOwner) {
//            addMarkerToMap(it)
        }

        with(mBinding) {
            imgBack.setThrottleClickListener {
                findNavController().popBackStack()
            }

            btnInvite.setThrottleClickListener {
                val phoneInvite = edtPhoneNumber.text.toString().trim()
                mapViewModel.checkHasPhoneNumber(phoneInvite)
            }

            btnInviteList.setThrottleClickListener {
                findNavController().navigate(NavMainDirections.actionGlobalInviteListFragment())
            }

            createGeofenceButton.setThrottleClickListener {
                createGeofence()
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

    private fun addMarkerToMap(coordinatesList: List<LatLng>) {
        for (latLng in coordinatesList) {
            mMap.addMarker(MarkerOptions().position(latLng).title("Marker"))
        }

        // Di chuyển bản đồ đến trung tâm danh sách tọa độ và thay đổi mức zoom
        val builder = LatLngBounds.Builder()
        for (latLng in coordinatesList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        val padding = 50 // Padding để đảm bảo tất cả các đánh dấu hiển thị
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(cameraUpdate)
    }


    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(mapViewModel)
        }
}
