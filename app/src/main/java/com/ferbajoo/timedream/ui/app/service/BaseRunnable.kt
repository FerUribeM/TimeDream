package com.ferbajoo.timedream.ui.app.service

abstract class BaseRunnable : Runnable{

    @Volatile var mIsRunning = false

    fun getStartTime() = 1000

    fun isRunning() = mIsRunning

    fun setIsRunning(isRunning : Boolean){
        this.mIsRunning = isRunning
    }


}
