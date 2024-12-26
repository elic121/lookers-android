package com.example.lookers.view.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.lookers.interfaces.ItemClickListener

abstract class BasePager<VB : ViewBinding, M>(
    private val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    protected val items: List<M>,
    protected val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<BasePager.BasePagerViewHolder<VB>>() {

    class BasePagerViewHolder<VB : ViewBinding>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePagerViewHolder<VB> {
        val binding = bindingFactory(LayoutInflater.from(parent.context), parent, false)
        return BasePagerViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BasePagerViewHolder<VB>, position: Int) {
        val item = items[position]
        bind(holder.binding, item)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(item)
        }
    }

    abstract fun bind(binding: VB, item: M)
}
