package com.hailm.mapinvitedemo.ui.zone_create

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.google.firebase.Timestamp
import com.google.firebase.messaging.FirebaseMessaging
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.LocationUtils
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.DOCUMENT_ID
import com.hailm.mapinvitedemo.base.util.Constants.GEOFENCE_RADIUS
import com.hailm.mapinvitedemo.base.util.Constants.MEMBER_LIST
import com.hailm.mapinvitedemo.databinding.FragmentCreateZoneBinding
import com.hailm.mapinvitedemo.ui.invite_list.UserInviteUiModel
import com.hailm.mapinvitedemo.ui.map.GeofenceData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateZoneFragment : BaseFragment(R.layout.fragment_create_zone), OnMapReadyCallback,
    GoogleMap.OnMapClickListener {

    private val mBinding by viewBinding(FragmentCreateZoneBinding::bind)
    private val createZoneViewModel: CreateZoneViewModel by viewModels()
    private val mArgs by navArgs<CreateZoneFragmentArgs>()
    private lateinit var bottomSheet: AddMemberBottomSheet
    private var currentZoom: Float = 0.0f

    @Inject
    lateinit var userProfileProvider: UserProfileProvider

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private val geofenceList = mutableListOf<Geofence>()
    private lateinit var geofencePendingIntent: PendingIntent
    private val markerListCenter = mutableListOf<Marker>()
    private val markerList = mutableListOf<Marker>()
    private var zoneType = Constants.ZONE_SAFE

    private lateinit var geofenceData: GeofenceData
    private var memberList = mutableListOf<UserInviteUiModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createChannel(context)
        initialData()
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geofencingClient = LocationServices.getGeofencingClient(context)
        bottomSheet = AddMemberBottomSheet()
        initViewInstance()
    }

    private fun initialData() {
        val initialLatitude = if (mArgs.fromTo == Constants.FROM_ZONE_ALERT_CREATE) {
            21.028215
        } else {
            mArgs.zoneAlert.zoneLat
        }

        val initialLongtitude = if (mArgs.fromTo == Constants.FROM_ZONE_ALERT_CREATE) {
            105.723288
        } else {
            mArgs.zoneAlert.zoneLong
        }

        currentZoom = if (mArgs.fromTo == Constants.FROM_ZONE_ALERT_CREATE) {
            14f
        } else {
            mArgs.zoneAlert.currentZoom
        }

        geofenceData = GeofenceData(
            id = "geofence_id",
            latitude = initialLatitude.toString().toDouble(),
            longitude = initialLongtitude.toString().toDouble(),
            radius = GEOFENCE_RADIUS,
            transitionTypes = Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )

        if (mArgs.fromTo == Constants.FROM_ZONE_ALERT_EDIT) {
            val zoneAlert = mArgs.zoneAlert
            when (zoneAlert.zoneType) {
                Constants.ZONE_SAFE -> mBinding.circleView.setBackgroundResource(R.drawable.circle_background_safe)
                Constants.ZONE_DANGER -> mBinding.circleView.setBackgroundResource(R.drawable.circle_background_danger)
                Constants.ZONE_ONE_TIME -> mBinding.circleView.setBackgroundResource(R.drawable.circle_background_one_time)
            }

            mBinding.edtZoneAlertName.setText(zoneAlert.zoneName)
        }
    }

    private fun initViewInstance() {
        createZoneViewModel.addZoneAlert.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        createZoneViewModel.memberList.observe(viewLifecycleOwner) {
            memberList = it
        }
        createZoneViewModel.getListMember()
        with(mBinding) {
            imgBack.setThrottleClickListener {
                findNavController().popBackStack()
            }

            createGeofenceButton.setThrottleClickListener {
//                createGeofence()

                if (edtZoneAlertName.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please enter zoneName", Toast.LENGTH_SHORT).show()
                    return@setThrottleClickListener
                }
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val deviceToken = task.result
                        saveZoneToFirebase(deviceToken)
                    } else {
                        // Xử lý trường hợp không thể lấy được token.
                        saveZoneToFirebase()
                    }
                }
            }

            btnDanger.setThrottleClickListener {
                zoneType = Constants.ZONE_DANGER
                circleView.setBackgroundResource(R.drawable.circle_background_danger)
            }

            btnSafe.setThrottleClickListener {
                zoneType = Constants.ZONE_SAFE
                circleView.setBackgroundResource(R.drawable.circle_background_safe)
            }

            btnOneTime.setThrottleClickListener {
                zoneType = Constants.ZONE_ONE_TIME
                circleView.setBackgroundResource(R.drawable.circle_background_one_time)
            }

            btnAddMember.setThrottleClickListener {
                val bundle = Bundle()
                bundle.putString(DOCUMENT_ID, mArgs.zoneAlert.documentId)
                bundle.putParcelableArrayList(MEMBER_LIST, ArrayList(memberList))
                bottomSheet.arguments = bundle
                bottomSheet.show(childFragmentManager, TAG)
            }
        }
    }

    private fun saveZoneToFirebase(deviceToken: String = "") {
        val zoneName = mBinding.edtZoneAlertName.text.toString().trim()
        val dateTime = Timestamp.now()
        val newUserIds = listOf<String>()
        val zoneData = hashMapOf(
            "zoneName" to zoneName,
            "zoneLat" to geofenceData.latitude.toString(),
            "zoneLong" to geofenceData.longitude.toString(),
            "zoneRadius" to geofenceData.radius.toString(),
            "zonePhoneNumber" to userProfileProvider.userPhoneNumber.toString(),
            "zoneType" to zoneType,
            "zoneMember" to newUserIds,
            "currentZoom" to currentZoom,
            "zoneDeviceToken" to deviceToken,
            "updateTime" to dateTime
        )

        if (mArgs.fromTo == Constants.FROM_ZONE_ALERT_CREATE) {
            createZoneViewModel.addZoneAlertToFirebase(zoneData)
        } else {
            createZoneViewModel.addEditZoneAlertToFirebase(
                zoneData,
                mArgs.zoneAlert.documentId.toString()
            )
        }

    }

    private fun createGeofence() {
        val geofence = Geofence.Builder().setRequestId(geofenceData.id)
            // Set the circular region of this geofence.
            .setCircularRegion(
                geofenceData.latitude, geofenceData.longitude, geofenceData.radius
            )
            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(geofenceData.transitionTypes).build()

        geofenceList.clear()
        geofenceList.add(geofence)

        val geofencingRequest = GeofencingRequest.Builder().addGeofences(geofenceList)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build()

//        geofencePendingIntent = getGeofencePendingIntent()
//
//        if (ActivityCompat.checkSelfPermission(
//                context, Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
//            addOnSuccessListener {
//                Toast.makeText(context, "Geofences added", Toast.LENGTH_SHORT).show()
//            }
//            addOnFailureListener { e ->
//            }
//        }
//    }

//    private fun getGeofencePendingIntent(): PendingIntent {
//        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
//        return PendingIntent.getBroadcast(
//            requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE
//        )
    }

    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(createZoneViewModel)
        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        // Yêu cầu quyền truy cập vị trí nếu chưa có
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true // Hiển thị nút "My Location" trên bản đồ
            moveMapToCurrentLocation()
            // Đặt vị trí trung tâm bản đồ và zoom level
            val center = LatLng(geofenceData.latitude, geofenceData.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, currentZoom))
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
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
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
            val newZoom = mMap.cameraPosition.zoom
            if (newZoom != currentZoom) {
                currentZoom = newZoom
            }

            val centerLatLng = mMap.cameraPosition.target
            val centerLatitude = centerLatLng.latitude
            val centerLongitude = centerLatLng.longitude

            geofenceData.latitude = centerLatitude
            geofenceData.longitude = centerLongitude

            // Vẽ vòng tròn từ tâm với bán kính
