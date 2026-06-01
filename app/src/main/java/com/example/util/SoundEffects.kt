package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

object SoundEffects {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            // Volume is set to 70 out of 100
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playMoveSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playWinSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 80)
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 180)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 120)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playDrawSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_LOW_L, 250)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playButtonSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
