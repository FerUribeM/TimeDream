package com.ferbajoo.timedream.core.utils

import android.app.Application
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.content.Context
import android.content.Intent


/**
 * Created by
 *      ferbajoo
 */
fun Disposable.addTo(androidDisposable: AndroidDisposable): Disposable = apply { androidDisposable.add(this) }


fun View.addThrottle(): Observable<Any> {
    return RxView.clicks(this).throttleFirst(1, TimeUnit.SECONDS)
}

/**
 *  variable using in service notification to view
 */
private val ACTION_NOTIFICATION = "CountDownTimer"

val START_TIME = 1
val STOP_TIME = 2


fun Application.removeAllNotifications() {
    val notifManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notifManager.cancelAll()
}

fun AppCompatActivity.callPlayMusic() {
    startActivity(Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC))
}