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
import androidx.core.graphics.drawable.toBitmap
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

class AlbumCoverEqView : View {

    private var bars = listOf<BarView>()
    private val barPaint = Paint().apply {
        this.color = Color.WHITE
        this.alpha = 255
        this.style = Paint.Style.FILL
    }
    private var cover: Bitmap? = null
    private var numberOfBars = 8
    private var dividerWidth = 2f
    private val viewBounds = Rect()

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
        dividerWidth = dp2px(4).toFloat()
    }

    fun setDividerWidth(value: Float) {
        dividerWidth = value
        calculateBars(measuredWidth, measuredHeight)
        invalidate()
    }

    fun getNumberOfBars() = numberOfBars

    fun setNumberOfBars(value: Int) {
        numberOfBars = value
        calculateBars(measuredWidth, measuredHeight)
        invalidate()
    }

    fun setWaveHeights(floatArray: FloatArray) {
        if (floatArray.size != numberOfBars) {
            throw IllegalArgumentException("Array size must be equal to number of bars!")
        }
        bars.forEachIndexed { index, barView ->
            if (index % 2 == 0) {
                barView.targetPercent = floatArray[index / 2]
                barView.animate()
            }
        }
    }

    @ColorInt
    fun getDividerColor() = barPaint.color

    fun setDividerColor(@ColorInt color: Int) {
        barPaint.color = color
        invalidate()
    }

    fun getCoverImage() = cover

    fun setCoverImage(coverImage: Bitmap?) {
        cover = coverImage
        invalidate()
    }

    fun setCoverImage(coverImage: Drawable?) {
        cover = coverImage?.toBitmap(measuredWidth, measuredHeight)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            cover?.let { canvas.drawBitmap(it, null, viewBounds, null) }
            bars.forEach { it.draw(canvas, barPaint) }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        calculateBars(width, height)
    }

    private fun calculateBars(width: Int, height: Int) {
        printLog("calculateBars: $width, $height")
        viewBounds.left = 0
        viewBounds.top = 0
        viewBounds.right = width
        viewBounds.bottom = height

        val numberOfDividers = numberOfBars - 1
        val barWidth = (width - (numberOfDividers * dividerWidth)) / numberOfBars
        var left = 0f
        val list = mutableListOf<BarView>()
        for (i in 0 until (numberOfBars * 2 - 1)) {
            left += if (i % 2 == 0) {
                val bounds = RectF(left, 0f, left + barWidth, height.toFloat())
                list.add(BarView(bounds, BarType.NORMAL))
                barWidth
            } else {
                val bounds = RectF(left, 0f, left + dividerWidth, height.toFloat())
                list.add(BarView(bounds, BarType.DIVIDER))
                dividerWidth
            }
        }

        bars = list
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            barPaint.color = state.getInt(COLOR_DIVIDER_KEY, barPaint.color)
            super.onRestoreInstanceState(state.getParcelable(SUPER_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedInstance = Bundle()
        savedInstance.putParcelable(SUPER_KEY, super.onSaveInstanceState())
        savedInstance.putInt(COLOR_DIVIDER_KEY, barPaint.color)
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

    private inner class BarView(private val bounds: RectF = RectF(), private val barType: BarType) {

        var percent: Float = 100.0f
            set(value) {
                field = value
                calculateBars(value)
            }
        var targetPercent = 100.0f
        private val topBar = RectF(bounds)
        private val bottomBar = RectF(bounds)
        private val percentPropertyAnim = object : FloatPropertyCompat<Float>("bar_percent") {
            override fun setValue(point: Float?, value: Float) {
                percent = value
                invalidate()
            }

            override fun getValue(point: Float?): Float {
                return point ?: 50.0f
            }
        }

        private fun calculateBars(value: Float) {
            if (barType == BarType.NORMAL) {
                val barHeight = bounds.height() / 2f * (value / 100f)

                topBar.top = bounds.top
                topBar.bottom = bounds.top + barHeight

                bottomBar.bottom = bounds.bottom
                bottomBar.top = bounds.bottom - barHeight
            }
        }

        fun animate() {
            if (barType == BarType.DIVIDER) return
            if (targetPercent <= 0.0f) return
            SpringAnimation(percent, percentPropertyAnim, targetPercent).apply {
                spring.stiffness = SpringForce.STIFFNESS_VERY_LOW
                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                start()
            }
        }

        fun draw(canvas: Canvas, paint: Paint) {
            if (barType == BarType.DIVIDER) {
                canvas.drawRect(topBar, paint)
            } else {
                canvas.drawRect(topBar, paint)
                canvas.drawRect(bottomBar, paint)
            }
        }
    }

    private enum class BarType {
        NORMAL, DIVIDER
    }

    private companion object {
        private const val SUPER_KEY = "super"
        private const val COLOR_DIVIDER_KEY = "color_thumb"
        private const val SHOW_LOGS = true
    }
}