package com.ferbajoo.timedream.ui.app.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.ferbajoo.timedream.ui.app.datasource.AppSharedPreferences
import com.ferbajoo.timedream.ui.app.repository.TimeDreamRepository

@Suppress("UNCHECKED_CAST")
class TimeDreamViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private var repository : TimeDreamRepository? = null

    init {
        repository = TimeDreamRepository(AppSharedPreferences(context))
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeDreamViewModel::class.java)){
            return TimeDreamViewModel(repository!!) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
