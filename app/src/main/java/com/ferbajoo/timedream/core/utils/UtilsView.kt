package com.ferbajoo.timedream.core.utils

import android.content.Context
import android.content.res.Resources
import android.support.v4.content.ContextCompat
import android.util.Log.e
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferbajoo.timedream.core.custom_views.CircularSeekBar

fun convertDpToPixel(r: Resources, dp: Float): Float {
    val metrics = r.displayMetrics
    return dp * (metrics.densityDpi / 160f)
}

fun Window.setupTranslucentStatusBar(context: Context) {
    this.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    this.navigationBarColor = ContextCompat.getColor(context, android.R.color.transparent)
    this.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
}

fun View.setTransition(transition: Int) {
    val view = this
    val animation = AnimationUtils.loadAnimation(context, transition)
    view.startAnimation(animation)
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {}
        override fun onAnimationEnd(p0: Animation?) {
            view.visibility = View.INVISIBLE
        }

        override fun onAnimationStart(p0: Animation?) {
            view.visibility = View.VISIBLE
        }
    })
}

fun CircularSeekBar.onProgressChanged(cb: (Int) -> Unit) {
    this.setOnCircularSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
        override fun onProgressChanged(seekBar: CircularSeekBar, progress: Float, fromUser: Boolean) {
            cb(progress.toInt() / 3)
        }

        override fun onStartTrackingTouch(seekBar: CircularSeekBar) {}
        override fun onStopTrackingTouch(seekBar: CircularSeekBar) {}
    })
}