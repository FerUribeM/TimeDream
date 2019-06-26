package com.ferbajoo.timedream.ui.app.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.CountDownTimer
import com.ferbajoo.timedream.core.utils.hmsTimeFormatter
import com.ferbajoo.timedream.core.utils.secoundTimeFormatter
import com.ferbajoo.timedream.ui.app.interfaces.ITimeDream
import com.ferbajoo.timedream.ui.app.repository.TimeDreamRepository

class TimeDreamViewModel(private val repository: TimeDreamRepository) : ViewModel(), ITimeDream {

    private val time = MutableLiveData<String>()

    private val seconds = MutableLiveData<String>()

    private var minutes = MutableLiveData<Int>()

    override fun setTime(minutes: Int) {
        this.minutes.value = minutes
        formaterTime(getTimeToLong())
    }

    override fun formaterTime(time_current: Long) {
        time.value = hmsTimeFormatter(time_current)
        seconds.value = secoundTimeFormatter(time_current)
    }

    override fun moreTime() {
        if (!isMax()) {
            this.minutes.value = (this.minutes.value ?: 0) + 5
            time.value = hmsTimeFormatter(getTimeToLong())
        }
    }

    override fun lessTime() {
        if (!isMin()) {
            this.minutes.value = (this.minutes.value ?: 0) - 5
            time.value = hmsTimeFormatter(getTimeToLong())
        }
    }

    private fun getTimeToLong(): Long {
        return ((this.minutes.value ?: 0) * 60 * 1000).toLong()
    }

    override fun saveTime() {
        repository.saveTime(getTimeToLong())
    }

    override fun haveTime() = (this.minutes.value ?: 0) > 0

    private fun isMax() = (((this.minutes.value ?: 0) + 5) > 720)

    private fun isMin() = ((this.minutes.value ?: 0) - 5) < 0

    override fun getSeconds() = seconds

    override fun getTime() = time

    override fun callPlayer(checked: Boolean) {
        repository.callPlayer(checked)
    }

    override fun getSettingPlayer(): Boolean {
        return repository.getSettingPlayer()
    }

}
