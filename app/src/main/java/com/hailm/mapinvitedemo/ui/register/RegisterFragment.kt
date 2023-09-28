package com.hailm.mapinvitedemo.ui.register

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.databinding.FragmentRegisterBinding
import com.hailm.mapinvitedemo.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : BaseFragment(R.layout.fragment_register), OnMapReadyCallback,
    GoogleMap.OnMapClickListener {

    companion object {
        @JvmStatic
        private val TAG = HomeFragment::class.java.simpleName
        private const val PERMISSIONS_REQUEST_LOCATION = 1000
    }

    private val mBinding by viewBinding(FragmentRegisterBinding::bind)
    private val registerViewModel: RegisterViewModel by viewModels()

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private val markerList = mutableListOf<Marker>() // Danh sách marker
    private var latCurrent = 0.0
    private var longCurrent = 0.0

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initViewInstance()
    }

    private fun initViewInstance() {
        with(mBinding) {
            registerViewModel.hasPhoneNumber.observe(viewLifecycleOwner) {
                if (!it) {
                    val phoneNumber = edtPhoneNumber.text.toString().trim()
                    val fullName = edtName.text.toString().trim()
                    val gender = edtGender.text.toString().trim()

                    val userData = hashMapOf(
                        "phoneNumber" to phoneNumber,
                        "fullName" to fullName,
                        "gender" to gender,
                        "avatar" to "dummy link demo",
                        "lat" to latCurrent.toString(),
                        "long" to longCurrent.toString()
                    )
                    registerViewModel.addNewUserToFirebase(userData)
                } else {
                    Toast.makeText(context, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show()
                }
            }
            registerViewModel.addNewUser.observe(viewLifecycleOwner) {
                if (it) {
                    Toast.makeText(context, "Đăng kí thành công", Toast.LENGTH_SHORT).show()
                }
            }

            btnRegister.setThrottleClickListener {
                val phoneNumber = edtPhoneNumber.text.toString().trim()
                registerViewModel.checkHasPhoneNumber(phoneNumber)
            }
        }
    }


    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(registerViewModel)
        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        // Yêu cầu quyền truy cập vị trí nếu chưa có
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true // Hiển thị nút "My Location" trên bản đồ
        } else {
            // Yêu cầu quyền truy cập vị trí từ người dùng
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onMapClick(p0: LatLng) {
        // Xóa tất cả các marker cũ
        clearMarkers()

        val marker = mMap.addMarker(MarkerOptions().position(p0).title("Marker"))

        // Lưu marker vào danh sách
        markerList.add(marker!!)

        latCurrent = p0.latitude
        longCurrent = p0.longitude
    }

    private fun clearMarkers() {
        // Lặp qua danh sách marker và xóa chúng khỏi bản đồ
        for (marker in markerList) {
            marker.remove()
        }

        // Xóa tất cả marker khỏi danh sách
        markerList.clear()
    }
}
