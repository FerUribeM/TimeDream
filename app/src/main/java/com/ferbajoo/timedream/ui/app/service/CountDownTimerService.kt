package com.ferbajoo.timedream.ui.app.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import com.ferbajoo.timedream.core.utils.NotificationHelper
import com.ferbajoo.timedream.core.utils.START_TIME
import com.ferbajoo.timedream.core.utils.STOP_TIME


class CountDownTimerService : Service() {

    private val TASK_NONE = -1
    /** Must no be 0  */
    private var mActualTask: BaseRunnable? = null
    private var mReturnState = Service.START_NOT_STICKY

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var task_id = TASK_NONE

        if (intent!!.extras != null) {
            task_id = intent.extras!!.getInt("type", -1)
        }

        when (task_id) {
            START_TIME -> {
                mActualTask = CountDownTimeTask(applicationContext)
            }
            STOP_TIME -> {
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                if (mActualTask != null && mActualTask?.isRunning() == true) {
                    (mActualTask as CountDownTimeTask).updateTimer(intent.getBooleanExtra("action", false))
                    return mReturnState
                }
            }
        }

        NotificationHelper().createNotification(this, "Time Dream", "Hora de dormir...")

        Handler().postDelayed(mActualTask, mActualTask!!.getStartTime())
        mReturnState = START_STICKY

        return mReturnState
    }

    override fun onDestroy() {
        (mActualTask as CountDownTimeTask).cancelTimer()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?) = null

}