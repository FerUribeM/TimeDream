package com.ferbajoo.timedream.core.utils

import android.os.CountDownTimer
import android.util.Log
import android.util.Log.e
import com.ferbajoo.timedream.core.utils.interfaces.ITimerListener
import java.util.concurrent.TimeUnit

class Timer(milis : Long,private val iTime : ITimerListener) : CountDownTimer(milis, 1000) {

    companion object {
        val minutes = TimeUnit.MINUTES.toMillis(5)
    }

    private var milisUntilFinished = 0L

    fun getMilisUntilFinished() : Long {
        return milisUntilFinished
    }

    override fun onFinish() {
        iTime.onFinishEvent()
    }

    override fun onTick(milis : Long) {
        this.milisUntilFinished = milis
        e("Timer","Es $milis")
        iTime.onTickEvent(milis)
    }

}