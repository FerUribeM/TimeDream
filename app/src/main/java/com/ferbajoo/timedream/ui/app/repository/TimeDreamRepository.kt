package com.ferbajoo.timedream.ui.app.repository

import com.ferbajoo.timedream.ui.app.datasource.AppSharedPreferences
import com.ferbajoo.timedream.ui.app.interfaces.ITimeDreamRepository

class TimeDreamRepository(private val appSharedPreferences: AppSharedPreferences) : ITimeDreamRepository{

    override fun saveTime(time : Long) {
        appSharedPreferences.currentTime = time
    }

    override fun cleanTime() {
        appSharedPreferences.currentTime = 0
    }

    override fun getTime(): Long {
        return appSharedPreferences.currentTime
    }

    override fun callPlayer(checked: Boolean) {
        appSharedPreferences.callPlayer = checked
    }

    override fun getSettingPlayer(): Boolean {
        return appSharedPreferences.callPlayer
    }

}
