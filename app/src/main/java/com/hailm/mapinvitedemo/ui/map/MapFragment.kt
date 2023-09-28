package com.hailm.mapinvitedemo.ui.map

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
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.NOT_ACCEPT
import com.hailm.mapinvitedemo.databinding.FragmentMapBinding
import com.hailm.mapinvitedemo.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : BaseFragment(R.layout.fragment_map) {
    companion object {
        @JvmStatic
        private val TAG = HomeFragment::class.java.simpleName
    }

    private val mBinding by viewBinding(FragmentMapBinding::bind)
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewInstance()
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
        }
    }


    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(mapViewModel)
        }
}
