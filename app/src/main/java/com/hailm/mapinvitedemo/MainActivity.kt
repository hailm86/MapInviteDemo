package com.hailm.mapinvitedemo

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hailm.mapinvitedemo.base.BaseActivity
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.base.model.constant.AppFragmentType
import com.hailm.mapinvitedemo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        @JvmStatic
        private val TAG = MainActivity::class.java.simpleName
    }

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val navController: NavController
        get() = findNavController(R.id.nav_host_fragment_activity_main)

    @IdRes
    var currentFragmentId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
    }
}
