package com.example.lookers.view.adapter

import com.example.lookers.databinding.ItemExampleBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.example.ExampleEntity
import com.example.lookers.view.base.BaseAdapter

class ExampleAdapter(
    examples: List<ExampleEntity>,
    itemClickListener: ItemClickListener
) : BaseAdapter<ItemExampleBinding, ExampleEntity>(
    bindingFactory = { layoutInflater, parent, attachToParent ->
        ItemExampleBinding.inflate(layoutInflater, parent, attachToParent)
    },
    list = examples,
    itemClickListener = itemClickListener,
){
    override fun onBindViewHolder(holder: BaseViewHolder<ItemExampleBinding>, position: Int) {
        super.onBindViewHolder(holder, position)

        val example = list[position]
        holder.binding.apply {
            item1.text = example.host
            item2.text = example.userAgent
        }
    }
}