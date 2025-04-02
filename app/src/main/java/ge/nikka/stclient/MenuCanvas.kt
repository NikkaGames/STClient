package ge.nikka.stclient

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Process
import android.util.Log
import android.view.Display
import android.view.View
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class MenuCanvas(var ctx: Context) : View(ctx), Runnable {
    var mStrokePaint: Paint? = null
    var mFilledPaint: Paint? = null
    var mTextPaint: Paint? = null
    var mThread: Thread
    var FPS: Int = 60
    var sleepTime: Long

    // Date time;
    var timeForm: SimpleDateFormat
    var dateForm: SimpleDateFormat

    //public native void SetTime(String value);
    //public native void SetDate(String value);
    init {
        InitializePaints()
        isFocusableInTouchMode = false
        setBackgroundColor(Color.TRANSPARENT)
        //time = new Date();
        timeForm = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        dateForm = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if (Companion.display != null) FPS = Companion.display!!.refreshRate.toInt()
        sleepTime = (1000 / FPS).toLong()
        mThread = Thread(this)
        mThread.start()
    }

    override fun onDraw(canvas: Canvas) {
        if (canvas != null && visibility == VISIBLE) {
            ClearCanvas(canvas)
            //time.setTime(System.currentTimeMillis());
            //SetTime(timeForm.format(time));
            //SetDate(dateForm.format(time));
            FloatingWindow.DrawOn(this, canvas)
        }
    }

    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
        while (mThread.isAlive && !mThread.isInterrupted) {
            try {
                val t1 = System.currentTimeMillis()
                postInvalidate()
                val td = System.currentTimeMillis() - t1
                Thread.sleep(
                    max(
                        min(0.0, (sleepTime - td).toDouble()),
                        sleepTime.toDouble()
                    ).toLong()
                )
            } catch (it: InterruptedException) {
                Log.e("OverlayThread", it.message!!)
            }
        }
    }

    fun InitializePaints() {
        mStrokePaint = Paint()
        mStrokePaint!!.style = Paint.Style.STROKE
        mStrokePaint!!.isAntiAlias = true
        mStrokePaint!!.color = Color.rgb(0, 0, 0)

        mFilledPaint = Paint()
        mFilledPaint!!.style = Paint.Style.FILL
        mFilledPaint!!.isAntiAlias = true
        mFilledPaint!!.color = Color.rgb(0, 0, 0)

        mTextPaint = Paint()
        mTextPaint!!.style = Paint.Style.FILL_AND_STROKE
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.color = Color.rgb(0, 0, 0)
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.strokeWidth = 1.1f
    }

    fun ClearCanvas(cvs: Canvas) {
        cvs.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    fun DrawLine(
        cvs: Canvas,
        a: Int,
        r: Int,
        g: Int,
        b: Int,
        lineWidth: Float,
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float
    ) {
        mStrokePaint!!.color = Color.rgb(r, g, b)
        mStrokePaint!!.alpha = a
        mStrokePaint!!.strokeWidth = lineWidth
        cvs.drawLine(fromX, fromY, toX, toY, mStrokePaint!!)
    }

    fun DrawText(
        cvs: Canvas,
        a: Int,
        r: Int,
        g: Int,
        b: Int,
        txt: String,
        posX: Float,
        posY: Float,
        size: Float
    ) {
        mTextPaint!!.color = Color.rgb(r, g, b)
        mTextPaint!!.alpha = a

        if (right > 1920 || bottom > 1920) {
            mTextPaint!!.setShadowLayer(12.0f, 0.0f, 0.0f, Color.rgb(r, g, b))
            mTextPaint!!.textSize = 4 + size
        } else if (right == 1920 || bottom == 1920) {
            mTextPaint!!.textSize = 2 + size
            mTextPaint!!.setShadowLayer(12.0f, 0.0f, 0.0f, Color.rgb(r, g, b))
        } else mTextPaint!!.textSize = size
        mTextPaint!!.setShadowLayer(12.0f, 0.0f, 0.0f, Color.rgb(r, g, b))
        cvs.drawText(txt, posX, posY, mTextPaint!!)
    }

    fun DrawCircle(
        cvs: Canvas,
        a: Int,
        r: Int,
        g: Int,
        b: Int,
        stroke: Float,
        posX: Float,
        posY: Float,
        radius: Float
    ) {
        mStrokePaint!!.color = Color.rgb(r, g, b)
        mStrokePaint!!.alpha = a
        mStrokePaint!!.strokeWidth = stroke
        cvs.drawCircle(posX, posY, radius, mStrokePaint!!)
    }

    fun DrawFilledCircle(
        cvs: Canvas,
        a: Int,
        r: Int,
        g: Int,
        b: Int,
        posX: Float,
        posY: Float,
        radius: Float
    ) {
        mFilledPaint!!.color = Color.rgb(r, g, b)
        mFilledPaint!!.alpha = a
        cvs.drawCircle(posX, posY, radius, mFilledPaint!!)
    }

    fun DrawTextNew(
        canvas: Canvas, r: Int, g: Int, b: Int, a: Int,
        x: Float, y: Float, width: Float, height: Float,
        textt: String, size: Float, shadow: Boolean,
        outline: Boolean, glow: Boolean, glowAlpha: Float, gradient: Boolean, type: Int
    ) {
        try {
            val paint = Paint()
            paint.isAntiAlias = true
            paint.textSize = size
            paint.color = Color.argb(a, r, g, b)
            paint.textAlign = Paint.Align.LEFT
            var font: Typeface? = null
            when (type) {
                0 -> font = Typeface.createFromAsset(ctx.assets, "font.ttf")
                1 -> font = Typeface.createFromAsset(ctx.assets, "fontfunc.ttf")
                2 -> font = Typeface.createFromAsset(ctx.assets, "ficons.ttf")
            }
            paint.setTypeface(font)
            val textBounds = Rect()
            var text = textt
            if (!paint.hasGlyph(text)) {
                text = text.replace("[^\\x20-\\x7E]".toRegex(), "?")
            }
            paint.getTextBounds(text, 0, text.length, textBounds)
            val textX = x + (width - textBounds.width()) / 2f
            val textY = y + (height + textBounds.height()) / 2f
            if (shadow) {
                val shadowPaint = Paint(paint)
                shadowPaint.color = Color.argb((a * 0.7f).toInt(), 0, 0, 0)
                canvas.drawText(text, textX + 2, textY + 2, shadowPaint)
            }
            if (outline) {
                val outlinePaint = Paint(paint)
                outlinePaint.style = Paint.Style.STROKE
                outlinePaint.strokeWidth = 2f // Adjust stroke width as needed
                outlinePaint.color = Color.argb(a, 0, 0, 0)
                canvas.drawText(text, textX, textY, outlinePaint)
            }
            if (glow) {
                val glowPaint = Paint(paint)
                glowPaint.setMaskFilter(BlurMaskFilter(glowAlpha, BlurMaskFilter.Blur.OUTER))
                glowPaint.color = Color.argb(a, r, g, b)
                canvas.drawText(text, textX, textY, glowPaint)
            }
            canvas.drawText(text, textX, textY, paint)
        } catch (ex: Exception) {
        }
    }

    fun DrawRectNew(
        canvas: Canvas, r: Int, g: Int, b: Int, a: Int,
        x: Float, y: Float, width: Float, height: Float,
        outline: Boolean, rounding: Float, stroke: Int,
        glow: Boolean, glowSize: Int, glowAlpha: Float
    ) {
        val startX = x
        val startY = y
        val endX = x + width
        val endY = y + height

        val paint = Paint()
        paint.isAntiAlias = true

        if (glow) {
            paint.style = Paint.Style.STROKE
            for (i in 0..<glowSize) {
                val alpha = glowAlpha * (1.0f / glowSize) * ((glowSize - i).toFloat() / glowSize)
                paint.color = Color.argb((alpha * 255).toInt(), r, g, b)
                paint.strokeWidth = (stroke + i).toFloat()
                canvas.drawRoundRect(
                    startX - i,
                    startY - i,
                    endX + i,
                    endY + i,
                    rounding,
                    rounding,
                    paint
                )
            }
        }

        if (outline) {
            paint.style = Paint.Style.STROKE
            paint.color = Color.argb((a * 0.8f).toInt(), 0, 0, 0)
            paint.strokeWidth = (stroke * 2).toFloat()
            canvas.drawRoundRect(startX, startY, endX, endY, rounding, rounding, paint)
        }

        paint.style = Paint.Style.STROKE
        paint.color = Color.argb(a, r, g, b)
        paint.strokeWidth = stroke.toFloat()
        canvas.drawRoundRect(startX, startY, endX, endY, rounding, rounding, paint)
    }

    fun DrawRect(
        cvs: Canvas,
        a: Int,
        r: Int,
        g: Int,
        b: Int,
        stroke: Float,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        mStrokePaint!!.strokeWidth = stroke
        mStrokePaint!!.color = Color.rgb(r, g, b)
        mStrokePaint!!.alpha = a
        cvs.drawRect(x, y, x + width, y + height, mStrokePaint!!)
    }

    fun DrawFilledRect(
        cvs: Canvas,
        a: Int,
        r: Int,
        g: Int,
        b: Int,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        mFilledPaint!!.color = Color.rgb(r, g, b)
        mFilledPaint!!.alpha = a
        cvs.drawRect(x, y, x + width, y + height, mFilledPaint!!)
    }

    companion object {
        @JvmField
        var display: Display? = null
    }
}
