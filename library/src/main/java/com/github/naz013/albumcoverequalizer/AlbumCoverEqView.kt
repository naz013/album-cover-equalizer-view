package com.github.naz013.albumcoverequalizer

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.Px

class AlbumCoverEqView : View {

    private var bars = listOf<BarView>()
    private val barPaint = Paint()

    @ColorInt
    private var dividerColor: Int = Color.WHITE

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {

    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) {
            canvas.drawColor(dividerColor)
            bars.forEach { it.draw(canvas, barPaint) }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        calculateObjects(width, height)
    }

    private fun calculateObjects(width: Int, height: Int) {
        printLog("calculateObjects: $width, $height")

        val padding = dp2px(4).toFloat()


    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            dividerColor = state.getInt(COLOR_DIVIDER_KEY, dividerColor)
            super.onRestoreInstanceState(state.getParcelable(SUPER_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedInstance = Bundle()
        savedInstance.putParcelable(SUPER_KEY, super.onSaveInstanceState())
        savedInstance.putInt(COLOR_DIVIDER_KEY, dividerColor)
        return savedInstance
    }

    override fun setOnTouchListener(l: OnTouchListener?) {
    }

    override fun setOnClickListener(l: OnClickListener?) {
    }

    override fun setBackgroundColor(color: Int) {
    }

    override fun setBackgroundTintBlendMode(blendMode: BlendMode?) {
    }

    override fun setBackgroundDrawable(background: Drawable?) {
    }

    override fun setBackground(background: Drawable?) {
    }

    override fun setBackgroundResource(resid: Int) {
    }

    override fun setBackgroundTintList(tint: ColorStateList?) {
    }

    override fun setBackgroundTintMode(tintMode: PorterDuff.Mode?) {
    }

    override fun getBackground(): Drawable? = null

    @Px
    private fun dp2px(dp: Int): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        var display: Display? = null
        if (wm != null) display = wm.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display?.getMetrics(displayMetrics)
        return (dp * displayMetrics.density + 0.5f).toInt()
    }

    private fun printLog(message: String) {
        if (SHOW_LOGS) Log.d("AlbumCoverEqView", message)
    }

    private inner class BarView(
        val srcRect: RectF = RectF(),
        val dstRect: RectF = RectF()
    ) {

        fun draw(canvas: Canvas, paint: Paint) {

        }
    }

    private companion object {
        private const val SUPER_KEY = "super"
        private const val COLOR_DIVIDER_KEY = "color_thumb"
        private const val SHOW_LOGS = true
    }
}