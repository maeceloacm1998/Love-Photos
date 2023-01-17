package com.br.photoscheme.teste.holder

import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.br.photoscheme.R
import com.squareup.picasso.Picasso

@EpoxyModelClass
abstract class GridHolder : EpoxyModelWithHolder<GridHolder.SectionHolder>() {

    override fun bind(holder: SectionHolder) {
        super.bind(holder)
        holder.apply {
        }
    }

    inner class SectionHolder : EpoxyHolder() {
        lateinit var mGridLayout: GridLayout

        override fun bindView(itemView: View) {
            mGridLayout = itemView.findViewById(R.id.grid_layout)
        }
    }

    override fun getDefaultLayout(): Int {
        return R.layout.grid_layout_holder
    }
}