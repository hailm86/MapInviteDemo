package com.hailm.mapinvitedemo.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.hailm.mapinvitedemo.NavMainDirections
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    companion object {
        @JvmStatic
        private val TAG = HomeFragment::class.java.simpleName
    }

    // member
    private val mBinding by viewBinding(FragmentHomeBinding::bind)
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewInstance()
    }

    private fun initViewInstance() {
        homeViewModel.hasPhoneNumber.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(NavMainDirections.actionGlobalMapFragment())
//                homeViewModel.onNavigationHandled()
            } else {
                Toast.makeText(context, "Số điện thoại chưa được đăng kí", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        with(mBinding) {
            btnRegister.setThrottleClickListener {
                findNavController().navigate(NavMainDirections.actionGlobalRegisterFragment())
            }

            btnLogin.setThrottleClickListener {
                homeViewModel.checkHasPhoneNumber(edtPhoneNumber.text.toString().trim())
            }
        }
    }

    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(homeViewModel)
        }
}
