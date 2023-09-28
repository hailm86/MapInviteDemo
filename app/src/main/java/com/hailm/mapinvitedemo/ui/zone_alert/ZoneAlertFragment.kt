package com.hailm.mapinvitedemo.ui.zone_alert

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.databinding.FragmentZoneAlertBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ZoneAlertFragment : BaseFragment(R.layout.fragment_zone_alert){
    private val mBinding by viewBinding(FragmentZoneAlertBinding::bind)
    private val zoneAlertViewModel: ZoneAlertViewModel by viewModels()

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewInstance()
    }

    private fun initViewInstance() {

    }

    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(zoneAlertViewModel)
        }
}
