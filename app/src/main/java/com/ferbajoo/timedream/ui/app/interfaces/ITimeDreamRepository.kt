package com.ferbajoo.timedream.ui.app.interfaces

interface ITimeDreamRepository {

    fun getTime() : Long

    fun saveTime(time : Long)

    fun cleanTime()
}