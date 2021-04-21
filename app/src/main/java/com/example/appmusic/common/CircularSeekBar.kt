package com.firekernel.musicplayer.ui.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.appmusic.R


class CircularSeekBar : View {
    /**
     * Used to scale the dp units to pixels
     */
    private val DPTOPX_SCALE = resources.displayMetrics.density

    /**
     * Minimum touch target size in DP. 48dp is the Android design recommendation
     */
    private val MIN_TOUCH_TARGET_DP = 48f

    /**
     * `Paint` instance used to draw the inactive circle.
     */
    private var mCirclePaint: Paint? = null

    /**
     * `Paint` instance used to draw the circle fill.
     */
    private var mCircleFillPaint: Paint? = null

    /**
     * `Paint` instance used to draw the active circle (represents progress).
     */
    private var mCircleProgressPaint: Paint? = null

    /**
     * `Paint` instance used to draw the glow from the active circle.
     */
    private var mCircleProgressGlowPaint: Paint? = null

    /**
     * `Paint` instance used to draw the center of the pointer.
     * Note: This is broken on 4.0+, as BlurMasks do not work with hardware acceleration.
     */
    private var mPointerPaint: Paint? = null

    /**
     * `Paint` instance used to draw the halo of the pointer.
     * Note: The halo is the part that changes transparency.
     */
    private var mPointerHaloPaint: Paint? = null

    /**
     * `Paint` instance used to draw the border of the pointer, outside of the halo.
     */
    private var mPointerHaloBorderPaint: Paint? = null

    /**
     * The width of the circle (in pixels).
     */
    private var mCircleStrokeWidth = 0f

    /**
     * The X radius of the circle (in pixels).
     */
    private var mCircleXRadius = 0f

    /**
     * The Y radius of the circle (in pixels).
     */
    private var mCircleYRadius = 0f

    /**
     * The radius of the pointer (in pixels).
     */
    private var mPointerRadius = 0f

    /**
     * The width of the pointer halo (in pixels).
     */
    private var mPointerHaloWidth = 0f

    /**
     * The width of the pointer halo border (in pixels).
     */
    private var mPointerHaloBorderWidth = 0f

    /**
     * Start angle of the CircularSeekBar.
     * Note: If mStartAngle and mEndAngle are set to the same angle, 0.1 is subtracted
     * from the mEndAngle to make the circle function properly.
     */
    private var mStartAngle = 0f

    /**
     * End angle of the CircularSeekBar.
     * Note: If mStartAngle and mEndAngle are set to the same angle, 0.1 is subtracted
     * from the mEndAngle to make the circle function properly.
     */
    private var mEndAngle = 0f

    /**
     * `RectF` that represents the circle (or ellipse) of the seekbar.
     */
    private val mCircleRectF = RectF()

    /**
     * Holds the color value for `mPointerPaint` before the `Paint` instance is created.
     */
    private var mPointerColor = DEFAULT_POINTER_COLOR

    /**
     * Holds the color value for `mPointerHaloPaint` before the `Paint` instance is created.
     */
    private var mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR

    /**
     * Holds the color value for `mPointerHaloPaint` before the `Paint` instance is created.
     */
    private var mPointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH

    /**
     * Holds the color value for `mCirclePaint` before the `Paint` instance is created.
     */
    private var mCircleColor = DEFAULT_CIRCLE_COLOR

    /**
     * Holds the color value for `mCircleFillPaint` before the `Paint` instance is created.
     */
    private var mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR

    /**
     * Holds the color value for `mCircleProgressPaint` before the `Paint` instance is created.
     */
    private var mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR

    /**
     * Holds the alpha value for `mPointerHaloPaint`.
     */
    private var mPointerAlpha = DEFAULT_POINTER_ALPHA

    /**
     * Holds the OnTouch alpha value for `mPointerHaloPaint`.
     */
    private var mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH

    /**
     * Distance (in degrees) that the the circle/semi-circle makes up.
     * This amount represents the max of the circle in degrees.
     */
    private var mTotalCircleDegrees = 0f

    /**
     * Distance (in degrees) that the current progress makes up in the circle.
     */
    private var mProgressDegrees = 0f

    /**
     * `Path` used to draw the circle/semi-circle.
     */
    private var mCirclePath: Path? = null

    /**
     * `Path` used to draw the progress on the circle.
     */
    private var mCircleProgressPath: Path? = null

    /**
     * Max value that this CircularSeekBar is representing.
     */
    private var mMax = 0

