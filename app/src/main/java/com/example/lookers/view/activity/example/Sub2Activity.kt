package com.example.lookers.view.activity.example

import android.os.Bundle
import com.example.lookers.databinding.ActivitySub2Binding
import com.example.lookers.util.goToActivity
import com.example.lookers.view.activity.LoginActivity
import com.example.lookers.view.base.BaseActivity

class Sub2Activity : BaseActivity<ActivitySub2Binding>(ActivitySub2Binding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnRegister.setOnClickListener {
            goToActivity(LoginActivity::class.java)
        }
    }
}
