package com.hailm.mapinvitedemo.ui.invite_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

typealias OnAccept = (UserInvite, Int) -> Unit

class InviteListAdapter : RecyclerView.Adapter<InviteViewHolder>() {

    var userList: List<UserInvite> = mutableListOf()
    var onAccept: OnAccept? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder =
        InviteViewHolder.create(
            LayoutInflater.from(parent.context), parent
        )

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        holder.bind(userList[position], position, onAccept)
    }
}
