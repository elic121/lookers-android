package com.example.lookers.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lookers.databinding.FragmentSearchProdBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.search.SearchProdEntity
import com.example.lookers.util.goToActivity
import com.example.lookers.view.activity.drawer.DrawerUnitActivity
import com.example.lookers.view.adapter.SearchProdAdapter
import com.example.lookers.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchProdFragment : BaseFragment<FragmentSearchProdBinding>() {
    private var searchList: List<SearchProdEntity> = emptyList()
    private var searchQuery: String = ""

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            searchList = it.getParcelableArrayList(ARG_SEARCH_LIST) ?: emptyList()
            searchQuery = it.getString("searchQuery", "")

            setSearchList(searchQuery, searchList)
        }
    }

    private fun setSearchList(
        searchQuery: String,
        searchList: List<SearchProdEntity>,
    ) {
        binding.tvSearchQuery.text = "'${searchQuery}' 검색결과 · ${searchList.size}건"

        if (searchList.isEmpty()) {
            binding.cvManual.visibility = View.GONE
            binding.cvNone.visibility = View.VISIBLE
        } else {
            binding.cvManual.visibility = View.VISIBLE
            binding.cvNone.visibility = View.GONE
        }

        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearch.adapter =
            SearchProdAdapter(
                searchList,
                object : ItemClickListener {
                    override fun onClick(objects: Any?) {
                        val bundle =
                            Bundle().apply {
                                putInt("drawerUnitId", (objects as SearchProdEntity).drawerUnitId)
                            }

                        requireContext().goToActivity(
                            DrawerUnitActivity::class.java,
                            extras = bundle
                        )
                    }
                },
            )
    }

    fun updateSearchList(searchQuery: String, searchList: List<SearchProdEntity>) {
        this.searchList = searchList
        this.searchQuery = searchQuery
        setSearchList(searchQuery, searchList)
    }

    companion object {
        private const val TAG = "SearchFragment"
        private const val ARG_SEARCH_LIST = "SEARCH_LIST"

        fun newInstance(
            searchQuery: String,
            searchList: List<SearchProdEntity>,
        ): SearchProdFragment {
            val fragment = SearchProdFragment()
            val args =
                Bundle().apply {
                    putString("searchQuery", searchQuery)
                    putParcelableArrayList(ARG_SEARCH_LIST, ArrayList(searchList))
                }
            fragment.arguments = args
            return fragment
        }
    }
}
