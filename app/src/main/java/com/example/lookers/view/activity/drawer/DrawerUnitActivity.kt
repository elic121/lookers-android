package com.example.lookers.view.activity.drawer

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.fragment.app.FragmentContainerView
import com.example.lookers.R
import com.example.lookers.databinding.ActivityDrawerUnitBinding
import com.example.lookers.model.entity.drawer.DrawerUnitEntity
import com.example.lookers.model.entity.prod.ProdEntity
import com.example.lookers.model.entity.prod.ProdInfo
import com.example.lookers.util.goToActivity
import com.example.lookers.util.handleState
import com.example.lookers.util.toast
import com.example.lookers.view.base.BaseActivity
import com.example.lookers.view.fragment.DrawerUnitFragment
import com.example.lookers.view.fragment.ProdDialogFragment
import com.example.lookers.view.fragment.ProdGridFragment
import com.example.lookers.viewmodel.DrawerViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DrawerUnitActivity : BaseActivity<ActivityDrawerUnitBinding>(ActivityDrawerUnitBinding::inflate) {
    private val drawerViewModel: DrawerViewModel by viewModels()
    private lateinit var drawerUnit: DrawerUnitEntity
    private var drawerId: Int = -1
    private var drawerUnitId: Int = -1
    private var isExistFirebaseMessage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        observeUpdateDrawerUnit()

        setListeners()
        setIntent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_appbar_drawer_unit_icon, menu)
        return true
    }

    override fun onBackPressed() {
        if (isExistFirebaseMessage) {
            val bundle =
                Bundle().apply {
                    putInt("drawerId", drawerId)
                    putBoolean("firebaseMessage", true)
                }
            goToActivity(DrawerActivity::class.java, extras = bundle, clearStack = true)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                if (isExistFirebaseMessage) {
                    val bundle =
                        Bundle().apply {
                            putInt("drawerId", drawerId)
                            putBoolean("firebaseMessage", true)
                        }
                    goToActivity(DrawerActivity::class.java, extras = bundle, clearStack = true)
                } else {
                    finish()
                }
                true
            }

            R.id.action_change_name -> {
                showChangeNameDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun observeUpdateDrawerUnit() {
        drawerViewModel.drawerUnitUpdate.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {},
                onSuccess = { drawerUnit ->
                    toast("${drawerUnit.drawerUnitName}의 정보가 변경되었습니다.")
                    binding.toolbar.title = drawerUnit.drawerUnitName
                },
                onError = {
                    toast(it)
                },
            )
        }

        drawerViewModel.drawerUnit.observe(this) { resultState ->
            handleState(
                resultState,
                onLoading = {
                    binding.progressCircular.visibility = View.VISIBLE
                },
                onSuccess = { drawerUnit ->
                    this.drawerUnit = drawerUnit

                    binding.toolbar.title = this.drawerUnit.drawerUnitName
                    updateProdList(drawerUnit.prods)
                    updateDrawerUnitFragment(drawerUnit.prods)
                    binding.progressCircular.visibility = View.GONE
                },
                onError = {
                    toast(it)
                    binding.progressCircular.visibility = View.GONE
                },
            )
        }
    }

    private fun setIntent() {
        val intent = intent
        intent.extras?.let {
            val drawerUnit =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.getParcelable("drawerUnit", DrawerUnitEntity::class.java)
                } else {
                    it.getParcelable("drawerUnit") as DrawerUnitEntity?
                }

            this.drawerUnit = drawerUnit ?: DrawerUnitEntity(
                id = -1,
                drawerUnitName = "등록된 기기",
                prods = emptyList(),
                updatedAt = "",
            )
            binding.toolbar.title = this.drawerUnit.drawerUnitName

            val prodList = this.drawerUnit.prods
            updateProdList(prodList)
            updateDrawerUnitFragment(prodList)
        }

        intent.extras?.let {
            drawerUnitId = it.getInt("drawerUnitId")
            if (drawerUnitId > 0) {
                drawerViewModel.getDrawerUnitByUnitId(drawerUnitId)
            }
        }

        intent.extras?.let {
            drawerId = it.getInt("drawerId")
            isExistFirebaseMessage = it.getBoolean("firebaseMessage", false)
        }
    }

    private fun updateProdList(prods: List<ProdEntity>) {
        val containerLayout = binding.containerLayout
        val groupByType = prods.groupBy { it.prodType }

        for ((type, items) in groupByType) {
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

            val fragment = ProdGridFragment.newInstance(type.toString(), items)

            supportFragmentManager
                .beginTransaction()
                .replace(fragmentContainer.id, fragment)
                .commit()
        }
    }

    private fun updateDrawerUnitFragment(prods: List<ProdEntity>) {
        val fragment =
            DrawerUnitFragment.newInstance(
                prodList = prods,
                scaleX = 300f,
                scaleY = 240f,
            )

        supportFragmentManager
            .beginTransaction()
            .replace(binding.fcvDrawerUnit.id, fragment)
            .commit()
    }

    private fun setListeners() {
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
            val newDrawerUnit = drawerUnit.copy(drawerUnitName = changeName.text.toString())
            drawerViewModel.updateDrawerUnit(drawerUnit.id, newDrawerUnit)
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

    private fun showProdDialog(prodInfo: ProdInfo) {
        val fragment = ProdDialogFragment.newInstance(prodInfo)
        fragment.show(supportFragmentManager, "ProdDialogFragment")
    }
}
