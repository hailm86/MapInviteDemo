package com.hailm.mapinvitedemo.ui.zone_create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hailm.mapinvitedemo.ui.invite_list.UserInvite
import com.hailm.mapinvitedemo.ui.invite_list.UserInviteUiModel

typealias OnAddMember = (UserInviteUiModel) -> Unit

class AddMemberAdapter : RecyclerView.Adapter<AddMemberViewHolder>() {

    var memberList: List<UserInviteUiModel> = mutableListOf()
    var onAddMember: OnAddMember? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMemberViewHolder =
        AddMemberViewHolder.create(
            LayoutInflater.from(parent.context), parent
        )

    override fun getItemCount(): Int = memberList.size
    override fun onBindViewHolder(holder: AddMemberViewHolder, position: Int) {
        holder.bind(memberList[position], position, onAddMember)
    }
}
