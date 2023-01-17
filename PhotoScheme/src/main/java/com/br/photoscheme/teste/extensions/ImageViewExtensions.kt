package com.br.photoscheme.teste.extensions

import android.widget.ImageView
import com.br.photoscheme.R
import com.br.photoscheme.teste.utils.LovePhotoPicasso
import com.bumptech.glide.Glide
import com.squareup.picasso.Callback
import com.squareup.picasso.RequestCreator

fun ImageView.load(
    url: String?,
    extras: ((RequestCreator) -> RequestCreator)? = null,
    callback: Callback? = null
) {
    if (!url.isNullOrBlank()) {
        LovePhotoPicasso.Picasso(context)
            .load(url).resize(300, 300).centerCrop()
            .placeholder(R.drawable.ic_unavailable_image)
            .apply {
                extras?.invoke(this)
            }
            .into(this, callback)
    }
}

fun ImageView.loadThumbnail(url: String?) {
    if (!url.isNullOrBlank()) {
        Glide.with(context)
            .load(url)
            .thumbnail(0.1f)
            .centerCrop()
            .placeholder(R.drawable.ic_unavailable_image)
            .into(this)
    }
}
