package com.hailm.mapinvitedemo.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hailm.mapinvitedemo.databinding.ItemNotificationBinding

class NotificationViewHolder constructor(private val binding: ItemNotificationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) =
            NotificationViewHolder(ItemNotificationBinding.inflate(inflater, parent, false))
    }

    fun bind(model: NotificationUiModel) {
        with(binding) {
            tvContent.text = model.body
            tvZoneName.text = model.zoneName
            tvUpdateTime.text = model.createTime
        }
    }

}