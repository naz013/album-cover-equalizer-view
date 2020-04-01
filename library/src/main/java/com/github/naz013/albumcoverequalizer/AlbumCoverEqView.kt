package com.github.naz013.albumcoverequalizer

import android.content.Context
import android.util.AttributeSet
import android.view.View

class AlbumCoverEqView : View {

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {

    }
}