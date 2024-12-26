package com.example.lookers.view.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.example.lookers.databinding.ActivitySearchBinding
import com.example.lookers.model.entity.search.SearchProdEntity
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.view.fragment.SearchProdFragment
import com.example.lookers.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>(ActivitySearchBinding::inflate) {
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var searchProdFragment: SearchProdFragment
    private var inputText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupKeyboardBehavior()
        setupSearchEditText()
        observeSearchResult()
    }

    override fun onResume() {
        super.onResume()
        binding.searchEditText.requestFocus()
        showKeyboard()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupKeyboardBehavior() {
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE,
        )
    }

    private fun setupSearchEditText() {
        binding.searchEditText.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                } else {
                    false
                }
            }

            addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        inputText = s?.toString() ?: ""
                        searchViewModel.searchProdByName(inputText)
                    }
                },
            )
        }
    }

    private fun performSearch() {
        inputText = binding.searchEditText.text.toString()
        searchViewModel.searchProdByName(inputText)
        hideKeyboard()
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.searchEditText.let { editText ->
            editText.requestFocus()
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { view ->
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun observeSearchResult() {
        searchViewModel.searchState.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {
                    binding.progressCircular.show()
                },
                onSuccess = { results ->
                    binding.progressCircular.hide()
                    updateRecyclerView(results)
                },
                onError = { error ->
                    binding.progressCircular.hide()
                    toast("검색 중 오류가 발생했습니다: $error")
                },
            )
        }
    }

    private fun updateRecyclerView(searchList: List<SearchProdEntity>) {
        if (!::searchProdFragment.isInitialized) {
            searchProdFragment = SearchProdFragment.newInstance(inputText, searchList)
            supportFragmentManager
                .beginTransaction()
                .replace(binding.containerLayout.id, searchProdFragment)
                .commit()
        } else {
            searchProdFragment.updateSearchList(inputText, searchList)
        }
    }
}
