package com.hailm.mapinvitedemo.ui.invite_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.util.Constants.NOT_ACCEPT
import com.hailm.mapinvitedemo.databinding.ItemUserInviteBinding

class InviteViewHolder constructor(private val binding: ItemUserInviteBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) = InviteViewHolder(
            ItemUserInviteBinding.inflate(inflater, parent, false)
        )
    }

    fun bind(model: UserInvite, pos: Int, onAccept: OnAccept?) {
        with(binding) {
            tvStt.text = (pos + 1).toString()
            tvPhoneInvite.text = model.userOne
            if (model.status == NOT_ACCEPT) {
                btnAccept.visibility = View.VISIBLE
                btnAccept.setBackgroundResource(R.drawable.bg_accept)
            } else {
                btnAccept.isClickable = false
            }

            btnAccept.setThrottleClickListener {
                onAccept?.invoke(model, pos)
            }
        }
    }
}
