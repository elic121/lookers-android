package com.example.lookers.view.activity

import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.lookers.databinding.ActivityImageViewBinding
import com.example.lookers.view.base.BaseActivity
import timber.log.Timber

class ImageViewActivity : BaseActivity<ActivityImageViewBinding>(ActivityImageViewBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setIntent()
    }

    private fun setIntent() {
        intent.getStringExtra("imageUrl")?.let {
            Timber.d("imageUrl: $it")
            Glide
                .with(this)
                .load(it)
                .into(binding.ivImage)
        }
    }
}
