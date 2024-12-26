package com.example.lookers.view.adapter

import android.graphics.Color
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.lookers.R
import com.example.lookers.databinding.ItemMainDrawerCardviewBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.drawer.DrawerEntity
import com.example.lookers.view.base.BaseAdapter

class DrawerAdapter(
    drawers: List<DrawerEntity>,
    itemClickListener: ItemClickListener,
) : BaseAdapter<ItemMainDrawerCardviewBinding, DrawerEntity>(
        bindingFactory = { layoutInflater, parent, attachToParent ->
            ItemMainDrawerCardviewBinding.inflate(layoutInflater, parent, attachToParent)
        },
        list = drawers,
        itemClickListener = itemClickListener,
    ) {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemMainDrawerCardviewBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        val drawer = list[position]
        holder.binding.apply {
            Glide
                .with(holder.itemView.context)
                .load(drawer.imageUrl)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_lookers)
                .into(ivDrawerProfileImage)
            tvDrawerName.text = drawer.drawerName

            tvDrawerSize.text =
                if (drawer.drawerUnits.isEmpty()) {
                    "측정 불가"
                } else {
                    "${drawer.drawerUnits.size}개 보유"
                }

            root.setCardBackgroundColor(Color.parseColor(drawer.backgroundColor))
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(drawer)
        }
    }
}
