package com.hailm.mapinvitedemo.ui.zone_alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hailm.mapinvitedemo.R
import com.hailm.mapinvitedemo.base.extension.setThrottleClickListener
import com.hailm.mapinvitedemo.base.model.ZoneAlert
import com.hailm.mapinvitedemo.base.util.Constants
import com.hailm.mapinvitedemo.databinding.ItemZoneBinding

class ZoneViewHolder constructor(
    private val binding: ItemZoneBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(inflater: LayoutInflater, parent: ViewGroup) =
            ZoneViewHolder(ItemZoneBinding.inflate(inflater, parent, false))
    }

    fun bind(model: ZoneAlertUiModel, onClickItem: OnClickItem?, onDeleteZone: OnDeleteZone?) {
        with(binding) {
            tvZoneName.text = model.zoneName

            when (model.zoneType) {
                Constants.ZONE_SAFE -> background.setBackgroundResource(R.drawable.circle_background_safe)
                Constants.ZONE_DANGER -> background.setBackgroundResource(R.drawable.circle_background_danger)
                Constants.ZONE_ONE_TIME -> background.setBackgroundResource(R.drawable.circle_background_one_time)
            }

            itemView.setThrottleClickListener {
                onClickItem?.invoke(model)
            }

            btnDeleteZone.setThrottleClickListener {
                onDeleteZone?.invoke(model)
            }
        }
    }
}
