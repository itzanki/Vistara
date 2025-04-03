package com.example.vistara

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity

class IntroSplash : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var surfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_splash)

        surfaceView = findViewById(R.id.surfaceView)
        surfaceView.holder.addCallback(this)

        // Transition to MainActivity after 5 seconds
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 5000)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer = MediaPlayer.create(this, R.raw.vistara)
        mediaPlayer.setDisplay(holder)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(1f, 1f) // Full volume
        mediaPlayer.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mediaPlayer.release()
    }
}