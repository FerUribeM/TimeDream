package com.ferbajoo.timedream.core.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AndroidDisposable {

    private var compositeDisposable: CompositeDisposable? = null

    fun add(disposable: Disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable)
    }

    /*
    * do not send event after activity has been destroyed
    *
    * Note:
    * + Using CLEAR
    *   -   Will clear all, but can accept new disposable
    *   -   Doesn't allowing you to reuse the CompositeDisposable. (when view is destroyed)
    * + Using DISPOSE
    *   -   Will clear all and set isDisposed = true, so it will not accept any new disposable
    *   -   Terminates even future subscriptions
    * */
    fun dispose() {
        compositeDisposable?.clear()
        compositeDisposable = null
    }
}
