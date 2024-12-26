package com.example.lookers.view.adapter

import com.bumptech.glide.Glide
import com.example.lookers.R
import com.example.lookers.databinding.ItemHistroyDetailBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.prod.ProdHistory
import com.example.lookers.model.entity.prod.ProdInfo
import com.example.lookers.util.formatTimeAgo
import com.example.lookers.view.base.BaseAdapter

class ProdHistoryAdapter(
    historyList: List<ProdHistory>,
    itemCLickListener: ItemClickListener,
) : BaseAdapter<ItemHistroyDetailBinding, ProdHistory>(
        bindingFactory = { layoutInflater, parent, attachToParent ->
            ItemHistroyDetailBinding.inflate(layoutInflater, parent, attachToParent)
        },
        list = historyList,
        itemClickListener = itemCLickListener,
    ) {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemHistroyDetailBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        val history = list[position]

        holder.binding.root.setOnClickListener {
            itemClickListener.onClick(ProdInfo.fromHistory(history))
        }

        holder.binding.apply {
            Glide
                .with(holder.itemView.context)
                .load(history.imageUrl)
                .override(100, 100)
                .fitCenter()
                .into(ivDrawerHistoryImage)

            tvHistoryName.text =
                if (history.prodNames.isNotEmpty()) {
                    history.prodNames[0].prodName
                } else {
                    "없음"
                }
            tvDrawerUnitLastUpdateTime.text =
                "${formatTimeAgo(history.updatedAt)}"

            if (history.prodAccessType == "INSERT") {
                tvProdType.text = "IN"
            } else {
                tvProdType.text = "OUT"
                tvProdType.chipBackgroundColor =
                    holder.itemView.context.getColorStateList(R.color.md_theme_error)
            }
        }
    }
}
