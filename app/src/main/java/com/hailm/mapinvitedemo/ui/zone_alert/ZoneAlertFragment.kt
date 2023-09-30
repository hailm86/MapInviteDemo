package com.hailm.mapinvitedemo.ui.zone_alert

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.NavMainDirections
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.databinding.FragmentZoneAlertBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ZoneAlertFragment : BaseFragment(R.layout.fragment_zone_alert) {
    private val mBinding by viewBinding(FragmentZoneAlertBinding::bind)
    private val zoneAlertViewModel: ZoneAlertViewModel by viewModels()

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var zoneAlertAdapter: ZoneAlertAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewInstance()
    }

    private fun initViewInstance() {
        zoneAlertViewModel.zoneAlertList.observe(viewLifecycleOwner) {
            zoneAlertAdapter.zoneAlertList = it
            zoneAlertAdapter.notifyItemRangeChanged(0, it.size)
        }

        with(mBinding) {
            zoneAlertAdapter = ZoneAlertAdapter()
            val linearLayoutManagerZone =
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            rvZoneAlert.apply {
                adapter = zoneAlertAdapter
                layoutManager = linearLayoutManagerZone
            }
            zoneAlertAdapter.onClickItem = { model ->
                findNavController().navigate(
                    NavMainDirections.actionGlobalCreateZoneFragment(
                        Constants.FROM_ZONE_ALERT_EDIT, model
                    )
                )
            }
            zoneAlertViewModel.getAllZoneAlert()


            imgBack.setThrottleClickListener {
                findNavController().popBackStack()
            }

            btnCreateZone.setThrottleClickListener {
                findNavController().navigate(
                    NavMainDirections.actionGlobalCreateZoneFragment(
                        Constants.FROM_ZONE_ALERT_CREATE, ZoneAlert()
                    )
                )
            }
        }
    }

    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(zoneAlertViewModel)
        }
}
