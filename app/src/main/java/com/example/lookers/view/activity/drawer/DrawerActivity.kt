package com.example.lookers.view.activity.drawer

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.lookers.R
import com.example.lookers.databinding.ActivityDrawerBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.drawer.DrawerEntity
import com.example.lookers.model.entity.drawer.DrawerUnitEntity
import com.example.lookers.util.ImageProcessor
import com.example.lookers.util.animation.PagerZoomOutPageTransformer
import com.example.lookers.util.dpToPx
import com.example.lookers.util.goToActivity
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.activity.ImageViewActivity
import com.example.lookers.view.activity.MainActivity
import com.example.lookers.view.adapter.DrawerUnitAdapter
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.viewmodel.DrawerViewModel
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DrawerActivity : BaseActivity<ActivityDrawerBinding>(ActivityDrawerBinding::inflate) {
    private var isExistFirebaseMessage: Boolean = false

    private val drawerViewModel: DrawerViewModel by viewModels()
    private var drawerId: Int = -1
    private var drawer: DrawerEntity = DrawerEntity(0, "", "", listOf(), "#4F616E")

    private val imageProcessor = ImageProcessor(this)
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                handleSelectedImage(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        observeUpdateDrawer()
        observeGetDrawer()
        observeRegisterDrawerImage()

        handleIntent()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        if (drawerId != -1) {
            drawerViewModel.getDrawerByDrawerId(drawerId)
        }
    }

    override fun onBackPressed() {
        if (isExistFirebaseMessage) {
            goToActivity(MainActivity::class.java, clearStack = true)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_appbar_drawer_icon, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                if (isExistFirebaseMessage) {
                    goToActivity(MainActivity::class.java, clearStack = true)
                } else {
                    finish()
                }
                true
            }

            R.id.action_change_name -> {
                showChangeNameDialog()
                true
            }

            R.id.action_change_color -> {
                showChangeColorDialog()
                true
            }

            R.id.action_change_image -> {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun observeGetDrawer() {
        drawerViewModel.drawerDetail.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {},
                onSuccess = { drawer ->
                    this.drawer = drawer

                    binding.tvProfileName.text = drawer.drawerName
                    Glide
                        .with(this)
                        .load(drawer.imageUrl)
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_lookers)
                        .into(binding.ivProfileImage)

                    setViewPager(drawer.drawerUnits)
                },
                onError = {
                    toast(it)
                },
            )
        }
    }

    private fun observeUpdateDrawer() {
        drawerViewModel.drawerUpdate.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {
                },
                onSuccess = { drawer ->
                    toast("${drawer.drawerName}의 정보가 변경되었습니다.")
                    binding.tvProfileName.text = drawer.drawerName
                },
                onError = {
                    toast(it)
                },
            )
        }
    }

    private fun observeRegisterDrawerImage() {
        drawerViewModel.drawerImage.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {
                },
                onSuccess = { result ->
                    Glide
                        .with(this)
                        .load(result.imageUrl)
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher_lookers)
                        .into(binding.ivProfileImage)

                    toast("이미지가 변경되었습니다.")
                },
                onError = {
                    toast("Image upload failed: $it")
                    Timber.e(it)
                },
            )
        }
    }

    private fun handleIntent() {
        val intent = intent
        intent.extras?.let {
            drawerId = it.getInt("drawerId")
        }

        intent.extras?.let {
            isExistFirebaseMessage = it.getBoolean("firebaseMessage", false)
        }
    }

    private fun setListeners() {
        binding.clUsers.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt("drawerId", drawer.drawerId)
                    putString("drawerName", drawer.drawerName)
                }

            goToActivity(DrawerUserActivity::class.java, extras = bundle)
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit)
        }

        binding.clHistory.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putInt("drawerId", drawer.drawerId)
                    putString("drawerName", drawer.drawerName)
                }
            goToActivity(DrawerHistoryActivity::class.java, extras = bundle)
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit)
        }

        binding.ivProfileImage.setOnClickListener {
            val bundle =
                Bundle().apply {
                    putString("imageUrl", drawer.imageUrl)
                }
            goToActivity(ImageViewActivity::class.java, extras = bundle)
        }
    }

    private fun setViewPager(drawerUnits: List<DrawerUnitEntity>) {
        binding.viewPagerDrawerUnit.adapter =
            DrawerUnitAdapter(
                drawerUnits,
                object : ItemClickListener {
                    override fun onClick(objects: Any?) {
                        val bundle =
                            Bundle().apply {
                                putParcelable("drawerUnit", objects as DrawerUnitEntity)
                            }
                        goToActivity(DrawerUnitActivity::class.java, extras = bundle)
                    }
                },
            )
        binding.viewPagerDrawerUnit.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPagerDrawerUnit.setPageTransformer(PagerZoomOutPageTransformer())
        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerDrawerUnit) { _, _ ->
        }.attach()
    }

    private fun showChangeNameDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_drawer_change_name, null)

        val dialog =
            AlertDialog
                .Builder(this)
                .setView(dialogView)
                .create()

        val changeName = dialogView.findViewById<TextInputEditText>(R.id.tf_change_drawer_name)

        dialogView.findViewById<MaterialButton>(R.id.btnConnect).setOnClickListener {
            val newDrawer =
                drawer.copy(
                    drawerName = changeName.text.toString(),
                    imageUrl = drawer.imageUrl,
                )
            Timber.d(newDrawer.toString())
            drawerViewModel.updateDrawer(newDrawer)
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes =
            dialog.window?.attributes?.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }

        dialog.show()
    }

    private fun showChangeColorDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_drawer_change_color, null)
        val colorChipGroup = dialogView.findViewById<FlexboxLayout>(R.id.colorChipGroup)

        val dialog =
            AlertDialog
                .Builder(this)
                .setView(dialogView)
                .create()

        val colors =
            listOf(
                "#7A868B",
                "#3399CC",
                "#E67A7A",
                "#9E8FA1",
            )

        colors.forEach { colorHex ->
            val cardView =
                MaterialCardView(this).apply {
                    layoutParams =
                        FlexboxLayout.LayoutParams(60.dpToPx(), 60.dpToPx()).apply {
                            setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
                        }
                    radius = 30.dpToPx().toFloat()
                    strokeWidth = 2.dpToPx()
                    strokeColor = Color.parseColor("#E0E0E0")
                    cardElevation = 0f
                    setCardBackgroundColor(Color.parseColor(colorHex))

                    setOnClickListener {
                        colorChipGroup.children.forEach { view ->
                            (view as? MaterialCardView)?.strokeColor =
                                Color.parseColor("#E0E0E0")
                        }
                        strokeColor = Color.parseColor("#2196F3")
                        strokeWidth = 4.dpToPx()

                        val newDrawer = drawer.copy(backgroundColor = colorHex)
                        drawerViewModel.updateDrawer(newDrawer)
                        dialog.dismiss()
                    }
                }

            colorChipGroup.addView(cardView)
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes =
            dialog.window?.attributes?.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }

        dialog.show()
    }

    private fun handleSelectedImage(uri: Uri) {
        lifecycleScope.launch {
            try {
                val tempFile = imageProcessor.createTempFileFromUri(uri)
                val resizedFile = imageProcessor.resizeAndCompressImage(tempFile, 4)
                val imagePart = imageProcessor.createMultipartBodyPart(resizedFile, "file")

                drawerViewModel.registerDrawerImage(drawer.drawerId, imagePart)
            } catch (e: Exception) {
                toast("Failed to process image: ${e.message}")
            }
        }
    }
}
