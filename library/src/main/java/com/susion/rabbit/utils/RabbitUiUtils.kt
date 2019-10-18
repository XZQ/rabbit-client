package com.susion.rabbit.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast

/**
 * susionwang at 2019-09-23
 */
object RabbitUiUtils {
    private val sMetrics = Resources.getSystem().displayMetrics

    fun dp2px(dipValue: Float): Int {
        val scale: Float = sMetrics?.density ?: 1f
        return (dipValue * scale + 0.5f).toInt()
    }

    fun getScreenHeight(): Int {
        return sMetrics?.heightPixels ?: 0
    }

    fun getScreenWidth(): Int {
        return sMetrics?.widthPixels ?: 0
    }

    /**
     * get the color by id
     */
    fun getColor(context: Context?, resId: Int): Int {
        return if (null == context || context.resources == null || resId <= -1) {
            Color.TRANSPARENT
        } else ContextCompat.getColor(context, resId)
    }

    /*
    * 一种比较通用的 Status Bar Color , for Android 6.0 +
    * 魅族，小米 有特有的API。这里没做适配
    * */
    fun setGeneralStatusBarColor(window: Window) {
        setStatusBarColor(window, Color.WHITE)
        setStatusBarTextColor(window, true)
    }


    private fun setStatusBarColor(window: Window, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = color
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }


    fun setStatusBarTextColor(window: Window, lightStatusBar: Boolean) {
        // 设置状态栏字体颜色 白色与深色
        val decor = window.decorView
        var ui = decor.systemUiVisibility
        ui = ui or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ui = if (lightStatusBar) {
                ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
        decor.systemUiVisibility = ui
    }

    /**
     * 复制评论
     * */
    fun copyComment(commentTv: TextView){
        copyStrToClipBoard(commentTv.context, commentTv.text.toString())
        Toast.makeText(commentTv.context, "已复制当前评论", Toast.LENGTH_SHORT).show()
    }

    private fun copyStrToClipBoard(context: Context, str: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(null, str)
        clipboard.primaryClip = clipData
    }


}