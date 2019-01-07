package com.ferbajoo.timedream.core.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ferbajoo.timedream.core.utils.AndroidDisposable
import io.reactivex.disposables.CompositeDisposable

open class BaseActivity : AppCompatActivity(){

    enum class TimerAction{
        PLAY,
        STOP
    }

    val subscriptions = AndroidDisposable()

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.dispose()
    }

}
