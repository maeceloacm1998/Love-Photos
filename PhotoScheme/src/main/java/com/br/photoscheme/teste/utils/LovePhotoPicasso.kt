package com.br.photoscheme.teste.utils

import android.content.Context
import com.squareup.picasso.Picasso

class LovePhotoPicasso {
    companion object {
        fun Picasso(context: Context): Picasso {
            return Picasso.Builder(context.applicationContext).build()
        }
    }
}