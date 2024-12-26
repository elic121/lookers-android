package com.example.lookers.view.adapter

import com.example.lookers.databinding.ItemDrawerUnitBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.drawer.DrawerUnitEntity
import com.example.lookers.util.formatTimeAgo
import com.example.lookers.view.base.BasePager

class DrawerUnitAdapter(
    drawerUnits: List<DrawerUnitEntity>,
    itemClickListener: ItemClickListener,
) : BasePager<ItemDrawerUnitBinding, DrawerUnitEntity>(
        bindingFactory = { layoutInflater, parent, attachToParent ->
            ItemDrawerUnitBinding.inflate(layoutInflater, parent, attachToParent)
        },
        items = drawerUnits,
        itemClickListener = itemClickListener,
    ) {
    override fun bind(
        binding: ItemDrawerUnitBinding,
        item: DrawerUnitEntity,
    ) {
        binding.tvDrawerUnitName.text = item.drawerUnitName
        binding.tvDrawerUnitCount.text = "${item.prods.size}개"

        val prodGroups = item.prods.groupBy { it.prodType.toString() }

        binding.tvTags.text =
            if (item.prods.isNotEmpty()) {
                prodGroups
                    .entries
                    .sortedByDescending { it.value.size }
                    .take(3)
                    .joinToString(", ") { it.key }
            } else {
                "없음"
            }

        binding.tvDrawerUnitLastUpdateTime.text =
            if (item.prods.isNotEmpty()) {
                formatTimeAgo(item.updatedAt)
            } else {
                "없음"
            }

        binding.tvDrawerUnitVolume.text = "${prodGroups.size}가지"
    }
}
