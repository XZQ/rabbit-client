package com.susion.rabbit.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitMainActivity
import com.susion.rabbit.R
import com.susion.rabbit.utils.RabbitUiUtils
import kotlinx.android.synthetic.main.dev_tools_view_floating.view.*
import kotlin.math.abs

/**
 * susionwang at 2019-09-23
 */
class RabbitFloatingView(context: Context) : FrameLayout(context) {

    private var mAnimator: ValueAnimator? = null /* the animation of the float view*/
    private var mXInScreen: Float = 0.toFloat() /* the x position in screen */
    private var mYInScreen: Float = 0.toFloat() /* the y position in screen*/
    var isShow = false

    private val mWindowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val mParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.dev_tools_view_floating, this)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        isClickable = true
        mDevToolsFloatingIv.setOnClickListener {
            if (Rabbit.isInDevToolsPage) {
                Rabbit.quickFinishAllDevToolsPage()
                Rabbit.isInDevToolsPage = false
            } else {
                Rabbit.isInDevToolsPage = true
                RabbitMainActivity.start(context)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mXInScreen = event.rawX
                mYInScreen = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                mXInScreen = event.rawX
                mYInScreen = event.rawY
                updateViewPosition()
            }
            MotionEvent.ACTION_UP -> {
                moveToEdge()
            }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * show this tacker float view
     */
    fun show() {
        if (isShow) return

        isShow = true
        Rabbit.setDevToolsOpenStatus(isShow)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        }
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mParams.format = PixelFormat.RGBA_8888
        mParams.gravity = Gravity.START or Gravity.TOP
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.x = RabbitUiUtils.getScreenWidth()
        mParams.y = (RabbitUiUtils.getScreenHeight() / 2) * 1
        mParams.windowAnimations = android.R.style.Animation_Toast
        mWindowManager.addView(this, mParams)
    }

    fun hide() {
        isShow = false
        Rabbit.setDevToolsOpenStatus(isShow)
        mWindowManager.removeView(this)
    }

    /**
     * update this tracker float position
     */
    private fun updateViewPosition() {
        mParams.x = (mXInScreen - width).toInt()
        mParams.y = (mYInScreen - height).toInt()
        mWindowManager.updateViewLayout(this, mParams)
    }

    /**
     * move this to edge
     */
    private fun moveToEdge() {
        val start = mXInScreen
        val screenWidth = mWindowManager.defaultDisplay?.width
        val end: Float
        end = if (mXInScreen > (screenWidth ?: 0) / 2) {
            (screenWidth ?: 0).toFloat()
        } else {
            0f
        }
        val time = abs(start - end).toLong() * 800 / (screenWidth ?: 0)
        mAnimator = ValueAnimator.ofFloat(start, end).setDuration(time)
        mAnimator?.interpolator = LinearInterpolator()
        mAnimator?.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            mParams.x = value.toInt()
            mWindowManager.updateViewLayout(this@RabbitFloatingView, mParams)
        }
        mAnimator?.start()
    }

}