package com.ferbajoo.timedream.ui.app.interfaces

import android.arch.lifecycle.MutableLiveData

interface ITimeDream {

    fun setTime(minutes : Int)

    fun formaterTime(time_current : Long)

    fun moreTime()

    fun lessTime()

    fun getTime() : MutableLiveData<String>

    fun getSeconds() : MutableLiveData<String>

    fun saveTime()
}
