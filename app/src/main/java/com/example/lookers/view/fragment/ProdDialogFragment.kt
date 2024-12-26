package com.example.lookers.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.lookers.databinding.FragmentProdDialogBinding
import com.example.lookers.model.entity.prod.ProdInfo
import com.example.lookers.util.convertTime
import com.example.lookers.util.goToActivity
import com.example.lookers.view.activity.ImageViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProdDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentProdDialogBinding
    private lateinit var prod: ProdInfo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProdDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            prod = it.getParcelable(ARG_PROD_INFO)!!
            setInfo()
        }

        setListeners()
    }

    private fun setListeners() {
        binding.ivProdImage.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putString("imageUrl", prod.imageUrl)
                }

            requireContext().goToActivity(ImageViewActivity::class.java, extras = bundle)
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes =
                attributes?.apply {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }
        }
    }

    private fun setInfo() {
        binding.tvProdName.text = prod.prodNames.getOrNull(0)?.prodName ?: "없음"
        if (binding.tvProdName.text == "None") {
            binding.tvProdName.text = "없음"
        }

        binding.tvProdType.text = prod.prodType.toString()
        binding.tvProdHistory.text = convertTime(prod.updatedAt)

        context?.let {
            Glide
                .with(it)
                .load(prod.imageUrl)
                .fitCenter()
                .into(binding.ivProdImage)
        }
    }

    companion object {
        private const val ARG_PROD_INFO = "PROD_INFO"

        fun newInstance(prod: ProdInfo): ProdDialogFragment {
            val fragment = ProdDialogFragment()
            val args =
                Bundle().apply {
                    putParcelable(ARG_PROD_INFO, prod)
                }
            fragment.arguments = args
            return fragment
        }
    }
}
