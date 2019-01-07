package com.ferbajoo.timedream.core.utils

import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by
 *      ferbajoo
 */
fun Disposable.addTo(androidDisposable: AndroidDisposable): Disposable
        = apply { androidDisposable.add(this) }


fun View.addThrottle(): Observable<Any> {
    return RxView.clicks(this).throttleFirst(1, TimeUnit.SECONDS)
}

/**
 *  variable using in service notification to view
 */
private val ACTION_NOTIFICATION = "CountDownTimer"

val START_TIME = 1

val STOP_TIME = 2