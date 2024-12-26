package com.example.lookers.view.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lookers.R
import com.example.lookers.databinding.FragmentProdGridBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.prod.ProdEntity
import com.example.lookers.model.entity.prod.ProdInfo
import com.example.lookers.model.entity.prod.ProdType
import com.example.lookers.view.adapter.ProdAdapter
import com.example.lookers.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProdGridFragment : BaseFragment<FragmentProdGridBinding>() {
    private var prods: List<ProdEntity> = emptyList()
    private var type: String = ProdType.UNKNOWN.toString()
    private var isExpanded = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewProds.isVisible = false
        binding.viewDivider.isVisible = false

        setupExpandableHeader()

        arguments?.let {
            type = it.getString("type", ProdType.UNKNOWN.toString())
            prods = it.getParcelableArrayList(ARG_PRODS) ?: emptyList()

            updateProdGrid(type, prods)
        }
    }

    private fun setupExpandableHeader() {
        binding.headerContainer.setOnClickListener {
            toggleExpansion()
        }
    }

    private fun toggleExpansion() {
        isExpanded = !isExpanded

        binding.recyclerViewProds
            .animate()
            .alpha(if (isExpanded) 1f else 0f)
            .setDuration(300)
            .withStartAction {
                if (isExpanded) {
                    binding.recyclerViewProds.isVisible = true
                    binding.viewDivider.isVisible = true
                }
            }.withEndAction {
                if (!isExpanded) {
                    binding.recyclerViewProds.isVisible = false
                    binding.viewDivider.isVisible = false
                }
            }

        ValueAnimator
            .ofFloat(
                if (isExpanded) 0f else 180f,
                if (isExpanded) 180f else 0f,
            ).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { animator ->
                    binding.ivExpandArrow.rotation = animator.animatedValue as Float
                }
                start()
            }
    }

    private fun updateProdGrid(
        type: String,
        prods: List<ProdEntity>,
    ) {
        binding.tvProdType.text = type
        binding.ivProdIcon.setImageResource(getIconResource(type))

        binding.recyclerViewProds.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewProds.adapter =
            ProdAdapter(
                prods,
                object : ItemClickListener {
                    override fun onClick(objects: Any?) {
                        showProdDialog(objects as ProdInfo)
                    }
                },
            )
    }

    private fun showProdDialog(prodInfo: ProdInfo) {
        val fragment = ProdDialogFragment.newInstance(prodInfo)
        fragment.show(parentFragmentManager, "ProdDialogFragment")
    }

    companion object {
        private const val TAG = "ProdGridFragment"
        private const val ARG_PRODS = "PRODS"

        fun newInstance(
            type: String,
            prods: List<ProdEntity>,
        ): ProdGridFragment {
            val fragment = ProdGridFragment()
            val args =
                Bundle().apply {
                    putString("type", type)
                    putParcelableArrayList(ARG_PRODS, ArrayList(prods))
                }
            fragment.arguments = args
            return fragment
        }
    }

    private fun getIconResource(type: String): Int =
        when (type) {
            ProdType.FOOD.toString() -> R.drawable.ic_prod_type_food
            ProdType.ELECTRONICS.toString() -> R.drawable.ic_prod_type_electronics
            ProdType.BOOKS.toString() -> R.drawable.ic_prod_type_book
            ProdType.STATIONERY.toString() -> R.drawable.ic_prod_type_stationery
            ProdType.CLOTHING.toString() -> R.drawable.ic_prod_type_clothing
            ProdType.COSMETICS.toString() -> R.drawable.ic_prod_type_cosmetics
            ProdType.KITCHENWARE.toString() -> R.drawable.ic_prod_type_kitchenware
            ProdType.TOYS.toString() -> R.drawable.ic_prod_type_toy
            else -> R.drawable.ic_prod_type_unknown
        }
}