    /**
     * Progress value that this CircularSeekBar is representing.
     */
    private var mProgress = 0

    /**
     * If true, then the user can specify the X and Y radii.
     * If false, then the View itself determines the size of the CircularSeekBar.
     */
    private var mCustomRadii = false

    /**
     * Maintain a perfect circle (equal x and y radius), regardless of view or custom attributes.
     * The smaller of the two radii will always be used in this case.
     * The default is to be a circle and not an ellipse, due to the behavior of the ellipse.
     */
    private var mMaintainEqualCircle = false

    /**
     * Once a user has touched the circle, this determines if moving outside the circle is able
     * to change the position of the pointer (and in turn, the progress).
     */
    private var mMoveOutsideCircle = false

    /**
     * Used for enabling/disabling the lock option for easier hitting of the 0 progress mark.
     */
    var isLockEnabled = true

    /**
     * Used for when the user moves beyond the start of the circle when moving counter clockwise.
     * Makes it easier to hit the 0 progress mark.
     */
    private var lockAtStart = true

    /**
     * Used for when the user moves beyond the end of the circle when moving clockwise.
     * Makes it easier to hit the 100% (max) progress mark.
     */
    private var lockAtEnd = false

    /**
     * When the user is touching the circle on ACTION_DOWN, this is set to true.
     * Used when touching the CircularSeekBar.
     */
    private var mUserIsMovingPointer = false

    /**
     * Represents the clockwise distance from `mStartAngle` to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private var cwDistanceFromStart = 0f

    /**
     * Represents the counter-clockwise distance from `mStartAngle` to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private var ccwDistanceFromStart = 0f

    /**
     * Represents the clockwise distance from `mEndAngle` to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private var cwDistanceFromEnd = 0f

    /**
     * Represents the counter-clockwise distance from `mEndAngle` to the touch angle.
     * Used when touching the CircularSeekBar.
     * Currently unused, but kept just in case.
     */
    private var ccwDistanceFromEnd = 0f

    /**
     * The previous touch action value for `cwDistanceFromStart`.
     * Used when touching the CircularSeekBar.
     */
    private var lastCWDistanceFromStart = 0f

    /**
     * Represents the clockwise distance from `mPointerPosition` to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private var cwDistanceFromPointer = 0f

    /**
     * Represents the counter-clockwise distance from `mPointerPosition` to the touch angle.
     * Used when touching the CircularSeekBar.
     */
    private var ccwDistanceFromPointer = 0f

    /**
     * True if the user is moving clockwise around the circle, false if moving counter-clockwise.
     * Used when touching the CircularSeekBar.
     */
    private var mIsMovingCW = false

    /**
     * The width of the circle used in the `RectF` that is used to draw it.
     * Based on either the View width or the custom X radius.
     */
    private var mCircleWidth = 0f

    /**
     * The height of the circle used in the `RectF` that is used to draw it.
     * Based on either the View width or the custom Y radius.
     */
    private var mCircleHeight = 0f

    /**
     * Represents the progress mark on the circle, in geometric degrees.
     * This is not provided by the user; it is calculated;
     */
    private var mPointerPosition = 0f

    /**
     * Pointer position in terms of X and Y coordinates.
     */
    private val mPointerPositionXY = FloatArray(2)

