package com.ferbajoo.timedream.ui.app.service

import android.app.Application
import android.content.Context
import android.content.Intent
import com.ferbajoo.timedream.core.utils.SoundManager
import com.ferbajoo.timedream.core.utils.Timer
import com.ferbajoo.timedream.core.utils.interfaces.ITimerListener
import com.ferbajoo.timedream.ui.app.datasource.AppSharedPreferences

class CountDownTimeTask(private val context: Context) : BaseRunnable(), ITimerListener {

    private var preferences: AppSharedPreferences? = null

    private var timer: Timer? = null

    companion object {
        const val COUNTDOWNSERVICE = "ferbajoo.countdown_service"
    }

    override fun run() {
        setIsRunning(true)

        preferences = AppSharedPreferences(context)

        startTime(preferences!!.currentTime)
    }

    private fun startTime(milis: Long) {
        timer = Timer(milis, this)
        timer?.start()
    }

    fun updateTimer(less: Boolean = false) {
        if (timer != null) {
            val milis = if (less) (timer?.getMilisUntilFinished()
                    ?: 0L) - Timer.minutes else (timer?.getMilisUntilFinished()
                    ?: 0L) + Timer.minutes

            timer?.cancel()
            timer = null

            startTime(milis)
        } else {
            startTime(preferences!!.currentTime)
        }
    }

    override fun onTickEvent(milis: Long) {
        context.sendBroadcast(createIntentTime(milis))
    }

    override fun onFinishEvent() {
        SoundManager(context as Application).lowerSound()
        setIsRunning(false)
    }

    fun cancelTimer() {
        timer?.cancel()
    }

    private fun createIntentTime(millisUntilFinished: Long): Intent {
        return Intent(COUNTDOWNSERVICE).putExtra("millisUntilFinished", millisUntilFinished)
    }

}