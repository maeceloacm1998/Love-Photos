package com.br.photoscheme.teste.holder

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.br.photoscheme.R

@EpoxyModelClass
abstract class UpdateNewPhotoHolder: EpoxyModelWithHolder<UpdateNewPhotoHolder.SectionHolder>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickUpdatePhotoListener: () -> Unit

    override fun bind(holder: SectionHolder) {
        super.bind(holder)
        holder.updatePhotoBtn.setOnClickListener {
            clickUpdatePhotoListener()
        }
    }

    inner class SectionHolder: EpoxyHolder() {
        lateinit var updatePhotoBtn: ConstraintLayout

        override fun bindView(itemView: View) {
            updatePhotoBtn = itemView.findViewById(R.id.update_photo_btn)
        }
    }

    override fun getDefaultLayout(): Int {
        return R.layout.update_new_photo_holder
    }
}