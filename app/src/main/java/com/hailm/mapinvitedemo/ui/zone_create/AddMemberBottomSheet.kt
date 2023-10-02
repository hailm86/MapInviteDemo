package com.hailm.mapinvitedemo.ui.zone_create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.hailm.mapinvitedemo.base.cache.UserProfileProvider
import com.hailm.mapinvitedemo.base.extension.printLog
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.databinding.BottomSheetAddMemberBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddMemberBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddMemberBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Cannot access view after view destroyed or before view creation")
    private val viewModel by activityViewModels<CreateZoneViewModel>()

    @Inject
    lateinit var profileProvider: UserProfileProvider

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var addMemberAdapter: AddMemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val modalBehavior = (dialog as? BottomSheetDialog)?.behavior
        modalBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        modalBehavior?.skipCollapsed = true

        initViewInstance()
    }

    private fun initViewInstance() {
        val dataReceived = arguments?.getString("documentId")

        viewModel.memberList.observe(viewLifecycleOwner) {
            addMemberAdapter.memberList = it
        }

        viewModel.hasZoneMember.observe(viewLifecycleOwner) {
            if (!it.first) {
                viewModel.addMemberToZone(
                    it.second,
                    dataReceived.toString(),
                    "NameTest"
                )
                Toast.makeText(context, "add ${it.second}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getListMember()

        with(binding) {
            addMemberAdapter = AddMemberAdapter()
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            rvMemberList.apply {
                adapter = addMemberAdapter
                layoutManager = linearLayoutManager
            }

            addMemberAdapter.onAddMember = {
                viewModel.checkHasPhoneNumber(dataReceived.toString(), it.userTwo.toString())
            }

            btnClose.setThrottleClickListener {
                dialog?.dismiss()
            }


        }
    }

}
