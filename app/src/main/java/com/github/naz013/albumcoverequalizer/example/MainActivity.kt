package com.github.naz013.albumcoverequalizer.example

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSeekBar
import com.github.naz013.albumcoverequalizer.AlbumCoverEqView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val random = Random()
    private var job: Job? = null
    private lateinit var albumView: AlbumCoverEqView
    private lateinit var playPauseButton: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playPauseButton = findViewById(R.id.playPauseButton)
        albumView = findViewById(R.id.albumView)
        playPauseButton.setOnClickListener { playPauseClick() }
        findViewById<RadioGroup>(R.id.radioGroup).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioSlow -> albumView.setAnimationSpeed(AlbumCoverEqView.ANIMATION_SLOW)
                R.id.radioMedium -> albumView.setAnimationSpeed(AlbumCoverEqView.ANIMATION_MEDIUM)
                R.id.radioFast -> albumView.setAnimationSpeed(AlbumCoverEqView.ANIMATION_FAST)
            }
        }
        findViewById<AppCompatSeekBar>(R.id.numberSeekBar).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                albumView.setNumberOfBars(progress + 5)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        findViewById<AppCompatSeekBar>(R.id.numberSeekBar).progress = 11

        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = Picasso.get()
                .load("https://images.theconversation.com/files/258026/original/file-20190208-174861-nms2kt.jpg?ixlib=rb-1.1.0&q=45&auto=format&w=926&fit=clip")
                .centerCrop()
                .resize(1024, 1024)
                .get()
            withContext(Dispatchers.Main) {
                albumView.setCoverImage(bitmap)
            }
        }
    }

    private fun playPauseClick() {
        val j = job
        job = if (j == null) {
            GlobalScope.launch(Dispatchers.Default) {
                while (true) {
                    if (!isActive) break
                    val array = newFloatArray(albumView.getNumberOfBars())
                    withContext(Dispatchers.Main) {
                        albumView.setWaveHeights(array)
                    }
                    delay(250)
                }
            }
        } else {
            j.cancel()
            albumView.showFullCover()
            null
        }
        updateButtonIcon()
    }

    private fun updateButtonIcon() {
        if (job == null) {
            playPauseButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp)
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
        job = null
        updateButtonIcon()
    }

    private fun newFloatArray(size: Int): FloatArray {
        val array = FloatArray(size)
        for (i in 0 until size) {
            array[i] = random.nextInt(75).toFloat()
        }
        return array
    }
}
