package com.example.lookers.view.adapter

import com.bumptech.glide.Glide
import com.example.lookers.databinding.ItemProdBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.prod.ProdEntity
import com.example.lookers.model.entity.prod.ProdInfo
import com.example.lookers.view.base.BaseAdapter

class ProdAdapter(
    prods: List<ProdEntity>,
    itemCLickListener: ItemClickListener,
) : BaseAdapter<ItemProdBinding, ProdEntity>(
        bindingFactory = { layoutInflater, parent, attachToParent ->
            ItemProdBinding.inflate(layoutInflater, parent, attachToParent)
        },
        list = prods,
        itemClickListener = itemCLickListener,
    ) {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemProdBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(ProdInfo.fromEntity(list[position]))
        }

        val prod = list[position]
        holder.binding.apply {
            Glide
                .with(holder.itemView.context)
                .load(prod.imageUrl)
                .fitCenter()
                .into(ivProdImage)

            tvProdName.text =
                if (prod.prodNames.isNotEmpty()) {
                    prod.prodNames[0].prodName
                } else {
                    "없음"
                }
        }
    }
}
