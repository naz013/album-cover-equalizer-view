package com.github.naz013.albumcoverequalizer.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.naz013.albumcoverequalizer.AlbumCoverEqView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val albumView = findViewById<AlbumCoverEqView>(R.id.albumView)

    }
}
