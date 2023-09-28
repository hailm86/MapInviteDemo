package com.hailm.mapinvitedemo.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.hailm.mapinvitedemo.NavMainDirections
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.databinding.FragmentSplashBinding
import com.hailm.mapinvitedemo.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    private val binding by viewBinding(FragmentSplashBinding::bind)
    private val mSplashViewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        activity.onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isNavigateUp = findNavController().navigateUp()
                    if (isNavigateUp) {
                        appNavigation.openSplashToHomeScreen()
                    } else {
                        activity.finish()
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewInstance()
    }

    private fun initViewInstance() {
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(NavMainDirections.actionGlobalHomeFragment(), null)
        }, timeDelay)
    }

    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(mSplashViewModel)
        }

    companion object {
        const val timeDelay = 2000L
    }

}
