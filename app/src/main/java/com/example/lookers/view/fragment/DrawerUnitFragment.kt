package com.example.lookers.view.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.lookers.R
import com.example.lookers.databinding.FragmentDrawerUnitBinding
import com.example.lookers.model.entity.prod.ProdEntity
import com.example.lookers.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class DrawerUnitFragment : BaseFragment<FragmentDrawerUnitBinding>() {
    private var prodList: List<ProdEntity> = emptyList()
    private var scaleX: Float = 0.0f
    private var scaleY: Float = 0.0f

    private var initialY: Float = 0f
    private var initialHeight: Int = 0
    private var minHeight: Int = 0
    private var maxHeight: Int = 0
    private var currentAnimator: ValueAnimator? = null
    private var isInitialAnimationDone = false

    companion object {
        private const val TAG = "DrawerUnitFragment"
        private const val ARG_PROD_LIST = "PROD_LIST"
        private const val SWIPE_THRESHOLD = 100
        private const val ANIMATION_DURATION = 600L
        private const val INITIAL_ANIMATION_DURATION = 800L
        private const val MIN_HEIGHT_RATIO = 0.15f

        fun newInstance(
            prodList: List<ProdEntity>,
            scaleX: Float,
            scaleY: Float,
        ): DrawerUnitFragment {
            val fragment = DrawerUnitFragment()
            val args =
                Bundle().apply {
                    putFloat("scaleX", scaleX)
                    putFloat("scaleY", scaleY)
                    putParcelableArrayList(ARG_PROD_LIST, ArrayList(prodList))
                }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            prodList = it.getParcelableArrayList(ARG_PROD_LIST) ?: emptyList()
            scaleX = it.getFloat("scaleX", 0.0f)
            scaleY = it.getFloat("scaleY", 0.0f)

            adjustViewHeight()
            displayImages()
            addDrawerHandle()
        }

        view.setBackgroundResource(R.drawable.shape_bg_rounded_drawer_unit)
        setupGestureDetection()

        view.alpha = 0f
    }

    override fun onResume() {
        super.onResume()
        if (!isInitialAnimationDone) {
            playInitialAnimation()
        }
    }

    private fun playInitialAnimation() {
        binding.root.post {
            binding.root.layoutParams.height = minHeight
            binding.root.requestLayout()

            binding.root
                .animate()
                .alpha(1f)
                .setDuration(300)
                .start()

            binding.root.postDelayed({
                currentAnimator?.cancel()
                currentAnimator =
                    ValueAnimator.ofInt(minHeight, maxHeight).apply {
                        duration = INITIAL_ANIMATION_DURATION
                        interpolator = OvershootInterpolator(0.8f)

                        addUpdateListener { animator ->
                            binding.root.layoutParams.height = animator.animatedValue as Int
                            binding.root.requestLayout()
                        }

                        doOnEnd {
                            currentAnimator = null
                            isInitialAnimationDone = true
                        }

                        start()
                    }
            }, 200)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestureDetection() {
        var startY = 0f
        var startHeight = 0

        binding.root.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                    startHeight = view.layoutParams.height
                    initialY = event.rawY
                    initialHeight = view.layoutParams.height
                    currentAnimator?.cancel()
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val diff = event.rawY - startY
                    val newHeight = (startHeight - diff).toInt()

                    when {
                        newHeight > maxHeight -> view.layoutParams.height = maxHeight
                        newHeight < minHeight -> view.layoutParams.height = minHeight
                        else -> view.layoutParams.height = newHeight
                    }

                    view.requestLayout()
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val totalDiff = event.rawY - initialY

                    if (abs(totalDiff) > SWIPE_THRESHOLD) {
                        if (totalDiff > 0) {
                            animateHeight(view.layoutParams.height, maxHeight)
                        } else {
                            animateHeight(view.layoutParams.height, minHeight)
                        }
                    } else {
                        animateHeight(view.layoutParams.height, initialHeight)
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun animateHeight(
        fromHeight: Int,
        toHeight: Int,
    ) {
        currentAnimator?.cancel()

        currentAnimator =
            ValueAnimator.ofInt(fromHeight, toHeight).apply {
                duration = ANIMATION_DURATION
                interpolator = OvershootInterpolator(0.8f)

                addUpdateListener { animator ->
                    binding.root.layoutParams.height = animator.animatedValue as Int
                    binding.root.requestLayout()
                }

                doOnEnd {
                    currentAnimator = null
                }

                start()
            }

        if (toHeight == minHeight) {
            hideImages()
        } else {
            showImages()
        }
    }

    private fun adjustViewHeight() {
        binding.root.post {
            val width = binding.root.width
            if (scaleX != 0.0f) {
                val height = (width * (scaleY / scaleX)).toInt()
                maxHeight = height
                minHeight = (height * MIN_HEIGHT_RATIO).toInt()
                binding.root.layoutParams.height = height
                binding.root.requestLayout()
            }
        }
    }

    private fun displayImages() {
        binding.root.post {
            val parentWidth = binding.root.width
            val parentHeight = binding.root.layoutParams.height

            prodList.forEach { prod ->
                val imageView =
                    ImageView(requireContext()).apply {
                        layoutParams =
                            FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                            )
                    }

                Glide
                    .with(this)
                    .load(prod.imageUrl)
                    .override(200, Target.SIZE_ORIGINAL)
                    .fitCenter()
                    .into(imageView)

                val posX = prod.positionX * parentWidth
                val posY = prod.positionY * parentHeight

                imageView.x = posX.toFloat()
                imageView.y = posY.toFloat()
                imageView.rotation = prod.rotate.toFloat()

                binding.root.addView(imageView)
            }
        }
    }

    private fun hideImages() {
        binding.root.children.forEach { view ->
            if (view is ImageView) {
                view.visibility = View.GONE
            }
        }
    }

    private fun showImages() {
        binding.root.children.forEach { view ->
            if (view is ImageView) {
                view.visibility = View.VISIBLE
            }
        }
    }

    private fun addDrawerHandle() {
        val handleContainer =
            View(requireContext()).apply {
                layoutParams =
                    FrameLayout
                        .LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            resources.getDimensionPixelSize(R.dimen.drawer_handle_touch_height),
                        ).apply {
                            gravity = Gravity.BOTTOM
                        }
            }

        val handle =
            View(requireContext()).apply {
                layoutParams =
                    FrameLayout
                        .LayoutParams(
                            resources.getDimensionPixelSize(R.dimen.drawer_handle_width),
                            resources.getDimensionPixelSize(R.dimen.drawer_handle_height),
                        ).apply {
                            gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                            bottomMargin =
                                resources.getDimensionPixelSize(R.dimen.drawer_handle_bottom_margin)
                        }
                background =
                    GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius =
                            resources
                                .getDimensionPixelSize(R.dimen.drawer_handle_corner_radius)
                                .toFloat()
                        setColor(resources.getColor(R.color.md_theme_outline, null))
                    }
            }

        FrameLayout(requireContext()).apply {
            layoutParams = handleContainer.layoutParams
            addView(handle)
            binding.root.addView(this)
        }
    }
}
