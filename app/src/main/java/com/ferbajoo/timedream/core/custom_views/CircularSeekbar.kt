package com.ferbajoo.timedream.core.custom_views

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.Paint.Style
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.ferbajoo.timedream.R
import com.ferbajoo.timedream.core.utils.convertDpToPixel
import java.text.DecimalFormat
import java.text.NumberFormat


/**
 * Displays a touchable and circular SeekBar, an optional central circle
 * and a customizable text in the center.
 * The faster the user moves the finger across the CircularSeekBar, the faster the progress changes.
 */
class CircularSeekBar : View {

    // settable by the client through attributes and programmatically
    private var mOnCircularSeekBarChangeListener: OnCircularSeekBarChangeListener? = null
    private var mOnCenterClickedListener: OnCenterClickedListener? = null
    private var mEnabled = true
    var isIndicatorEnabled = true
        private set
    private var mMinValue = 0f
    private var mMaxValue = 100f
    /**
     * Accelerate or decelerate the change in progress relative to the user's circular scrolling movement
     * @param speedMultiplier 0-1 to decrease change, 1+ to increase change
     */
    @FloatRange(from = 0.0)
    var speedMultiplier = 1f
    private var mProgress = 0f
    var isProgressTextEnabled = true
        private set
    @FloatRange(from = 0.0, to = 1.0)
    private var mRingWidthFactor = 0.5f
    private var mProgressText: String? = null
    var isInnerCircleEnabled = true
        private set
    @ColorInt
    private var mRingColor = Color.rgb(192, 255, 140) //LIGHT LIME
    @ColorInt
    private var mInnerCircleColor = Color.WHITE
    @ColorInt
    private var mProgressTextColor = Color.BLACK
    @FloatRange(from = 0.0)
    private var mProgressTextSize = convertDpToPixel(resources, 24f)

    // settable by the client programmatically
    private var mRingPaint: Paint? = null
    private var mInnerCirclePaint: Paint? = null
    private var mProgressTextPaint: Paint? = null
    /**
     * Set the format of the progress text. <br></br>
     *
     *  * "###,###,###,##0.0" will display: 1,234.5
     *  * "###,###,###,##0.00" will display: 1,234.56
     *
     * @param format
     */
    var progressTextFormat: NumberFormat = DecimalFormat("###,###,###,##0.0")
        set(format) {
            field = format
            invalidate()
        }

    // private
    private val mViewBox = RectF()
    private val mDimAlpha = 80
    private var mGestureDetector: GestureDetector? = null
    private var mTouching = false
    @FloatRange(from = 0.0, to = 360.0)
    private var mTouchAngle = 0f
    private var mAngularVelocityTracker: AngularVelocityTracker? = null

    /**
     * Minimum possible value of the progress
     * @param min
     */
    var min: Float
        get() = mMinValue
        set(min) {
            mMinValue = min
            progress = Math.min(mMinValue, mProgress)
        }

    /**
     * Maximum possible value of the progress
     * @param max
     */
    var max: Float
        get() = mMaxValue
        set(max) {
            mMaxValue = max
            progress = Math.max(mMaxValue, mProgress)
        }

    /**
     * Returns the currently displayed value from the view. Depending on the
     * used method to show the value, this value can be percent or actual value.
     *
     * @return
     */
    /**
     * Set current value of the progress
     * @param progress
     */
    var progress: Float
        get() = mProgress
        set(progress) {
            mProgress = progress
            if (mOnCircularSeekBarChangeListener != null) {
                mOnCircularSeekBarChangeListener!!.onProgressChanged(this, mProgress, false)
            }
            invalidate()
        }

    /**
     * Set the thickness of the outer ring (touchable area), relative to the size of the whole view
     * @param factor
     */
    var ringWidthFactor: Float
        get() = mRingWidthFactor
        set(@FloatRange(from = 0.0, to = 1.0) factor) {
            mRingWidthFactor = factor
            invalidate()
        }

