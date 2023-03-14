package com.br.photoscheme.teste.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.br.photoscheme.R
import com.br.photoscheme.teste.utils.LovePhotoPicasso
import com.bumptech.glide.Glide
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import java.io.ByteArrayOutputStream
import java.io.IOException

interface LoadBitmapResponse {
    fun success(bitmap: Bitmap)
    fun error()
    fun loading()
}

fun ImageView.load(
    url: String?,
    extras: ((RequestCreator) -> RequestCreator)? = null,
    callback: Callback? = null
) {
    if (!url.isNullOrBlank()) {
        LovePhotoPicasso.Picasso(context)
            .load(url)
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
            .placeholder(R.drawable.ic_unavailable_image)
            .into(this)
    }
}

fun loadBitmap(url: String, loadBitmapResponse: LoadBitmapResponse) {
    Picasso.get()
        .load(url)
        .into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) {
                    loadBitmapResponse.success(bitmap)
                }
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                loadBitmapResponse.error()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                loadBitmapResponse.loading()
            }
        })
}

// Essa função veio através da resposta dessa link no stackoverflow.
// https://stackoverflow.com/questions/18573774/how-to-reduce-an-image-file-size-before-uploading-to-a-server
fun Uri.downsizedImageBytes(context: Context, scaleDivider: Int? = null): ByteArray? {
    val SCALE_DIVIDER_THUMB = 5
    val MAX_WIDTH_SCALE = 2000

    return try {
        val fullBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        val useScaleDivider = scaleDivider ?: SCALE_DIVIDER_THUMB

        val scaleWidth = fullBitmap.width / useScaleDivider
        val scaleHeight = fullBitmap.height / useScaleDivider
        val imageRotation =
            if (fullBitmap.width > MAX_WIDTH_SCALE) exifToDegrees(ExifInterface.ORIENTATION_ROTATE_90) else 0

        getDownsizedImageBytes(fullBitmap, imageRotation, scaleWidth, scaleHeight)
    } catch (ioEx: IOException) {
        ioEx.printStackTrace()
        null
    }
}

private fun getDownsizedImageBytes(
    fullBitmap: Bitmap?,
    imageRotation: Int,
    scaleWidth: Int,
    scaleHeight: Int
): ByteArray? {
    var scaledBitmap =
        Bitmap.createScaledBitmap(fullBitmap!!, scaleWidth, scaleHeight, true)
    val arrayOutput = ByteArrayOutputStream()
    if (imageRotation != 0) scaledBitmap = getBitmapRotatedByDegree(scaledBitmap, imageRotation)

    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutput)
    return arrayOutput.toByteArray()
}

// O problema de imagens na horizontal foi resolvido através desse tópico
// https://stackoverflow.com/questions/39515637/camera-image-gets-rotated-when-i-upload-to-server/39515879#39515879
private fun getBitmapRotatedByDegree(bitmap: Bitmap, rotationDegree: Int): Bitmap? {
    val matrix = Matrix()
    matrix.preRotate(rotationDegree.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun exifToDegrees(rotation: Int): Int {
    if (rotation == ExifInterface.ORIENTATION_ROTATE_90) return 90
    return 0
}