package com.hailm.mapinvitedemo.ui.invite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.BaseFragment
import com.hailm.mapinvitedemo.base.helper.viewBinding
import com.hailm.mapinvitedemo.databinding.FragmentInviteBinding
import com.hailm.mapinvitedemo.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteFragment : BaseFragment(R.layout.fragment_invite) {
    companion object {
        @JvmStatic
        private val TAG = HomeFragment::class.java.simpleName
    }

    private val mBinding by viewBinding(FragmentInviteBinding::bind)
    private val inviteViewModel: InviteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewInstance()
    }

    private fun initViewInstance() {

    }


    override val viewModelList: List<ViewModel>
        get() = super.viewModelList.toMutableList().apply {
            add(inviteViewModel)
        }
}
