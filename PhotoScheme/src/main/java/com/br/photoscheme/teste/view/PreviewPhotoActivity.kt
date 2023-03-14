package com.br.photoscheme.teste.view

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.br.photoscheme.R
import com.br.photoscheme.databinding.ActivityPreviewPhotoBinding
import com.br.photoscheme.teste.constants.PhotoSchemeConstants
import com.br.photoscheme.teste.extensions.LoadBitmapResponse
import com.br.photoscheme.teste.extensions.load
import com.br.photoscheme.teste.extensions.loadBitmap
import com.br.photoscheme.teste.state.PreviewPhotoState
import com.br.photoscheme.teste.viewModel.PreviewPhotoViewModel
import java.io.FileOutputStream
import java.util.UUID

class PreviewPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewPhotoBinding
    private val viewModel by lazy { PreviewPhotoViewModel() }

    private val imageUrl by lazy { intent?.getStringExtra(URL) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observers()

        fetchPhotoList()
        clickBackButtonListener()
        handleDownloadButton()
    }

    private fun observers() {
        viewModel.photoUrl.observe(this) { previewPhotoState ->
            when (previewPhotoState) {
                is PreviewPhotoState.Success -> {
                    showPhoto(previewPhotoState.url)
                    binding.loadingLayout.loading.visibility = View.GONE
                }
                is PreviewPhotoState.Loading -> {
                    binding.loadingLayout.loading.visibility = View.VISIBLE
                }
                else -> {
                    binding.loadingLayout.loading.visibility = View.GONE
                    binding.errorLayout.error.visibility = View.VISIBLE
                    binding.errorLayout.retryBtn.setOnClickListener {
                        fetchPhotoList()
                    }
                }
            }
        }
    }

    private fun getUrlId(url: String?): String {
        url.let {
            val uri = Uri.parse(url).lastPathSegment
            val split = uri.toString().split("/")
            return split[split.size - 1]
        }
    }

    private fun fetchPhotoList() {
        viewModel.fetchSpecificPhoto(getUrlId(imageUrl), PhotoSchemeConstants.PHOTO_PATH)
        binding.loadingLayout.loading.visibility = View.VISIBLE
        binding.errorLayout.error.visibility = View.GONE
    }

    private fun clickBackButtonListener() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun showPhoto(url: String?) {
        url.let {
            binding.previewPhoto.load(url)
        }
    }

    private fun handleDownloadButton() {
        binding.download.setOnClickListener {
            saveInGallery()
        }
    }

    private fun saveInGallery() {
        imageUrl?.let {
            loadBitmap(it, object : LoadBitmapResponse {
                override fun success(bitmap: Bitmap) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        addImageToGallery(bitmap)
                    }
                }

                override fun error() {
                    Toast.makeText(
                        applicationContext, getString(R.string.error_image), Toast.LENGTH_SHORT
                    ).show()
                }

                override fun loading() {
                    Toast.makeText(
                        applicationContext, getString(R.string.loading_image), Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun addImageToGallery(bitmap: Bitmap): Uri? {
        Toast.makeText(
            applicationContext, getString(R.string.loading_gallery), Toast.LENGTH_SHORT
        ).show()

        val title = "anexo_${UUID.randomUUID()}"
        val resolver = applicationContext.contentResolver

        val pictureCollection = MediaStore.Images.Media
            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val pictureDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${title}.png")
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        val pictureContentUri = resolver.insert(pictureCollection, pictureDetails)

        pictureContentUri?.let { uri ->
            resolver.openFileDescriptor(uri, "w", null).use { parcelFileDescriptor ->
                parcelFileDescriptor?.let { file ->
                    val fileStream = FileOutputStream(file.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileStream)
                    Toast.makeText(
                        applicationContext, getString(R.string.success_gallery), Toast.LENGTH_SHORT
                    ).show()
                    fileStream.close()
                }
            }
        }

        pictureDetails.clear()
        pictureDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
        pictureContentUri?.let { resolver.update(it, pictureDetails, null, null) }
        return pictureContentUri
    }

    // Download Image Example
    private fun downloadItem(uri: Uri) {
        val title = "anexo_${UUID.randomUUID()}"

        val request = DownloadManager.Request(uri)
            .setTitle(title)
            .setAllowedOverRoaming(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

        Toast.makeText(
            this, R.string.progress_download, Toast.LENGTH_SHORT
        ).show()

        (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(
                    context, R.string.success_download, Toast.LENGTH_SHORT
                ).show()
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    companion object {
        const val URL = "photo_url"

        fun newInstance(context: Context, url: String) =
            Intent(context, PreviewPhotoActivity::class.java).apply {
                putExtra(URL, url)
            }.run {
                putExtras(this)
            }
    }
}