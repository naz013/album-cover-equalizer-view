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

    @ColorInt
    private var dividerColor = Color.WHITE

    @ColorInt
    private var barColor = Color.WHITE
    private var cover: Bitmap? = null
    private var numberOfBars = 8
    private var dividerWidth = 2f
    private var animationStiffness = SpringForce.STIFFNESS_LOW
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
        attrs?.let {
            val a =
                context.theme.obtainStyledAttributes(attrs, R.styleable.AlbumCoverEqView, 0, 0)
            try {
                barColor = a.getColor(R.styleable.AlbumCoverEqView_acv_barColor, barColor)
                dividerColor =
                    a.getColor(R.styleable.AlbumCoverEqView_acv_dividerColor, dividerColor)
                numberOfBars = a.getInt(R.styleable.AlbumCoverEqView_acv_numberOfBars, numberOfBars)
                animationStiffness = stiffness(
                    a.getInt(
                        R.styleable.AlbumCoverEqView_acv_animationSpeed,
                        ANIMATION_SLOW
                    )
                )
                dividerWidth = a.getDimensionPixelSize(
                    R.styleable.AlbumCoverEqView_acv_dividerWidth,
                    dividerWidth.toInt()
                ).toFloat()
                val imageId = a.getResourceId(R.styleable.AlbumCoverEqView_acv_coverDrawable, 0)
                if (imageId != 0) {
                    cover = BitmapFactory.decodeResource(resources, imageId)
                }
            } catch (e: Exception) {
                printLog("initView: " + e.localizedMessage)
            } finally {
                a.recycle()
            }
        }
        showFullCover()
    }

    fun showFullCover() {
        val array = FloatArray(numberOfBars)
        for (i in 0 until numberOfBars) array[i] = 0.0f
        setWaveHeights(array)
    }

    fun setAnimationSpeed(speed: Int) {
        animationStiffness = stiffness(speed)
    }

    fun setDividerWidth(@Px value: Float) {
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
    fun getDividerColor() = dividerColor

    fun setDividerColor(@ColorInt color: Int) {
        dividerColor = color
        invalidate()
    }

    @ColorInt
    fun getBarColor() = barColor

    fun setBarColor(@ColorInt color: Int) {
        barColor = color
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
            barColor = state.getInt(COLOR_BAR_KEY, barColor)
            dividerColor = state.getInt(COLOR_DIVIDER_KEY, dividerColor)
            numberOfBars = state.getInt(BARS_COUNT_KEY, numberOfBars)
            dividerWidth = state.getFloat(DIVIDER_WIDTH_KEY, dividerWidth)
            animationStiffness = state.getFloat(STIFFNESS_KEY, animationStiffness)
            super.onRestoreInstanceState(state.getParcelable(SUPER_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedInstance = Bundle()
        savedInstance.putParcelable(SUPER_KEY, super.onSaveInstanceState())
        savedInstance.putInt(COLOR_DIVIDER_KEY, dividerColor)
        savedInstance.putInt(COLOR_BAR_KEY, barColor)
        savedInstance.putInt(BARS_COUNT_KEY, numberOfBars)
        savedInstance.putFloat(DIVIDER_WIDTH_KEY, dividerWidth)
        savedInstance.putFloat(STIFFNESS_KEY, animationStiffness)
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
                spring.stiffness = animationStiffness
                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                start()
            }
        }

        fun draw(canvas: Canvas, paint: Paint) {
            if (barType == BarType.DIVIDER) {
                paint.color = dividerColor
                canvas.drawRect(topBar, paint)
            } else {
                paint.color = barColor
                canvas.drawRect(topBar, paint)
                canvas.drawRect(bottomBar, paint)
            }
        }
    }

    private fun stiffness(value: Int): Float {
        return when (value) {
            0 -> SpringForce.STIFFNESS_LOW
            1 -> SpringForce.STIFFNESS_MEDIUM
            2 -> SpringForce.STIFFNESS_HIGH
            else -> SpringForce.STIFFNESS_LOW
        }
    }

    private enum class BarType {
        NORMAL, DIVIDER
    }

    companion object {
        const val ANIMATION_SLOW = 0
        const val ANIMATION_MEDIUM = 1
        const val ANIMATION_FAST = 2

        private const val SUPER_KEY = "super"
        private const val COLOR_DIVIDER_KEY = "color_divider"
        private const val COLOR_BAR_KEY = "color_bar"
        private const val STIFFNESS_KEY = "stiffness"
        private const val DIVIDER_WIDTH_KEY = "divider_width"
        private const val BARS_COUNT_KEY = "bars_count"
        private const val SHOW_LOGS = true
    }
}