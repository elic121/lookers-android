package com.example.lookers.view.activity.drawer

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lookers.databinding.ActivityDrawerUserBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.drawer.DrawerUser
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.adapter.DrawerUserAdapter
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.viewmodel.DrawerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DrawerUserActivity : BaseActivity<ActivityDrawerUserBinding>(ActivityDrawerUserBinding::inflate) {
    private val drawerViewModel: DrawerViewModel by viewModels()
    private var drawerId: Int = 0
    private var drawerName: String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        observeUsers()
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

    private fun setIntent() {
        val intent = intent
        intent.extras?.let {
            drawerId = it.getInt("drawerId")
            drawerName = it.getString("drawerName") ?: " "
            drawerViewModel.getUsersByDrawerId(drawerId)
            binding.toolbar.title = "${drawerName}의 멤버"
        }
    }

    private fun observeUsers() {
        drawerViewModel.drawerUsers.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {},
                onSuccess = { drawerUsers ->
                    updateUserList(drawerUsers)
                },
                onError = { errorMessage ->
                    toast("Failed: $errorMessage")
                },
            )
        }
    }

    private fun updateUserList(users: List<DrawerUser>) {
        binding.tvMembers.text = "멤버 · ${users.size - 1}"

        binding.recyclerViewHost.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHost.adapter =
            DrawerUserAdapter(
                users.filter { it.role == "HOST" },
                object : ItemClickListener {
                    override fun onClick(objects: Any?) {}
                },
            )

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsers.adapter =
            DrawerUserAdapter(
                users.filter { it.role != "HOST" },
                object : ItemClickListener {
                    override fun onClick(objects: Any?) {}
                },
            )
    }
}
