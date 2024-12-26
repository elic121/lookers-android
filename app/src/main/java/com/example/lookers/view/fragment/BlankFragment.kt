package com.example.lookers.view.fragment

import android.os.Bundle
import android.view.View
import com.example.lookers.R
import com.example.lookers.databinding.FragmentBlankBinding
import com.example.lookers.view.base.BaseFragment

class BlankFragment : BaseFragment<FragmentBlankBinding>() {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgGoogle.setImageResource(R.drawable.ic_launcher_background)
    }
}
