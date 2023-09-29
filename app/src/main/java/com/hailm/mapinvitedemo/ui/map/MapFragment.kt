package com.hailm.mapinvitedemo.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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
        const val GEOFENCE_RADIUS = 1500.0f
    }

    private val mBinding by viewBinding(FragmentMapBinding::bind)
    private val mapViewModel: MapViewModel by viewModels()

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient

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
            addMarkerToMap(it)
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

            btnZoneAlert.setThrottleClickListener {
                findNavController().navigate(NavMainDirections.actionGlobalZoneAlertFragment())
            }
        }
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
