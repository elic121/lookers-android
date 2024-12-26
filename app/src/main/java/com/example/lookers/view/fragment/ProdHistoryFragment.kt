package com.example.lookers.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lookers.databinding.FragmentHistoryBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.prod.ProdHistory
import com.example.lookers.model.entity.prod.ProdInfo
import com.example.lookers.view.adapter.ProdHistoryAdapter
import com.example.lookers.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProdHistoryFragment : BaseFragment<FragmentHistoryBinding>() {
    private var historyList: List<ProdHistory> = emptyList()
    private var date: String = ""

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            historyList = it.getParcelableArrayList(ARG_HISTORY_LIST) ?: emptyList()
            date = it.getString("date", "")

            updateHistoryList(historyList, date)
        }
    }

    private fun updateHistoryList(
        historyList: List<ProdHistory>,
        date: String,
    ) {
        binding.tvUpdatedDate.text = date

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter =
            ProdHistoryAdapter(
                historyList,
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
        private const val TAG = "HistoryFragment"
        private const val ARG_HISTORY_LIST = "HISTORY_LIST"

        fun newInstance(
            historyList: List<ProdHistory>,
            date: String,
        ): ProdHistoryFragment {
            val fragment = ProdHistoryFragment()
            val args =
                Bundle().apply {
                    putString("date", date)
                    putParcelableArrayList(ARG_HISTORY_LIST, ArrayList(historyList))
                }
            fragment.arguments = args
            return fragment
        }
    }
}
