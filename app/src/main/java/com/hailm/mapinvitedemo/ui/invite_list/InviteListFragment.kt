package com.hailm.mapinvitedemo.ui.invite_list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.base.util.Constants.ACCEPT
import com.hailm.mapinvitedemo.databinding.FragmentInviteListBinding
import com.hailm.mapinvitedemo.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteListFragment : BaseFragment(R.layout.fragment_invite_list) {
    companion object {
        @JvmStatic
        private val TAG = HomeFragment::class.java.simpleName
    }

    private val mBinding by viewBinding(FragmentInviteListBinding::bind)
    private val inviteListViewModel: InviteListViewModel by viewModels()

    private lateinit var mAdapter: InviteListAdapter

    private var userList = mutableListOf<UserInvite>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewInstance()
    }

    private fun initViewInstance() {
        inviteListViewModel.userInviteList.observe(viewLifecycleOwner) {
            userList.clear()
            userList.addAll(it)
            mAdapter.userList = userList
            mAdapter.notifyItemRangeChanged(0, it.size)
        }

        inviteListViewModel.accepted.observe(viewLifecycleOwner) {
            if (it.first == Constants.SUCCESS) {
                userList[it.second].status = ACCEPT
                mAdapter.notifyItemChanged(it.second)
            }
        }

        with(mBinding) {

            mAdapter = InviteListAdapter()
            mAdapter.onAccept = { model, pos ->
                inviteListViewModel.updateStatus(model, pos)
            }

            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rvUserInvite.apply {
                adapter = mAdapter
                layoutManager = linearLayoutManager
            }
            inviteListViewModel.getListInvite()

            imgBack.setThrottleClickListener {
                findNavController().popBackStack()
            }
        }
    }


    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(inviteListViewModel)
        }
}
