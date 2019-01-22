package com.ferbajoo.timedream.core.utils.interfaces

interface ITimerListener {
    fun onTickEvent(milis : Long)
    fun onFinishEvent()
}