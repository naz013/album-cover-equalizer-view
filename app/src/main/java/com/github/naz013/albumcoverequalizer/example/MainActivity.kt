package com.github.naz013.albumcoverequalizer.example

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.naz013.albumcoverequalizer.AlbumCoverEqView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val random = Random()
    private var job: Job? = null
    private lateinit var albumView: AlbumCoverEqView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        albumView = findViewById(R.id.albumView)

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

    override fun onResume() {
        super.onResume()
        job = GlobalScope.launch(Dispatchers.Default) {
            while (true) {
                if (!isActive) break
                val array = newFloatArray(albumView.getNumberOfBars())
                withContext(Dispatchers.Main) {
                    albumView.setWaveHeights(array)
                }
                delay(250)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }

    private fun newFloatArray(size: Int): FloatArray {
        val array = FloatArray(size)
        for (i in 0 until size) {
            array[i] = random.nextInt(75).toFloat()
        }
        return array
    }
}
