package com.hailm.mapinvitedemo.ui.zone_alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

typealias OnClickItem = (ZoneAlertUiModel) -> Unit
typealias OnDeleteZone = (ZoneAlertUiModel) -> Unit

class ZoneAlertAdapter : RecyclerView.Adapter<ZoneViewHolder>() {
    var zoneAlertList: List<ZoneAlertUiModel> = mutableListOf()
    var onClickItem: OnClickItem? = null
    var onDeleteZone: OnDeleteZone? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneViewHolder =
        ZoneViewHolder.create(
            LayoutInflater.from(parent.context), parent
        )

    override fun getItemCount(): Int = zoneAlertList.size

    override fun onBindViewHolder(holder: ZoneViewHolder, position: Int) {
        holder.bind(zoneAlertList[position], onClickItem, onDeleteZone)
    }
}
