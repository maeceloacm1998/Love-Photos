package com.br.photoscheme.teste.holder

import android.view.View
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.br.photoscheme.R
import com.br.photoscheme.teste.extensions.load
import com.br.photoscheme.teste.extensions.loadThumbnail
import com.bumptech.glide.Glide

@EpoxyModelClass
abstract class PhotoHolder: EpoxyModelWithHolder<PhotoHolder.SectionHolder>() {

    @EpoxyAttribute
    var url: String = ""

    override fun bind(holder: SectionHolder) {
        super.bind(holder)

       holder.photoImage.loadThumbnail(url)
    }

    inner class SectionHolder : EpoxyHolder() {
        lateinit var photoImage: ImageView

        override fun bindView(itemView: View) {
            photoImage = itemView.findViewById(R.id.photo_image)
        }
    }

    override fun getDefaultLayout(): Int {
        return R.layout.photo_holder
    }
}
