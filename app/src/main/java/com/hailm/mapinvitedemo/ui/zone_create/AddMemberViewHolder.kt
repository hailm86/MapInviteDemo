package com.hailm.mapinvitedemo.ui.zone_create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.databinding.ItemAddMemberBinding
import com.hailm.mapinvitedemo.ui.invite_list.UserInvite

class AddMemberViewHolder constructor(
    private val binding: ItemAddMemberBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) =
            AddMemberViewHolder(ItemAddMemberBinding.inflate(inflater, parent, false))
    }

    fun bind(model: UserInvite, pos: Int, onAddMember: OnAddMember?) {
        binding.tvStt.text = (pos + 1).toString()
        binding.tvPhoneInvite.text = model.userTwo

        binding.btnAdd.setThrottleClickListener {
            onAddMember?.invoke(model)
        }
    }
}