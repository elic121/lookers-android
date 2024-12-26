package com.example.lookers.view.activity

import android.Manifest
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lookers.R
import com.example.lookers.databinding.ActivityMainBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.drawer.DrawerEntity
import com.example.lookers.util.getFirebaseToken
import com.example.lookers.util.goToActivity
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.activity.drawer.DrawerActivity
import com.example.lookers.view.activity.join.RegisterActivity
import com.example.lookers.view.activity.join.ShareActivity
import com.example.lookers.view.adapter.DrawerAdapter
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.viewmodel.AuthViewModel
import com.example.lookers.viewmodel.DataStoreViewModel
import com.example.lookers.viewmodel.DrawerViewModel
import com.example.lookers.viewmodel.SearchViewModel
import com.gun0912.tedpermission.coroutine.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val drawerViewModel: DrawerViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private var shareDrawerId = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setClickListeners()

        observeUserInfo()
        observeDrawerList()
        observeRegisterDrawer()
        handleIntent()

        lifecycleScope.launch {
            val firebaseToken = getFirebaseToken()
            authViewModel.sendFirebaseToken(firebaseToken)
            dataStoreViewModel.getUserInfo()
            drawerViewModel.getAllDrawers()

            getPermission()
        }

        searchViewModel.searchState.observe(this) {
            Timber.d("searchState: $it")
        }
        searchViewModel.searchProdByName("라즈베리파이")
    }

    override fun onResume() {
        super.onResume()
        drawerViewModel.getAllDrawers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_appbar_icon, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_search -> {
                Timber.d("search")
                goToActivity(SearchActivity::class.java)
                true
            }

            R.id.action_profile -> {
                Timber.d("profile")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun handleIntent() {
        val intent = intent
        intent.data?.let { uri ->
            if (uri.scheme == "lookers" && uri.host == "main_activity") {
                shareDrawerId = uri.getQueryParameter("drawerId") ?: ""
                drawerViewModel.registerDrawer(shareDrawerId.toInt())
            }
        }
    }

    private fun updateDrawerList(drawerList: List<DrawerEntity>) {
        binding.recyclerViewDevices.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewDevices.adapter =
            DrawerAdapter(
                drawerList,
                object : ItemClickListener {
                    override fun onClick(objects: Any?) {
                        val bundle =
                            Bundle().apply {
                                putInt("drawerId", (objects as DrawerEntity).drawerId)
                            }
                        goToActivity(DrawerActivity::class.java, extras = bundle)
                    }
                },
            )
    }

    private fun setClickListeners() {
        binding.clRegister.setOnClickListener {
            goToActivity(RegisterActivity::class.java)
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit)
        }

        binding.clShare.setOnClickListener {
            goToActivity(ShareActivity::class.java)
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getPermission() {
        try {
            val permissionResult =
                TedPermission
                    .create()
                    .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ).setDeniedMessage("권한을 허용하지 않으면 일부 기능을 사용할 수 없습니다.\n\n[설정] > [권한]")
                    .check()
        } catch (e: Exception) {
            toast("error: $e")
        }
    }

    private fun observeDrawerList() {
        drawerViewModel.drawerList.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {
                },
                onSuccess = { drawerList ->
                    binding.llEmpty.visibility =
                        if (drawerList.isEmpty()) View.VISIBLE else View.GONE
                    binding.llExist.visibility =
                        if (drawerList.isNotEmpty()) View.VISIBLE else View.GONE

                    updateDrawerList(drawerList)
                },
                onError = { errorMessage ->
                    toast("Failed: $errorMessage")
                },
            )
        }
    }

    private fun observeUserInfo() {
        dataStoreViewModel.userInfo.observe(this) { userInfo ->
            val title = "${userInfo.name}님 안녕하세요."
            val spannable = SpannableString(title)
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                title.lastIndexOf("님"),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
            binding.toolbar.title = spannable
        }
    }

    private fun observeRegisterDrawer() {
        drawerViewModel.drawerRegister.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {
                    toast("Registering...")
                },
                onSuccess = { drawer ->
                    toast("${drawer.drawerName}가 등록되었습니다.")
                },
                onError = {
                    toast("Failed: $it")
                },
            )
        }
    }
}