    /**
     * Set color for the outer ring (touchable area)
     * @param color
     */
    var ringColor: Int
        @ColorInt get() = mRingColor
        set(@ColorInt color) {
            mRingColor = color
            mRingPaint!!.color = mRingColor
            invalidate()
        }

    var innerCircleColor: Int
        @ColorInt get() = mInnerCircleColor
        set(@ColorInt color) {
            mInnerCircleColor = color
            mInnerCirclePaint!!.color = mInnerCircleColor
            invalidate()
        }

    var progressTextColor: Int
        @ColorInt get() = mProgressTextColor
        set(@ColorInt color) {
            mProgressTextColor = color
            mProgressTextPaint!!.color = mProgressTextColor
            invalidate()
        }

    var progressTextSize: Float
        get() = mProgressTextSize
        set(@FloatRange(from = 0.0) pixels) {
            mProgressTextSize = pixels
            mProgressTextPaint!!.textSize = mProgressTextSize
            invalidate()
        }

    private val diameter: Float
        get() = Math.min(width, height).toFloat()

    private val outerCircleRadius: Float
        get() = diameter / 2f

    private val innerCircleRadius: Float
        get() = outerCircleRadius * (1 - mRingWidthFactor)

    private val center: PointF
        get() = PointF((width / 2).toFloat(), (height / 2).toFloat())

    /**
     * Listen for touch-events on the ring area
     */
    interface OnCircularSeekBarChangeListener {
        fun onProgressChanged(seekBar: CircularSeekBar, progress: Float, fromUser: Boolean)

        fun onStartTrackingTouch(seekBar: CircularSeekBar)

        fun onStopTrackingTouch(seekBar: CircularSeekBar)
    }

    /**
     * Listen for singletap-events on the inner circle area
     */
    interface OnCenterClickedListener {
        fun onCenterClicked(seekBar: CircularSeekBar, progress: Float)
    }