//            mMap.clear()
//            mMap.addCircle(
//                CircleOptions()
//                    .center(centerLatLng)
//                    .radius(geofenceData.radius.toDouble())
//                    .strokeColor(ContextCompat.getColor(context, R.color.colorPrimary))
//                    .fillColor(ContextCompat.getColor(context, R.color.random_2))
//            )

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

    //removing a geofence
    private fun removeGeofence() {

//        geofencingClient.removeGeofences(geofencePendingIntent).run {
//            addOnSuccessListener {
//                Toast.makeText(context, "Geofences removed", Toast.LENGTH_SHORT).show()
//
//            }
//            addOnFailureListener {
//                Toast.makeText(context, "Failed to remove geofences", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    override fun onMapClick(p0: LatLng) {
        // Xóa tất cả các marker cũ
        clearNewMarkers()
        val marker = mMap.addMarker(MarkerOptions().position(p0).title("Click"))

        // Lưu marker vào danh sách
        markerList.add(marker!!)

        if (isInsideGeofence(p0)) {
            Toast.makeText(context, "Geofence User entered geofence", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Geofence User exited geofence", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isInsideGeofence(newLatLng: LatLng): Boolean {
        for (geofence in geofenceList) {
            val geofenceLatLng = LatLng(geofenceData.latitude, geofenceData.longitude)

            // Tính khoảng cách giữa tọa độ mới và tọa độ của geofence
            val distance = LocationUtils.distanceBetween(newLatLng, geofenceLatLng)

            if (distance <= geofence.radius) {
                // Tọa độ mới nằm trong geofence
                return true
            }
        }
        // Tọa độ mới không nằm trong bất kỳ geofence nào
        return false
    }

    private fun clearNewMarkers() {
        // Lặp qua danh sách marker và xóa chúng khỏi bản đồ
        for (marker in markerList) {
            marker.remove()
        }

        // Xóa tất cả marker khỏi danh sách
        markerList.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeGeofence()
    }

    companion object {
        @JvmStatic
        private val TAG = CreateZoneFragment::class.java.simpleName
        private const val PERMISSIONS_REQUEST_LOCATION = 1000
    }
}
