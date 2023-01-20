package com.br.photoscheme.teste.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.br.photoscheme.databinding.ActivityPreviewPhotoBinding
import com.br.photoscheme.teste.constants.PhotoSchemeConstants
import com.br.photoscheme.teste.extensions.load
import com.br.photoscheme.teste.extensions.loadThumbnail
import com.br.photoscheme.teste.models.PhotoItem
import com.br.photoscheme.teste.models.asPhotoModel
import com.br.photoscheme.teste.models.asThumbModel
import com.br.photoscheme.teste.service.database.PhotoListDB
import com.br.photoscheme.teste.service.database.ThumbListDB
import com.br.photoscheme.teste.state.PhotoSchemeState
import com.br.photoscheme.teste.state.PreviewPhotoState
import com.br.photoscheme.teste.viewModel.PhotoSchemeViewModel
import com.br.photoscheme.teste.viewModel.PreviewPhotoViewModel

class PreviewPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewPhotoBinding
    private val viewModel by lazy { PreviewPhotoViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observers()

        fetchPhotoList()
        clickBackButtonListener()
    }

    private fun observers() {
        viewModel.photoUrl.observe(this) { previewPhotoState ->
            when(previewPhotoState) {
                is PreviewPhotoState.Success -> {
                    showPhoto(previewPhotoState.url)
                    binding.loadingLayout.loading.visibility = View.GONE
                }
                is PreviewPhotoState.Loading -> {
                    val url = intent?.getStringExtra(URL)
                    binding.photoBackground.loadThumbnail(url)
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
        val url = intent?.getStringExtra(URL)
        viewModel.fetchSpecificPhoto(getUrlId(url), PhotoSchemeConstants.PHOTO_PATH)
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

    companion object {
        const val URL = "photo_url"

        fun newInstance(context: Context, url: String): Intent {
            val intent = Intent(context, PreviewPhotoActivity::class.java)
            bundle(intent, url)
            return intent
        }

        private fun bundle(intent: Intent, url: String) {
            val bundle = Bundle().apply {
                putString(URL, url)
            }
            intent.putExtras(bundle)
        }
    }
}