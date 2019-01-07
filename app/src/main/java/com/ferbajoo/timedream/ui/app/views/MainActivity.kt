package com.ferbajoo.timedream.ui.app.views

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.view.Menu
import com.ferbajoo.timedream.R
import com.ferbajoo.timedream.core.base.BaseActivity
import com.ferbajoo.timedream.core.utils.*
import com.ferbajoo.timedream.ui.app.interfaces.ITimeDream
import com.ferbajoo.timedream.ui.app.service.CountDownTimeTask
import com.ferbajoo.timedream.ui.app.service.CountDownTimerService
import com.ferbajoo.timedream.ui.app.viewmodel.TimeDreamViewModel
import com.ferbajoo.timedream.ui.app.viewmodel.TimeDreamViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private var viewModel: ITimeDream? = null

    private fun setupViewModel() {
        val viewModelFactory = TimeDreamViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TimeDreamViewModel::class.java)
    }

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateTimer(intent) // or whatever method used to update your GUI fields
        }
    }

    private fun updateTimer(intent: Intent) {
        viewModel!!.formaterTime(intent.getLongExtra("millisUntilFinished", 0))
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(br, IntentFilter(CountDownTimeTask.COUNTDOWNSERVICE))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(br)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupTranslucentStatusBar(this)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tb_main)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        setupViewModel()

        RxView.clicks(ib_play).subscribe(this::startClick).addTo(subscriptions)
        RxView.clicks(btn_reset).subscribe(this::resetClick).addTo(subscriptions)

        RxView.clicks(less_minutes).throttleFirst(1, TimeUnit.SECONDS).subscribe(this::lessMinutes).addTo(subscriptions)
        RxView.clicks(more_minutes).throttleFirst(1, TimeUnit.SECONDS).subscribe(this::moreMinutes).addTo(subscriptions)

        croller_view.onProgressChanged { viewModel!!.setTime(it) }

        viewModel!!.getTime().observe(this, Observer { it -> setTime(it!!) })
        viewModel!!.getSeconds().observe(this, Observer { it -> setTimeSecound(it!!) })

    }

    private fun setTimeSecound(it: String) {
        tv_seconds.text = it
    }

    private fun setTime(time: String) {
        tv_time.text = time
    }

    private fun resetClick(any: Any) {
        (btn_reset.drawable as Animatable).start()
        viewModel!!.setTime(0)
    }

    private fun startClick(any: Any) {
        createAnimStartPause()
        viewModel!!.saveTime()
    }

    private fun createAnimStartPause() {
        var image: AnimatedVectorDrawableCompat? = null
        when (getTag()) {
            TimerAction.PLAY -> {
                image = AnimatedVectorDrawableCompat.create(this, R.drawable.play_anim)
                startServiceTimeDown()
                croller_view.tag = TimerAction.STOP
            }
            TimerAction.STOP -> {
                image = AnimatedVectorDrawableCompat.create(this, R.drawable.pause_play)
                stopServiceTimeDown()
                croller_view.tag = TimerAction.PLAY
            }
        }

        image?.let {
            ib_play.setImageDrawable(image)
            it.start()
        }
    }

    private fun getTag(): TimerAction {
        return if (croller_view.tag == null || (croller_view.tag != null && croller_view.tag == TimerAction.PLAY)) {
            TimerAction.PLAY
        } else {
            TimerAction.STOP
        }
    }

    private fun startServiceTimeDown() {
        croller_view.isEnabled = false
        btn_reset.isEnabled = false
        startService(Intent(applicationContext, CountDownTimerService::class.java).putExtra("type", START_TIME))
    }

    private fun stopServiceTimeDown() {
        croller_view.isEnabled = true
        btn_reset.isEnabled = true
        startService(Intent(applicationContext, CountDownTimerService::class.java).putExtra("type", STOP_TIME))
    }

    @SuppressLint("PrivateResource")
    private fun lessMinutes(any: Any) {
        tv_less_minutes.setTransition(R.anim.abc_slide_in_top)
        viewModel!!.lessTime()
    }

    @SuppressLint("PrivateResource")
    private fun moreMinutes(any: Any) {
        tv_more_minutes.setTransition(R.anim.abc_slide_in_bottom)
        viewModel!!.moreTime()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
