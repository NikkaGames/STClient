package ge.nikka.stclient.ui.home

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.EditText

class EditTextCursorWatcher : androidx.appcompat.widget.AppCompatEditText {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        this@EditTextCursorWatcher.scaleX = `val`
                        this@EditTextCursorWatcher.scaleY = `val`
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        this@EditTextCursorWatcher.scaleX = `val`
                        this@EditTextCursorWatcher.scaleY = `val`
                    }
                    animator2.start()
                }
            }
            false
        }
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        this@EditTextCursorWatcher.scaleX = `val`
                        this@EditTextCursorWatcher.scaleY = `val`
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        this@EditTextCursorWatcher.scaleX = `val`
                        this@EditTextCursorWatcher.scaleY = `val`
                    }
                    animator2.start()
                }
            }
            false
        }
    }

    constructor(context: Context?) : super(context!!) {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        this@EditTextCursorWatcher.scaleX = `val`
                        this@EditTextCursorWatcher.scaleY = `val`
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        this@EditTextCursorWatcher.scaleX = `val`
                        this@EditTextCursorWatcher.scaleY = `val`
                    }
                    animator2.start()
                }
            }
            false
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        val anim2 = ValueAnimator.ofFloat(1f, 1.01f)
        anim2.setDuration(50)
        anim2.addUpdateListener { animation ->
            this@EditTextCursorWatcher.scaleX =
                (animation.animatedValue as Float)
            this@EditTextCursorWatcher.scaleY =
                (animation.animatedValue as Float)
        }
        anim2.repeatCount = 1
        anim2.repeatMode = ValueAnimator.REVERSE
        anim2.start()
    }
}