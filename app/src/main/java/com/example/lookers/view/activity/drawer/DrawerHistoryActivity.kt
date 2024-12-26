package com.example.lookers.view.activity.drawer

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.fragment.app.FragmentContainerView
import com.example.lookers.databinding.ActivityDrawerHistoryBinding
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.view.fragment.ProdHistoryFragment
import com.example.lookers.viewmodel.DrawerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DrawerHistoryActivity :
    BaseActivity<ActivityDrawerHistoryBinding>(ActivityDrawerHistoryBinding::inflate) {
    private val drawerViewModel: DrawerViewModel by viewModels()
    private var drawerId: Int = 0
    private var drawerName: String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        observeHistory()
        setIntent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun observeHistory() {
        drawerViewModel.prodHistoryList.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {
                    binding.progressCircular.visibility = View.VISIBLE
                },
                onSuccess = { prodHistoryList ->
                    val containerLayout = binding.containerLayout
                    val groupByDate =
                        prodHistoryList
                            .sortedByDescending { it.updatedAt }
                            .groupBy { it.updatedAt.substringBefore("T") }

                    for ((date, items) in groupByDate) {
                        val fragmentContainer =
                            FragmentContainerView(this).apply {
                                id = View.generateViewId()
                                layoutParams =
                                    LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                    )
                            }

                        containerLayout.addView(fragmentContainer)

                        val fragment = ProdHistoryFragment.newInstance(items, date)
                        supportFragmentManager
                            .beginTransaction()
                            .replace(fragmentContainer.id, fragment)
                            .commit()
                    }

                    binding.progressCircular.visibility = View.GONE
                },
                onError = { errorMessage ->
                    toast("Failed: $errorMessage")
                    binding.progressCircular.visibility = View.GONE
                },
            )
        }
    }

    private fun setIntent() {
        val intent = intent
        intent.extras?.let {
            drawerId = it.getInt("drawerId")
            drawerName = it.getString("drawerName") ?: " "

            binding.toolbar.title = "${drawerName}의 기록"
            drawerViewModel.getHistoryByDrawerId(drawerId)
        }
    }
}
