package com.example.lookers.view.adapter

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.lookers.databinding.ItemDrawerUserBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.drawer.DrawerUser
import com.example.lookers.util.convertTimeToKorean
import com.example.lookers.util.goToActivity
import com.example.lookers.view.activity.ImageViewActivity
import com.example.lookers.view.base.BaseAdapter

class DrawerUserAdapter(
    users: List<DrawerUser>,
    itemCLickListener: ItemClickListener,
) : BaseAdapter<ItemDrawerUserBinding, DrawerUser>(
        bindingFactory = { layoutInflater, parent, attachToParent ->
            ItemDrawerUserBinding.inflate(layoutInflater, parent, attachToParent)
        },
        list = users,
        itemClickListener = itemCLickListener,
    ) {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemDrawerUserBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        val user = list[position]
        holder.binding.apply {
            Glide
                .with(holder.itemView.context)
                .load(user.profileImage)
                .fitCenter()
                .into(ivProfileImage)

            tvProfileName.text = user.name
            tvJoinDate.text = convertTimeToKorean(user.createdAt)

            ivProfileImage.setOnClickListener {
                val bundle =
                    Bundle().apply {
                        putString("imageUrl", user.profileImage)
                    }
                holder.itemView.context.goToActivity(ImageViewActivity::class.java, extras = bundle)
            }
        }
    }
}
