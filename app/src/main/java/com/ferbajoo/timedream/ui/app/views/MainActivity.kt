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
import android.view.MenuItem
import android.view.View
import com.ferbajoo.timedream.R
import com.ferbajoo.timedream.core.base.BaseActivity
import com.ferbajoo.timedream.core.utils.*
import com.ferbajoo.timedream.ui.app.interfaces.ITimeDream
import com.ferbajoo.timedream.ui.app.service.CountDownTimeTask
import com.ferbajoo.timedream.ui.app.service.CountDownTimerService
import com.ferbajoo.timedream.ui.app.viewmodel.TimeDreamViewModel
import com.ferbajoo.timedream.ui.app.viewmodel.TimeDreamViewModelFactory
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
            if (intent.getBooleanExtra("finish_time", false)) {
                resetViews()
            }
        }
    }

    private fun updateTimer(intent: Intent) {
        viewModel?.formaterTime(intent.getLongExtra("millisUntilFinished", 0))
        lockedViews()
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
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupViewModel()

        ib_play.addThrotle().subscribe(this::startClick).addTo(subscriptions)
        btn_reset.addThrotle().subscribe(this::resetClick).addTo(subscriptions)

        less_minutes.addThrotle().subscribe(this::lessMinutes).addTo(subscriptions)
        more_minutes.addThrotle().throttleFirst(1, TimeUnit.SECONDS).subscribe(this::moreMinutes).addTo(subscriptions)

        croller_view.onProgressChanged { viewModel?.setTime(it) }

        viewModel?.getTime()?.observe(this, Observer { setTime(it) })
        viewModel?.getSeconds()?.observe(this, Observer { setTimeSecound(it) })

    }

    private fun setTimeSecound(second: String?) {
        tv_seconds.text = second ?: ""
    }

    private fun setTime(time: String?) {
        tv_time.text = time ?: ""
    }

    private fun resetClick(any: Any) {
        (btn_reset.drawable as Animatable).start()
        btn_reset.postDelayed({
            btn_reset.setImageDrawable(AnimatedVectorDrawableCompat.create(this, R.drawable.reset_time))
        }, 900)
        viewModel?.setTime(0)
    }

    private fun startClick(any: Any) {
        createAnimStartPause()
        viewModel?.saveTime()
    }

    private fun createAnimStartPause() {
        var image: AnimatedVectorDrawableCompat? = null
        when (getTag()) {
            TimerAction.PLAY -> {
                if (viewModel?.haveTime() == true) {
                    image = AnimatedVectorDrawableCompat.create(this, R.drawable.play_anim)
                    setupServiceTimeDown(false, START_TIME)
                    croller_view.tag = TimerAction.STOP
                    btn_reset.visibility = View.INVISIBLE
                    callPlayMusic()
                }
            }
            TimerAction.STOP -> {
                this.application?.removeAllNotifications()
                image = AnimatedVectorDrawableCompat.create(this, R.drawable.pause_play)
                setupServiceTimeDown(true, STOP_TIME)
                croller_view.tag = TimerAction.PLAY
                resetTimerView()
                btn_reset.visibility = View.VISIBLE
            }
        }

        image?.let {
            ib_play.setImageDrawable(image)
            it.start()
        }
    }

    private fun resetTimerView() {
        tv_time.text = getString(R.string.reset_time_label)
        tv_seconds.text = getString(R.string.time_zero_label)
        viewModel?.setTime(0)
    }

    private fun getTag(): TimerAction {
        return if (croller_view.tag == null || (croller_view.tag != null && croller_view.tag == TimerAction.PLAY)) {
            TimerAction.PLAY
        } else {
            TimerAction.STOP
        }
    }

    private fun setupServiceTimeDown(enable: Boolean, type: Int) {
        croller_view.isEnabled = enable
        btn_reset.isEnabled = enable
        startService(Intent(applicationContext, CountDownTimerService::class.java).putExtra("type", type))
    }

    @SuppressLint("PrivateResource")
    private fun lessMinutes(any: Any) {
        tv_less_minutes.setTransition(R.anim.abc_slide_in_top)
        viewModel?.lessTime()
        startService(Intent(applicationContext, CountDownTimerService::class.java).putExtra("action", true))
    }

    @SuppressLint("PrivateResource")
    private fun moreMinutes(any: Any) {
        tv_more_minutes.setTransition(R.anim.abc_slide_in_bottom)
        viewModel?.moreTime()
        startService(Intent(applicationContext, CountDownTimerService::class.java).putExtra("action", false))
    }

    private fun resetViews() {
        ib_play.setImageDrawable(AnimatedVectorDrawableCompat.create(this, R.drawable.play_anim))
        croller_view.tag = TimerAction.STOP
        croller_view.isEnabled = true
        btn_reset.isEnabled = true
        viewModel?.setTime(0)
    }

    private fun lockedViews() {
        if (croller_view.isEnabled) {
            val image = AnimatedVectorDrawableCompat.create(this, R.drawable.pause_play)
            ib_play.setImageDrawable(image)
            image?.start()
            croller_view.tag = TimerAction.PLAY
            croller_view.isEnabled = false
            btn_reset.isEnabled = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_about -> {
                AboutFragmenDialog.newInstance().show(supportFragmentManager, AboutFragmenDialog.TAG)
                true
            }
            R.id.menu_play -> {
                item.isChecked = !item.isChecked
                viewModel?.callPlayer(item.isChecked)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        menu?.findItem(R.id.menu_play)?.isChecked = viewModel?.getSettingPlayer() ?: false

        return true
    }


}
