package com.hailm.mapinvitedemo.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter : RecyclerView.Adapter<NotificationViewHolder>() {

    var notificationList: List<NotificationUiModel> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder.create(
            LayoutInflater.from(parent.context), parent
        )

    override fun getItemCount(): Int = notificationList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }
}