    /**
     * Listener.
     */
    private var mOnCircularSeekBarChangeListener: OnCircularSeekBarChangeListener? = null

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    /**
     * Initialize the CircularSeekBar with the attributes from the XML style.
     * Uses the defaults defined at the top of this file when an attribute is not specified by the user.
     *
     * @param attrArray TypedArray containing the attributes.
     */
    private fun initAttributes(attrArray: TypedArray) {
        mCircleXRadius = attrArray.getFloat(
            R.styleable.CircularSeekBar_circle_x_radius,
            DEFAULT_CIRCLE_X_RADIUS
        ) * DPTOPX_SCALE
        mCircleYRadius = attrArray.getFloat(
            R.styleable.CircularSeekBar_circle_y_radius,
            DEFAULT_CIRCLE_Y_RADIUS
        ) * DPTOPX_SCALE
        mPointerRadius = attrArray.getFloat(
            R.styleable.CircularSeekBar_pointer_radius,
            DEFAULT_POINTER_RADIUS
        ) * DPTOPX_SCALE
        mPointerHaloWidth = attrArray.getFloat(
            R.styleable.CircularSeekBar_pointer_halo_width,
            DEFAULT_POINTER_HALO_WIDTH
        ) * DPTOPX_SCALE
        mPointerHaloBorderWidth = attrArray.getFloat(
            R.styleable.CircularSeekBar_pointer_halo_border_width,
            DEFAULT_POINTER_HALO_BORDER_WIDTH
        ) * DPTOPX_SCALE
        mCircleStrokeWidth = attrArray.getFloat(
            R.styleable.CircularSeekBar_circle_stroke_width,
            DEFAULT_CIRCLE_STROKE_WIDTH
        ) * DPTOPX_SCALE
        var tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_color)
        if (tempColor != null) {
            mPointerColor = try {
                Color.parseColor(tempColor)
            } catch (e: IllegalArgumentException) {
                DEFAULT_POINTER_COLOR
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_halo_color)
        if (tempColor != null) {
            mPointerHaloColor = try {
                Color.parseColor(tempColor)
            } catch (e: IllegalArgumentException) {
                DEFAULT_POINTER_HALO_COLOR
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_halo_color_ontouch)
        if (tempColor != null) {
            mPointerHaloColorOnTouch = try {
                Color.parseColor(tempColor)
            } catch (e: IllegalArgumentException) {
                DEFAULT_POINTER_HALO_COLOR_ONTOUCH
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_color)
        if (tempColor != null) {
            mCircleColor = try {
                Color.parseColor(tempColor)
            } catch (e: IllegalArgumentException) {
                DEFAULT_CIRCLE_COLOR
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_progress_color)
        if (tempColor != null) {
            mCircleProgressColor = try {
                Color.parseColor(tempColor)
            } catch (e: IllegalArgumentException) {
                DEFAULT_CIRCLE_PROGRESS_COLOR
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_fill)
        if (tempColor != null) {
            mCircleFillColor = try {
                Color.parseColor(tempColor)
            } catch (e: IllegalArgumentException) {
                DEFAULT_CIRCLE_FILL_COLOR
            }
        }
        mPointerAlpha = Color.alpha(mPointerHaloColor)
        mPointerAlphaOnTouch = attrArray.getInt(
            R.styleable.CircularSeekBar_pointer_alpha_ontouch,
            DEFAULT_POINTER_ALPHA_ONTOUCH
        )
        if (mPointerAlphaOnTouch > 255 || mPointerAlphaOnTouch < 0) {
            mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH
        }
        mMax = attrArray.getInt(R.styleable.CircularSeekBar_max, DEFAULT_MAX)
        mProgress = attrArray.getInt(R.styleable.CircularSeekBar_progress, DEFAULT_PROGRESS)
        mCustomRadii = attrArray.getBoolean(
            R.styleable.CircularSeekBar_use_custom_radii,
            DEFAULT_USE_CUSTOM_RADII
        )
        mMaintainEqualCircle = attrArray.getBoolean(
            R.styleable.CircularSeekBar_maintain_equal_circle,
            DEFAULT_MAINTAIN_EQUAL_CIRCLE
        )
        mMoveOutsideCircle = attrArray.getBoolean(
            R.styleable.CircularSeekBar_move_outside_circle,
            DEFAULT_MOVE_OUTSIDE_CIRCLE
        )
        isLockEnabled =
            attrArray.getBoolean(R.styleable.CircularSeekBar_lock_enabled, DEFAULT_LOCK_ENABLED)

        // Modulo 360 right now to avoid constant conversion
        mStartAngle = (360f + attrArray.getFloat(
            R.styleable.CircularSeekBar_start_angle,
            DEFAULT_START_ANGLE
        ) % 360f) % 360f
        mEndAngle = (360f + attrArray.getFloat(
            R.styleable.CircularSeekBar_end_angle,
            DEFAULT_END_ANGLE
        ) % 360f) % 360f
        if (mStartAngle == mEndAngle) {
            //mStartAngle = mStartAngle + 1f;
            mEndAngle = mEndAngle - .1f
        }
    }

    /**
     * Initializes the `Paint` objects with the appropriate styles.
     */
    private fun initPaints() {
        mCirclePaint = Paint()
        mCirclePaint!!.isAntiAlias = true
        mCirclePaint!!.isDither = true
        mCirclePaint!!.color = mCircleColor
        mCirclePaint!!.strokeWidth = mCircleStrokeWidth
        mCirclePaint!!.style = Paint.Style.STROKE
        mCirclePaint!!.strokeJoin = Paint.Join.ROUND
        mCirclePaint!!.strokeCap = Paint.Cap.ROUND
        mCircleFillPaint = Paint()
        mCircleFillPaint!!.isAntiAlias = true
        mCircleFillPaint!!.isDither = true
        mCircleFillPaint!!.color = mCircleFillColor
        mCircleFillPaint!!.style = Paint.Style.FILL
        mCircleProgressPaint = Paint()
        mCircleProgressPaint!!.isAntiAlias = true
        mCircleProgressPaint!!.isDither = true
        mCircleProgressPaint!!.color = mCircleProgressColor
        mCircleProgressPaint!!.strokeWidth = mCircleStrokeWidth
        mCircleProgressPaint!!.style = Paint.Style.STROKE
        mCircleProgressPaint!!.strokeJoin = Paint.Join.ROUND
        mCircleProgressPaint!!.strokeCap = Paint.Cap.ROUND
        mCircleProgressGlowPaint = Paint()
        mCircleProgressGlowPaint!!.set(mCircleProgressPaint)
        mCircleProgressGlowPaint!!.maskFilter =
            BlurMaskFilter(5f * DPTOPX_SCALE, BlurMaskFilter.Blur.NORMAL)
        mPointerPaint = Paint()
        mPointerPaint!!.isAntiAlias = true
        mPointerPaint!!.isDither = true
        mPointerPaint!!.style = Paint.Style.FILL
        mPointerPaint!!.color = mPointerColor
        mPointerPaint!!.strokeWidth = mPointerRadius
        mPointerHaloPaint = Paint()
        mPointerHaloPaint!!.set(mPointerPaint)
        mPointerHaloPaint!!.color = mPointerHaloColor
        mPointerHaloPaint!!.alpha = mPointerAlpha
        mPointerHaloPaint!!.strokeWidth = mPointerRadius + mPointerHaloWidth
        mPointerHaloBorderPaint = Paint()
        mPointerHaloBorderPaint!!.set(mPointerPaint)
        mPointerHaloBorderPaint!!.strokeWidth = mPointerHaloBorderWidth
        mPointerHaloBorderPaint!!.style = Paint.Style.STROKE
    }

    /**
     * Calculates the total degrees between mStartAngle and mEndAngle, and sets mTotalCircleDegrees
     * to this value.
     */
    private fun calculateTotalDegrees() {
        mTotalCircleDegrees =
            (360f - (mStartAngle - mEndAngle)) % 360f // Length of the entire circle/arc
        if (mTotalCircleDegrees <= 0f) {
            mTotalCircleDegrees = 360f
        }
    }

    /**
     * Calculate the degrees that the progress represents. Also called the sweep angle.
     * Sets mProgressDegrees to that value.
     */
    private fun calculateProgressDegrees() {
        mProgressDegrees = mPointerPosition - mStartAngle // Verified
        mProgressDegrees =
            if (mProgressDegrees < 0) 360f + mProgressDegrees else mProgressDegrees // Verified
    }

    /**
     * Calculate the pointer position (and the end of the progress arc) in degrees.
     * Sets mPointerPosition to that value.
     */
    private fun calculatePointerAngle() {
        val progressPercent = mProgress.toFloat() / mMax.toFloat()
        mPointerPosition = progressPercent * mTotalCircleDegrees + mStartAngle
        mPointerPosition = mPointerPosition % 360f
    }

    private fun calculatePointerXYPosition() {
        var pm = PathMeasure(mCircleProgressPath, false)
        var returnValue = pm.getPosTan(pm.length, mPointerPositionXY, null)
        if (!returnValue) {
            pm = PathMeasure(mCirclePath, false)
            returnValue = pm.getPosTan(0f, mPointerPositionXY, null)
        }
    }

    /**
     * Initialize the `Path` objects with the appropriate values.
     */
    private fun initPaths() {
        mCirclePath = Path()
        mCirclePath!!.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees)
        mCircleProgressPath = Path()
        mCircleProgressPath!!.addArc(mCircleRectF, mStartAngle, mProgressDegrees)
    }

    /**
     * Initialize the `RectF` objects with the appropriate values.
     */
    private fun initRects() {
        mCircleRectF[-mCircleWidth, -mCircleHeight, mCircleWidth] = mCircleHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate((this.width / 2).toFloat(), (this.height / 2).toFloat())
        canvas.drawPath(mCirclePath!!, mCirclePaint!!)
        canvas.drawPath(mCircleProgressPath!!, mCircleProgressGlowPaint!!)
        canvas.drawPath(mCircleProgressPath!!, mCircleProgressPaint!!)
        canvas.drawPath(mCirclePath!!, mCircleFillPaint!!)
        canvas.drawCircle(
            mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius + mPointerHaloWidth,
            mPointerHaloPaint!!
        )
        canvas.drawCircle(
            mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius,
            mPointerPaint!!
        )
        if (mUserIsMovingPointer) {
            canvas.drawCircle(
                mPointerPositionXY[0],
                mPointerPositionXY[1],
                mPointerRadius + mPointerHaloWidth + mPointerHaloBorderWidth / 2f,
                mPointerHaloBorderPaint!!
            )
        }
    }
    /**
     * Get the progress of the CircularSeekBar.
     *
     * @return The progress of the CircularSeekBar.
     */
    /**
     * Set the progress of the CircularSeekBar.
     * If the progress is the same, then any listener will not receive a onProgressChanged event.
     *
     * @param progress The progress to set the CircularSeekBar to.
     */
    var progress: Int
        get() = Math.round(mMax.toFloat() * mProgressDegrees / mTotalCircleDegrees)
        set(progress) {
            if (mProgress != progress) {
                mProgress = progress
                if (mOnCircularSeekBarChangeListener != null) {
                    mOnCircularSeekBarChangeListener!!.onProgressChanged(this, progress, false)
                }
                recalculateAll()
                invalidate()
            }
        }

    private fun setProgressBasedOnAngle(angle: Float) {
        mPointerPosition = angle
        calculateProgressDegrees()
        mProgress = Math.round(mMax.toFloat() * mProgressDegrees / mTotalCircleDegrees)
    }

    private fun recalculateAll() {
        calculateTotalDegrees()
        calculatePointerAngle()
        calculateProgressDegrees()
        initRects()
        initPaths()
        calculatePointerXYPosition()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        if (mMaintainEqualCircle) {
            val min = Math.min(width, height)
            setMeasuredDimension(min, min)
        } else {
            setMeasuredDimension(width, height)
        }

        // Set the circle width and height based on the view for the moment
        mCircleHeight =
            height.toFloat() / 2f - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
        mCircleWidth =
            width.toFloat() / 2f - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f

        // If it is not set to use custom
        if (mCustomRadii) {
            // Check to make sure the custom radii are not out of the view. If they are, just use the view values
            if (mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth < mCircleHeight) {
                mCircleHeight =
                    mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
            }
            if (mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth < mCircleWidth) {
                mCircleWidth =
                    mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth * 1.5f
            }
        }
        if (mMaintainEqualCircle) { // Applies regardless of how the values were determined
            val min = Math.min(mCircleHeight, mCircleWidth)
            mCircleHeight = min
            mCircleWidth = min
        }
        recalculateAll()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Convert coordinates to our internal coordinate system
        val x = event.x - width / 2
        val y = event.y - height / 2

        // Get the distance from the center of the circle in terms of x and y
        val distanceX = mCircleRectF.centerX() - x
        val distanceY = mCircleRectF.centerY() - y

        // Get the distance from the center of the circle in terms of a radius
        val touchEventRadius =
            Math.sqrt(Math.pow(distanceX.toDouble(), 2.0) + Math.pow(distanceY.toDouble(), 2.0))
                .toFloat()
        val minimumTouchTarget =
            MIN_TOUCH_TARGET_DP * DPTOPX_SCALE // Convert minimum touch target into px
        var additionalRadius: Float // Either uses the minimumTouchTarget size or larger if the ring/pointer is larger
        additionalRadius =
            if (mCircleStrokeWidth < minimumTouchTarget) { // If the width is less than the minimumTouchTarget, use the minimumTouchTarget
                minimumTouchTarget / 2
            } else {
                mCircleStrokeWidth / 2 // Otherwise use the width
            }
        val outerRadius = Math.max(
            mCircleHeight,
            mCircleWidth
        ) + additionalRadius // Max outer radius of the circle, including the minimumTouchTarget or wheel width
        val innerRadius = Math.min(
            mCircleHeight,
            mCircleWidth
        ) - additionalRadius // Min inner radius of the circle, including the minimumTouchTarget or wheel width
        additionalRadius =
            if (mPointerRadius < minimumTouchTarget / 2) { // If the pointer radius is less than the minimumTouchTarget, use the minimumTouchTarget
                minimumTouchTarget / 2
            } else {
                mPointerRadius // Otherwise use the radius
            }
        var touchAngle: Float
        touchAngle =
            (Math.atan2(y.toDouble(), x.toDouble()) / Math.PI * 180 % 360).toFloat() // Verified
        touchAngle = if (touchAngle < 0) 360 + touchAngle else touchAngle // Verified
        cwDistanceFromStart = touchAngle - mStartAngle // Verified
        cwDistanceFromStart =
            if (cwDistanceFromStart < 0) 360f + cwDistanceFromStart else cwDistanceFromStart // Verified
        ccwDistanceFromStart = 360f - cwDistanceFromStart // Verified
        cwDistanceFromEnd = touchAngle - mEndAngle // Verified
        cwDistanceFromEnd =
            if (cwDistanceFromEnd < 0) 360f + cwDistanceFromEnd else cwDistanceFromEnd // Verified
        ccwDistanceFromEnd = 360f - cwDistanceFromEnd // Verified
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // These are only used for ACTION_DOWN for handling if the pointer was the part that was touched
                val pointerRadiusDegrees = (mPointerRadius * 180 / (Math.PI * Math.max(
                    mCircleHeight,
                    mCircleWidth
                ))).toFloat()
                cwDistanceFromPointer = touchAngle - mPointerPosition
                cwDistanceFromPointer =
                    if (cwDistanceFromPointer < 0) 360f + cwDistanceFromPointer else cwDistanceFromPointer
                ccwDistanceFromPointer = 360f - cwDistanceFromPointer
                // This is for if the first touch is on the actual pointer.
                if (touchEventRadius >= innerRadius && touchEventRadius <= outerRadius && (cwDistanceFromPointer <= pointerRadiusDegrees || ccwDistanceFromPointer <= pointerRadiusDegrees)) {
                    setProgressBasedOnAngle(mPointerPosition)
                    lastCWDistanceFromStart = cwDistanceFromStart
                    mIsMovingCW = true
                    mPointerHaloPaint!!.alpha = mPointerAlphaOnTouch
                    mPointerHaloPaint!!.color = mPointerHaloColorOnTouch
                    recalculateAll()
                    invalidate()
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener!!.onStartTrackingTouch(this)
                    }
                    mUserIsMovingPointer = true
                    lockAtEnd = false
                    lockAtStart = false
                } else if (cwDistanceFromStart > mTotalCircleDegrees) { // If the user is touching outside of the start AND end
                    mUserIsMovingPointer = false
                    return false
                } else if (touchEventRadius >= innerRadius && touchEventRadius <= outerRadius) { // If the user is touching near the circle
                    setProgressBasedOnAngle(touchAngle)
                    lastCWDistanceFromStart = cwDistanceFromStart
                    mIsMovingCW = true
                    mPointerHaloPaint!!.alpha = mPointerAlphaOnTouch
                    mPointerHaloPaint!!.color = mPointerHaloColorOnTouch
                    recalculateAll()
                    invalidate()
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener!!.onStartTrackingTouch(this)
                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, mProgress, true)
                    }
                    mUserIsMovingPointer = true
                    lockAtEnd = false
                    lockAtStart = false
                } else { // If the user is not touching near the circle
                    mUserIsMovingPointer = false
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> if (mUserIsMovingPointer) {
                if (lastCWDistanceFromStart < cwDistanceFromStart) {
                    if (cwDistanceFromStart - lastCWDistanceFromStart > 180f && !mIsMovingCW) {
                        lockAtStart = true
                        lockAtEnd = false
                    } else {
                        mIsMovingCW = true
                    }
                } else {
                    if (lastCWDistanceFromStart - cwDistanceFromStart > 180f && mIsMovingCW) {
                        lockAtEnd = true
                        lockAtStart = false
                    } else {
                        mIsMovingCW = false
                    }
                }
                if (lockAtStart && mIsMovingCW) {
                    lockAtStart = false
                }
                if (lockAtEnd && !mIsMovingCW) {
                    lockAtEnd = false
                }
                if (lockAtStart && !mIsMovingCW && ccwDistanceFromStart > 90) {
                    lockAtStart = false
                }
                if (lockAtEnd && mIsMovingCW && cwDistanceFromEnd > 90) {
                    lockAtEnd = false
                }
                // Fix for passing the end of a semi-circle quickly
                if (!lockAtEnd && cwDistanceFromStart > mTotalCircleDegrees && mIsMovingCW && lastCWDistanceFromStart < mTotalCircleDegrees) {
                    lockAtEnd = true
                }
                if (lockAtStart && isLockEnabled) {
                    // TODO: Add a check if mProgress is already 0, in which case don't call the listener
                    mProgress = 0
                    recalculateAll()
                    invalidate()
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, mProgress, true)
                    }
                } else if (lockAtEnd && isLockEnabled) {
                    mProgress = mMax
                    recalculateAll()
                    invalidate()
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, mProgress, true)
                    }
                } else if (mMoveOutsideCircle || touchEventRadius <= outerRadius) {
                    if (cwDistanceFromStart <= mTotalCircleDegrees) {
                        setProgressBasedOnAngle(touchAngle)
                    }
                    recalculateAll()
                    invalidate()
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, mProgress, true)
                    }
                } else {

                }
                lastCWDistanceFromStart = cwDistanceFromStart
            } else {
                return false
            }
            MotionEvent.ACTION_UP -> {
                mPointerHaloPaint!!.alpha = mPointerAlpha
                mPointerHaloPaint!!.color = mPointerHaloColor
                if (mUserIsMovingPointer) {
                    mUserIsMovingPointer = false
                    invalidate()
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener!!.onStopTrackingTouch(this)
                    }
                } else {
                    return false
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mPointerHaloPaint!!.alpha = mPointerAlpha
                mPointerHaloPaint!!.color = mPointerHaloColor
                mUserIsMovingPointer = false
                invalidate()
            }
        }
        if (event.action == MotionEvent.ACTION_MOVE && parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        return true
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val attrArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, defStyle, 0)
        initAttributes(attrArray)
        attrArray.recycle()
        initPaints()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val state = Bundle()
        state.putParcelable("PARENT", superState)
        state.putInt("MAX", mMax)
        state.putInt("PROGRESS", mProgress)
        state.putInt("mCircleColor", mCircleColor)
        state.putInt("mCircleProgressColor", mCircleProgressColor)
        state.putInt("mPointerColor", mPointerColor)
        state.putInt("mPointerHaloColor", mPointerHaloColor)
        state.putInt("mPointerHaloColorOnTouch", mPointerHaloColorOnTouch)
        state.putInt("mPointerAlpha", mPointerAlpha)
        state.putInt("mPointerAlphaOnTouch", mPointerAlphaOnTouch)
        state.putBoolean("lockEnabled", isLockEnabled)
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as Bundle
        val superState = savedState.getParcelable<Parcelable>("PARENT")
        super.onRestoreInstanceState(superState)
        mMax = savedState.getInt("MAX")
        mProgress = savedState.getInt("PROGRESS")
        mCircleColor = savedState.getInt("mCircleColor")
        mCircleProgressColor = savedState.getInt("mCircleProgressColor")
        mPointerColor = savedState.getInt("mPointerColor")
        mPointerHaloColor = savedState.getInt("mPointerHaloColor")
        mPointerHaloColorOnTouch = savedState.getInt("mPointerHaloColorOnTouch")
        mPointerAlpha = savedState.getInt("mPointerAlpha")
        mPointerAlphaOnTouch = savedState.getInt("mPointerAlphaOnTouch")
        isLockEnabled = savedState.getBoolean("lockEnabled")
        initPaints()
        recalculateAll()
    }

    fun setOnSeekBarChangeListener(l: OnCircularSeekBarChangeListener?) {
        mOnCircularSeekBarChangeListener = l
    }
    /**
     * Gets the circle color.
     *
     * @return An integer color value for the circle
     */
    /**
     * Sets the circle color.
     *
     * @param color the color of the circle
     */
    var circleColor: Int
        get() = mCircleColor
        set(color) {
            mCircleColor = color
            mCirclePaint!!.color = mCircleColor
            invalidate()
        }
    /**
     * Gets the circle progress color.
     *
     * @return An integer color value for the circle progress
     */
    /**
     * Sets the circle progress color.
     *
     * @param color the color of the circle progress
     */
    var circleProgressColor: Int
        get() = mCircleProgressColor
        set(color) {
            mCircleProgressColor = color
            mCircleProgressPaint!!.color = mCircleProgressColor
            invalidate()
        }
    /**
     * Gets the pointer color.
     *
     * @return An integer color value for the pointer
     */
    /**
     * Sets the pointer color.
     *
     * @param color the color of the pointer
     */
    var pointerColor: Int
        get() = mPointerColor
        set(color) {
            mPointerColor = color
            mPointerPaint!!.color = mPointerColor
            invalidate()
        }
    /**
     * Gets the pointer halo color.
     *
     * @return An integer color value for the pointer halo
     */
    /**
     * Sets the pointer halo color.
     *
     * @param color the color of the pointer halo
     */
    var pointerHaloColor: Int
        get() = mPointerHaloColor
        set(color) {
            mPointerHaloColor = color
            mPointerHaloPaint!!.color = mPointerHaloColor
            invalidate()
        }
    /**
     * Gets the pointer alpha value.
     *
     * @return An integer alpha value for the pointer (0..255)
     */
    /**
     * Sets the pointer alpha.
     *
     * @param alpha the alpha of the pointer
     */
    var pointerAlpha: Int
        get() = mPointerAlpha
        set(alpha) {
            if (alpha >= 0 && alpha <= 255) {
                mPointerAlpha = alpha
                mPointerHaloPaint!!.alpha = mPointerAlpha
                invalidate()
            }
        }
    /**
     * Gets the pointer alpha value when touched.
     *
     * @return An integer alpha value for the pointer (0..255) when touched
     */
    /**
     * Sets the pointer alpha when touched.
     *
     * @param alpha the alpha of the pointer (0..255) when touched
     */
    var pointerAlphaOnTouch: Int
        get() = mPointerAlphaOnTouch
        set(alpha) {
            if (alpha >= 0 && alpha <= 255) {
                mPointerAlphaOnTouch = alpha
            }
        }
    /**
     * Gets the circle fill color.
     *
     * @return An integer color value for the circle fill
     */
    /**
     * Sets the circle fill color.
     *
     * @param color the color of the circle fill
     */
    var circleFillColor: Int
        get() = mCircleFillColor
        set(color) {
            mCircleFillColor = color
            mCircleFillPaint!!.color = mCircleFillColor
            invalidate()
        }
    /**
     * Get the current max of the CircularSeekBar.
     *
     * @return Synchronized integer value of the max.
     */// If the new max is less than current progress, set progress to zero// Check to make sure it's greater than zero
    /**
     * Set the max of the CircularSeekBar.
     * If the new max is less than the current progress, then the progress will be set to zero.
     * If the progress is changed as a result, then any listener will receive a onProgressChanged event.
     *
     * @param max The new max for the CircularSeekBar.
     */
    @get:Synchronized
    var max: Int
        get() = mMax
        set(max) {
            if (max > 0) { // Check to make sure it's greater than zero
                if (max <= mProgress) {
                    mProgress =
                        0 // If the new max is less than current progress, set progress to zero
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener!!.onProgressChanged(this, mProgress, false)
                    }
                }
                mMax = max
                recalculateAll()
                invalidate()
            }
        }

    /**
     * Listener for the CircularSeekBar. Implements the same methods as the normal OnSeekBarChangeListener.
     */
    interface OnCircularSeekBarChangeListener {
        fun onProgressChanged(circularSeekBar: CircularSeekBar?, progress: Int, fromUser: Boolean)
        fun onStopTrackingTouch(seekBar: CircularSeekBar?)
        fun onStartTrackingTouch(seekBar: CircularSeekBar?)
    }

    companion object {
        // Default values
        private const val DEFAULT_CIRCLE_X_RADIUS = 30f
        private const val DEFAULT_CIRCLE_Y_RADIUS = 30f
        private const val DEFAULT_POINTER_RADIUS = 7f
        private const val DEFAULT_POINTER_HALO_WIDTH = 6f
        private const val DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f
        private const val DEFAULT_CIRCLE_STROKE_WIDTH = 5f
        private const val DEFAULT_START_ANGLE = 270f // Geometric (clockwise, relative to 3 o'clock)
        private const val DEFAULT_END_ANGLE = 270f // Geometric (clockwise, relative to 3 o'clock)
        private const val DEFAULT_MAX = 100
        private const val DEFAULT_PROGRESS = 0
        private const val DEFAULT_CIRCLE_COLOR = Color.DKGRAY
        private val DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255)
        private val DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255)
        private val DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 74, 138, 255)
        private val DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 74, 138, 255)
        private const val DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT
        private const val DEFAULT_POINTER_ALPHA = 135
        private const val DEFAULT_POINTER_ALPHA_ONTOUCH = 100
        private const val DEFAULT_USE_CUSTOM_RADII = false
        private const val DEFAULT_MAINTAIN_EQUAL_CIRCLE = true
        private const val DEFAULT_MOVE_OUTSIDE_CIRCLE = false
        private const val DEFAULT_LOCK_ENABLED = true
    }
}