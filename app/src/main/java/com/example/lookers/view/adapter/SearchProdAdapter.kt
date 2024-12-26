package com.example.lookers.view.adapter

import com.bumptech.glide.Glide
import com.example.lookers.databinding.ItemSearchDetailBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.search.SearchProdEntity
import com.example.lookers.view.base.BaseAdapter

class SearchProdAdapter(
    searchList: List<SearchProdEntity>,
    itemCLickListener: ItemClickListener,
) : BaseAdapter<ItemSearchDetailBinding, SearchProdEntity>(
        bindingFactory = { layoutInflater, parent, attachToParent ->
            ItemSearchDetailBinding.inflate(layoutInflater, parent, attachToParent)
        },
        list = searchList,
        itemClickListener = itemCLickListener,
    ) {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemSearchDetailBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        val search = list[position]

        holder.binding.root.setOnClickListener {
            itemClickListener.onClick(search)
        }

        holder.binding.apply {
            Glide
                .with(holder.itemView.context)
                .load(search.imageUrl)
                .override(100, 100)
                .fitCenter()
                .into(ivDrawerSearchImage)

            tvSearchName.text =
                if (search.names.isNotEmpty()) {
                    search.names[0]
                } else {
                    "없음"
                }

            tvDrawerName.text =
                "#${search.drawerName} #${search.drawerUnitName} #${search.prodType}"
        }
    }
}
