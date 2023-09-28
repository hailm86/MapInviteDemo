package com.hailm.mapinvitedemo.base.extension

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

fun ImageView.loadDrawable(
    context: Context,
    @DrawableRes drawable: Int
) {
    Glide.with(context)
        .load(drawable)
        .into(this)
}

fun ImageView.loadUrl(
    url: String
) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.loadUrlWithCirCle(
    context: Context,
    url: String
) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .circleCrop()
        .into(this)
}
