package com.ferbajoo.timedream.ui.app.service

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import com.ferbajoo.timedream.ui.app.datasource.AppSharedPreferences

class CountDownTimeTask(private val context: Context) : BaseRunnable() {

    private var preferences : AppSharedPreferences? = null

    private var timer : CountDownTimer? = null

    companion object {
        const val COUNTDOWNSERVICE = "ferbajoo.countdown_service"
    }

    override fun run() {
        setIsRunning(true)

        preferences = AppSharedPreferences(context)

        startTime()
    }

    private fun startTime() {
        timer = object : CountDownTimer(preferences!!.currentTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                context.sendBroadcast(createIntentTime(millisUntilFinished))
            }

            override fun onFinish() {
                context.sendBroadcast(createIntentFinish())
                setIsRunning(false)
            }
        }
        timer!!.start()
    }



    fun cancelTimer() {
        timer?.cancel()
    }

    private fun createIntentTime(millisUntilFinished: Long): Intent {
        return Intent(COUNTDOWNSERVICE).putExtra("millisUntilFinished", millisUntilFinished)
    }

    private fun createIntentFinish(): Intent {
        return Intent(COUNTDOWNSERVICE).putExtra("finish_time", true)
    }


}