package ge.nikka.stclient

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
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
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.compose.animation.core.Animatable
import androidx.core.view.isVisible
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MenuCanvas(var ctx: Context) : View(ctx), Runnable {
    var mStrokePaint: Paint? = null
    var mFilledPaint: Paint? = null
    var mTextPaint: Paint? = null
    var mThread: Thread
    var sleepTime: Long
    private var previousOpenedState = false

    init {
        InitializePaints()
        isFocusableInTouchMode = false
        setBackgroundColor(Color.TRANSPARENT)
        sleepTime = (1000 / FPS).toLong()
        mThread = Thread(this)
        mThread.start()
    }

    private fun animateTint(from: Int, to: Int) {
        val animator = ValueAnimator.ofInt(from, to)
        animator.duration = 350
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            tintAlpha = it.animatedValue as Int
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        if (isVisible) {
            ClearCanvas(canvas)
            FloatingWindow.DrawOn(this, canvas)
            applyDarkTintEffect(canvas)
            if (FloatingWindow.isOpened != previousOpenedState) {
                previousOpenedState = FloatingWindow.isOpened
                if (FloatingWindow.isOpened) {
                    animateTint(0, 200)
                } else {
                    animateTint(200, 0)
                }
            }
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
                Log.e("ge.nikka.stclient", it.message!!)
            }
        }
    }

    fun applyDarkTintEffect(canvas: Canvas) {
        val darkTintPaint = Paint().apply {
            color = Color.argb(tintAlpha, 0, 0, 0)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), darkTintPaint)
    }

    fun fastblur(sentBitmap: Bitmap, scale: Float, radius: Int): Bitmap? {
        var sentBitmap = sentBitmap
        val width = Math.round(sentBitmap.width * scale)
        val height = Math.round(sentBitmap.height * scale)
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false)

        val bitmap = sentBitmap.copy(sentBitmap.config!!, true)

        if (radius < 1) {
            return (null)
        }

        val w = bitmap.width
        val h = bitmap.height

        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        val vmin = IntArray(max(w.toDouble(), h.toDouble()).toInt())

        var divsum = (div + 1) shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = (i / divsum)
            i++
        }

        yi = 0
        var yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[(yi + min(wm.toDouble(), max(i.toDouble(), 0.0))).toInt()]
                sir = stack[i + radius]
                sir[0] = (p and 0xff0000) shr 16
                sir[1] = (p and 0x00ff00) shr 8
                sir[2] = (p and 0x0000ff)
                rbs = (r1 - abs(i.toDouble())).toInt()
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = min((x + radius + 1).toDouble(), wm.toDouble()).toInt()
                }
                p = pix[yw + vmin[x]]

                sir[0] = (p and 0xff0000) shr 16
                sir[1] = (p and 0x00ff00) shr 8
                sir[2] = (p and 0x0000ff)

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[(stackpointer) % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = (max(0.0, yp.toDouble()) + x).toInt()

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = (r1 - abs(i.toDouble())).toInt()

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] =
                    (-0x1000000 and pix[yi]) or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = (min((y + r1).toDouble(), hm.toDouble()) * w).toInt()
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return (bitmap)
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
        @JvmField
        var tintAlpha = 0
        var FPS: Int = 60
        fun fadein(): Animation {
            val fadeOut: Animation = AlphaAnimation(1f, 0f)
            fadeOut.duration = 300
            return fadeOut
        }

        fun fadeout(): Animation {
            val fadeIn: Animation = AlphaAnimation(0f, 1f)
            fadeIn.duration = 300
            return fadeIn
        }
    }
}
