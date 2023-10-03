package com.hailm.mapinvitedemo.ui.zone_create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.databinding.ItemAddMemberBinding
import com.hailm.mapinvitedemo.ui.invite_list.UserInviteUiModel

class AddMemberViewHolder constructor(
    private val binding: ItemAddMemberBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) =
            AddMemberViewHolder(ItemAddMemberBinding.inflate(inflater, parent, false))
    }

    fun bind(model: UserInviteUiModel, pos: Int, onAddMember: OnAddMember?) {
        binding.tvStt.text = (pos + 1).toString()
        binding.tvPhoneInvite.text = model.userTwo

        if (model.status == Constants.ACCEPT) {
            binding.btnAdd.text = itemView.context.resources.getString(R.string.text_add)
            binding.btnAdd.isClickable = true
            binding.btnAdd.setBackgroundResource(R.drawable.bg_accept)
        } else {
            binding.btnAdd.text = itemView.context.resources.getString(R.string.text_pending)
            binding.btnAdd.isClickable = false
            binding.btnAdd.setBackgroundResource(R.drawable.bg_disable_button)
        }

        if (binding.btnAdd.text == itemView.context.resources.getString(R.string.text_add)) {
            binding.btnAdd.setThrottleClickListener {
                onAddMember?.invoke(model)
            }
        }
    }
}
