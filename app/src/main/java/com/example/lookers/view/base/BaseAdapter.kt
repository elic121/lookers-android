package com.example.lookers.view.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.lookers.interfaces.ItemClickListener

abstract class BaseAdapter<VB : ViewBinding, M>(
    private val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    protected val list: List<M>,
    protected val itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder<VB>>() {
    class BaseViewHolder<VB : ViewBinding>(
        var binding: VB,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<VB> {
        val binding = bindingFactory(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<VB>,
        position: Int,
    ) {
        val device = list[position]
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(device)
        }
    }

    override fun getItemCount(): Int = list.size
}