    //region Constructor
    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }
    //endregion

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircularSeekBar,
                0,
                0)
        try {
            mEnabled = a.getBoolean(R.styleable.CircularSeekBar_enabled, mEnabled)
            isIndicatorEnabled = a.getBoolean(R.styleable.CircularSeekBar_showIndicator, isIndicatorEnabled)
            mMinValue = a.getFloat(R.styleable.CircularSeekBar_min, mMinValue)
            mMaxValue = a.getFloat(R.styleable.CircularSeekBar_max, mMaxValue)
            speedMultiplier = a.getFloat(R.styleable.CircularSeekBar_speedMultiplier, speedMultiplier)
            mProgress = a.getFloat(R.styleable.CircularSeekBar_progress, mProgress)
            isProgressTextEnabled = a.getBoolean(R.styleable.CircularSeekBar_showProgressText, isProgressTextEnabled)
            mRingWidthFactor = a.getFloat(R.styleable.CircularSeekBar_ringWidth, mRingWidthFactor)
            mProgressText = a.getString(R.styleable.CircularSeekBar_progressText)
            isInnerCircleEnabled = a.getBoolean(R.styleable.CircularSeekBar_showInnerCircle, isInnerCircleEnabled)
            mRingColor = a.getColor(R.styleable.CircularSeekBar_ringColor, mRingColor)
            mInnerCircleColor = a.getColor(R.styleable.CircularSeekBar_innerCircleColor, mInnerCircleColor)
            mProgressTextColor = a.getColor(R.styleable.CircularSeekBar_progressTextColor, mProgressTextColor)
            mProgressTextSize = convertDpToPixel(resources, a.getFloat(R.styleable.CircularSeekBar_progressTextSize, mProgressTextSize))
        } finally {
            a.recycle()
        }

        mRingPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRingPaint!!.style = Style.FILL
        mRingPaint!!.color = mRingColor

        mInnerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mInnerCirclePaint!!.style = Style.FILL
        mInnerCirclePaint!!.color = mInnerCircleColor



        mProgressTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mProgressTextPaint!!.style = Style.STROKE
        mProgressTextPaint!!.textAlign = Align.CENTER
        mProgressTextPaint!!.color = mProgressTextColor
        mProgressTextPaint!!.textSize = mProgressTextSize

        mGestureDetector = GestureDetector(getContext(), GestureListener())
    }

    //region Lifecycle
    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)

        initViewBox()
        mAngularVelocityTracker = AngularVelocityTracker(center.x, center.y)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawWholeCircle(canvas)

        if (isIndicatorEnabled && mTouching) {
            drawProgressArc(canvas)
        }

        if (isInnerCircleEnabled) {
            drawInnerCircle(canvas)
        }

        if (isProgressTextEnabled) {
            if (mProgressText != null) {
                drawCustomText(canvas)
            } else {
                drawProgressText(canvas)
            }
        }
    }
    //endregion

    //region Touches
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mEnabled) {
            // if the detector recognized a gesture, consume it
            if (mGestureDetector!!.onTouchEvent(event)) {
                return true
            }

            // get the distance from the touch to the center of the view
            val distance = distanceToCenter(event.x, event.y)
            val outerCircleRadius = outerCircleRadius
            val innerCircleRadius = innerCircleRadius

            // touch gestures only work when touches are made exactly on the bar/arc
            if (distance >= innerCircleRadius && distance < outerCircleRadius) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mTouching = true
                        trackTouchStart(event)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        mTouching = true
                        trackTouchMove(event)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        mTouching = false
                        trackTouchStop()
                    }
                }
            } else {
                mTouching = false
                mAngularVelocityTracker!!.clear()
            }

            invalidate()
            return true
        } else {
            return super.onTouchEvent(event)
        }
    }

    private fun trackTouchStart(event: MotionEvent) {
        mAngularVelocityTracker!!.clear()
        updateProgress(event.x, event.y, mAngularVelocityTracker!!.angularVelocity)
        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener!!.onStartTrackingTouch(this)
        }
    }

    private fun trackTouchMove(event: MotionEvent) {
        mAngularVelocityTracker!!.addMovement(event)
        updateProgress(event.x, event.y, mAngularVelocityTracker!!.angularVelocity)
        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener!!.onProgressChanged(this, mProgress, true)
        }
    }

    private fun trackTouchStop() {
        mAngularVelocityTracker!!.clear()
        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener!!.onStopTrackingTouch(this)
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            // get the distance from the touch to the center of the view
            val distance = distanceToCenter(event.x, event.y)
            val r = outerCircleRadius

            // touch gestures only work when touches are made exactly on the bar/arc
            if (mOnCenterClickedListener != null && distance <= r - r * mRingWidthFactor) {
                mOnCenterClickedListener!!.onCenterClicked(this@CircularSeekBar, mProgress)
            }
            return false
        }
    }
    //endregion

    //region Public listener
    /**
     * Set a listener for touch-events related to the outer ring of the CircularSeekBar
     * @param listener
     */
    fun setOnCircularSeekBarChangeListener(listener: OnCircularSeekBarChangeListener?) {
        mOnCircularSeekBarChangeListener = listener
    }

    /**
     * Set a listener for singletap-events related to the central ring of the CircularSeekBar
     * @param listener
     */
    fun setOnCenterClickedListener(listener: OnCenterClickedListener?) {
        mOnCenterClickedListener = listener
    }
    //endregion

    //region Public attribute
    /**
     * Enable/disable the visual indicator shown under the touched area
     * @param enable
     */
    fun setIndicator(enable: Boolean) {
        isIndicatorEnabled = enable
        invalidate()
    }

    /**
     * Enable touch gestures on the CircularSeekBar
     * @param enable
     */
    override fun setEnabled(enable: Boolean) {
        mEnabled = enable
        invalidate()
    }

    override fun isEnabled(): Boolean {
        return mEnabled
    }

    /**
     * Draw a text with the current progress in the center of the view
     * @param enabled
     */
    fun setProgressText(enabled: Boolean) {
        isProgressTextEnabled = enabled
        invalidate()
    }

    /**
     * Set fixed text to be drawn in the center of the view
     * @param text
     */
    fun setProgressText(text: String?) {
        mProgressText = text
        invalidate()
    }

    fun getProgressText(): String? {
        return mProgressText
    }

    /**
     * Enable/disable inner circle display
     * @param enable
     */
    fun setInnerCircle(enable: Boolean) {
        isInnerCircleEnabled = enable
        invalidate()
    }
    //endregion

    //region Public mutator
    /**
     * Set the Paint used to draw the outer ring (touchable area)
     * @param paint
     */
    fun setRingPaint(paint: Paint) {
        mRingPaint = paint
        invalidate()
    }

    /**
     * Set the Paint used to draw the inner circle
     * @param paint
     */
    fun setInnerCirclePaint(paint: Paint) {
        mInnerCirclePaint = paint
        invalidate()
    }

    /**
     * Set the Paint used to draw the progress text or fixed custom text
     * @param paint
     */
    fun setProgressTextPaint(paint: Paint) {
        mProgressTextPaint = paint
        invalidate()
    }
    //endregion

    //region Private draw
    private fun drawWholeCircle(c: Canvas) {
        mRingPaint!!.alpha = mDimAlpha
        mRingPaint!!.color = mRingColor
        c.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), outerCircleRadius, mRingPaint!!)
    }

    private fun drawInnerCircle(c: Canvas) {
        c.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), innerCircleRadius, mInnerCirclePaint!!)
    }

    private fun drawProgressArc(c: Canvas) {
        mRingPaint!!.alpha = 255
        mRingPaint!!.color = ContextCompat.getColor(context,R.color.colorWhite)
        c.drawArc(mViewBox, mTouchAngle - 105, 30f, true, mRingPaint!!)
    }

    private fun drawProgressText(c: Canvas) {
        if (mAngularVelocityTracker != null) {
            c.drawText(progressTextFormat.format(mProgress.toDouble()),
                    (width / 2).toFloat(),
                    height / 2 + mProgressTextPaint!!.descent(),
                    mProgressTextPaint!!)
        }
    }

    private fun drawCustomText(c: Canvas) {
        c.drawText(mProgressText!!,
                (width / 2).toFloat(),
                height / 2 + mProgressTextPaint!!.descent(),
                mProgressTextPaint!!)
    }
    //endregion

    //region Private
    private fun initViewBox() {
        val width = width
        val height = height
        val diameter = diameter

        mViewBox.set(width / 2 - diameter / 2, height / 2 - diameter / 2, width / 2 + diameter / 2, height / 2 + diameter / 2)
    }

    /**
     * update display with the given touch position
     *
     * @param x
     * @param y
     */
    private fun updateProgress(x: Float, y: Float, speed: Float) {
        // calculate the touch-angle
        mTouchAngle = getAngle(x, y)

        // calculate the new value depending on angle
        var newVal = mProgress + mMaxValue / 100 * speed * speedMultiplier
        newVal = Math.min(newVal, mMaxValue)
        newVal = Math.max(newVal, mMinValue)
        mProgress = newVal
    }

    /**
     * return angle relative to the view center for the given point on the chart in degrees.
     *
     * @param x
     * @param y
     * @return angle in degrees. 0° is NORTH
     */
    @FloatRange(from = 0.0, to = 360.0)
    private fun getAngle(x: Float, y: Float): Float {
        val c = center
        return (-Math.toDegrees(Math.atan2((c.x - x).toDouble(), (c.y - y).toDouble()))).toFloat()
    }

    private fun distanceToCenter(x: Float, y: Float): Float {
        val c = center
        return Math.sqrt(Math.pow((x - c.x).toDouble(), 2.0) + Math.pow((y - c.y).toDouble(), 2.0)).toFloat()
    }
    //endregion
}
