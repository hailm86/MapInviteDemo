package com.hailm.mapinvitedemo

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.BaseActivity
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.base.model.constant.AppFragmentType
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.databinding.ActivityMainBinding
import com.hailm.mapinvitedemo.service.LocationTrackingService
import com.hailm.mapinvitedemo.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        @JvmStatic
        private val TAG = MainActivity::class.java.simpleName
    }

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val navController: NavController
        get() = findNavController(R.id.nav_host_fragment_activity_main)

    @IdRes
    var currentFragmentId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Đăng ký BroadcastReceiver để lắng nghe broadcast
        val filter = IntentFilter(Constants.LOCATION_UPDATE)
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY // Add this line
        registerReceiver(locationReceiver, filter, null, null)
        initView()
    }

    private fun initView() {
        val navView: BottomNavigationView = binding.bottomAppBar
        navView.itemIconTintList = null
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, args ->
            currentFragmentId = destination.id
            hideSoftKeyboard()
            val amFragmentType = AppFragmentType.fromDestinationId(destination.id)
            with(amFragmentType) {
                with(binding) {
                    if (!isDialog) bottomAppBar.isVisible = amFragmentType.showBottomNavigation
                }
                window.statusBarColor =
                    ContextCompat.getColor(this@MainActivity, amFragmentType.statusBar)
                window.navigationBarColor =
                    ContextCompat.getColor(this@MainActivity, amFragmentType.navigationBar)

            }
        }

        val serviceIntent = Intent(this, LocationTrackingService::class.java)
        startService(serviceIntent)

    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Constants.LOCATION_UPDATE) {
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                // Process location data here
                printLog("LocationUpdated ==> $latitude -- $longitude")
                mainViewModel.getListZone(LatLng(latitude, longitude), context)
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
    }

}
