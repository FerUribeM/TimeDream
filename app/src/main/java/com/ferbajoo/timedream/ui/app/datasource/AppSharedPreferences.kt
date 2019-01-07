package com.ferbajoo.timedream.ui.app.datasource

import android.content.Context
import com.ferbajoo.timedream.R

open class AppSharedPreferences(context: Context) {

    private var NAME = context.getString(R.string.app_name)

    private var prefer = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    var currentTime: Long
        get() = prefer.getLong("currentTime", 0)
        set(value) = prefer.edit().putLong("currentTime", value).apply()

}