package com.hailm.mapinvitedemo.ui.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.databinding.FragmentNotificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : BaseFragment(R.layout.fragment_notification) {
    private val mBinding by viewBinding(FragmentNotificationBinding::bind)
    private val notificationViewModel: NotificationViewModel by viewModels()

    private lateinit var notificationAdapter: NotificationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewInstance()
    }

    private fun initViewInstance() {
        notificationViewModel.notificationList.observe(viewLifecycleOwner) {
            notificationAdapter.notificationList = it
        }

        with(mBinding) {
            notificationAdapter = NotificationAdapter()
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            rvNotification.apply {
                adapter = notificationAdapter
                layoutManager = linearLayoutManager
            }
            notificationViewModel.getListNotification()

            imgBack.setThrottleClickListener {
                findNavController().popBackStack()
            }
        }
    }
}

