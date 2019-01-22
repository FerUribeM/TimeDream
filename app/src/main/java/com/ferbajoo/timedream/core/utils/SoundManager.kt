package com.ferbajoo.timedream.core.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import com.ferbajoo.timedream.core.utils.interfaces.ITimerListener
import com.ferbajoo.timedream.ui.app.service.CountDownTimeTask

class SoundManager(private val applicationContext: Application) {

    fun lowerSound() {
        val audioManager = (applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager)

        var volumen = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        Timer((volumen * 1000).toLong(), object : ITimerListener {
            override fun onTickEvent(milis: Long) {
                volumen -= 1
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumen, AudioManager.FLAG_SHOW_UI)
            }

            override fun onFinishEvent() {
                stopMusic(audioManager)
            }
        }).start()
    }

    private fun stopMusic(audioManager: AudioManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mPlaybackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(mPlaybackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { event ->
                        Log.e("Event", "Algo hace esto $event")
                    }
                    .build()

            audioManager.requestAudioFocus(request)
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        }

        applicationContext.sendBroadcast(createIntentFinish())
    }

    private fun createIntentFinish(): Intent {
        return Intent(CountDownTimeTask.COUNTDOWNSERVICE).putExtra("finish_time", true)
    }

}