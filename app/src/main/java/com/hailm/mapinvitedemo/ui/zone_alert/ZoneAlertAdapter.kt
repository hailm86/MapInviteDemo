package com.hailm.mapinvitedemo.ui.zone_alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hailm.mapinvitedemo.base.model.ZoneAlert

typealias OnClickItem = (ZoneAlert) -> Unit

class ZoneAlertAdapter : RecyclerView.Adapter<ZoneViewHolder>() {
    var zoneAlertList: List<ZoneAlert> = mutableListOf()
    var onClickItem: OnClickItem? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneViewHolder =
        ZoneViewHolder.create(
            LayoutInflater.from(parent.context), parent
        )

    override fun getItemCount(): Int = zoneAlertList.size

    override fun onBindViewHolder(holder: ZoneViewHolder, position: Int) {
        holder.bind(zoneAlertList[position], onClickItem)
    }
}
