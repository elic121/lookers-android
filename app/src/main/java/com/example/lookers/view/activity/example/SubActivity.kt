package com.example.lookers.view.activity.example

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lookers.databinding.ActivitySubBinding
import com.example.lookers.interfaces.ItemClickListener
import com.example.lookers.model.entity.example.ExampleEntity
import com.example.lookers.util.goToActivity
import com.example.lookers.util.toast
import com.example.lookers.view.activity.MainActivity
import com.example.lookers.view.fragment.BlankFragment
import com.example.lookers.view.adapter.ExampleAdapter
import com.example.lookers.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubActivity : BaseActivity<ActivitySubBinding>(ActivitySubBinding::inflate) {
    private val exampleEntities = listOf(
        ExampleEntity(
            id = 1,
            xCloudTraceContext = "trace-context-1",
            traceparent = "traceparent-1",
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36",
            host = "example.com"
        ),
        ExampleEntity(
            id = 2,
            xCloudTraceContext = "trace-context-2",
            traceparent = "traceparent-2",
            userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36",
            host = "test.com"
        ),
        ExampleEntity(
            id = 3,
            xCloudTraceContext = "trace-context-3",
            traceparent = "traceparent-3",
            userAgent = "Mozilla/5.0 (Linux; Android 10; SM-G970F Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.101 Mobile Safari/537.36",
            host = "anotherexample.com"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragment1.id, BlankFragment())
                .replace(binding.fragment2.id, BlankFragment())
                .commit()
        }
        setUpListeners()
        setUpAdapter()
    }

    private fun setUpListeners() {
        binding.btnMove.setOnClickListener {
            toast("move to main")
            goToActivity(MainActivity::class.java, clearStack = true)
        }
    }

    private fun setUpAdapter() {
        val itemClickListener = object : ItemClickListener {
            override fun onClick(objects: Any?) {
                val example = objects as ExampleEntity
                toast("Host: ${example.host} clicked")
            }
        }

        val exampleAdapter = ExampleAdapter(
            examples = exampleEntities,
            itemClickListener = itemClickListener
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = exampleAdapter
    }
}
