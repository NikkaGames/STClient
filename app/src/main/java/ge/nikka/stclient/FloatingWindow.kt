package ge.nikka.stclient

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.text.Html
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import ge.nikka.stclient.Base64Utils.Decrypt
import java.util.Objects

class FloatingWindow : Service() {
    var mFloatingView: View? = null
    private var close: Button? = null
    private var mButtonPanel: LinearLayout? = null
    var mCollapsed: RelativeLayout? = null
    var mExpanded: LinearLayout? = null
    var mWindowManager: WindowManager? = null
    var params: WindowManager.LayoutParams? = null
    private var patches: LinearLayout? = null
    private var startimage: ImageView? = null
    private var view1: LinearLayout? = null
    private var view2: LinearLayout? = null
    private var Btns: LinearLayout? = null
    private var alert: AlertDialog? = null
    private var edittextnegue: EditText? = null
    private var aalert: AlertDialog? = null
    private var showrage = false
    private var prefs: SharedPreferences? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        return START_NOT_STICKY
    }

    val layoutType: Int
        get() {
            return if (Build.VERSION.SDK_INT >= 26) {
                2038
            } else {
                2002
            }
            //return 2003;
        }

    private fun DrawCanvas() {
        overlayView = MenuCanvas(this)
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        espParams = layoutParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layoutParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        layoutParams.gravity = Gravity.TOP or Gravity.START
        espParams!!.x = 0
        espParams!!.y = 0
    }

    override fun onCreate() {
        super.onCreate()
        prefs = applicationContext.getSharedPreferences("url", MODE_PRIVATE)
        DrawCanvas()

        initFloating()
        initAlertDialog()

        val colorAnim: ValueAnimator = ObjectAnimator.ofInt(
            textView, "textColor",
            Color.rgb(244, 67, 54), Color.rgb(233, 30, 99), Color.rgb(156, 39, 176),
            Color.rgb(103, 58, 183), Color.rgb(63, 81, 181), Color.rgb(33, 150, 243),
            Color.rgb(3, 169, 244), Color.rgb(0, 188, 212), Color.rgb(0, 150, 136),
            Color.rgb(76, 175, 80), Color.rgb(139, 195, 74), Color.rgb(205, 220, 57),
            Color.rgb(255, 235, 59), Color.rgb(255, 193, 7), Color.rgb(255, 152, 0),
            Color.rgb(255, 87, 34)
        )
        colorAnim.setDuration(6000)
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.repeatCount = ValueAnimator.INFINITE
        colorAnim.repeatMode = ValueAnimator.REVERSE
        colorAnim.addUpdateListener { animation ->
            gradientDrawable!!.setStroke(
                4,
                (animation.animatedValue as Int)
            )
        }
        colorAnim.start()

        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                Thread()
                handler.postDelayed(this, 1000)
            }
        })
    }

    var gradientDrawable: GradientDrawable? = null

    var textView: TextView? = null

    var a: String = "#"
    var b: String = "9"
    var c: String = "A"
    var d: String = "2"
    var e: String = "D"
    var f: String = "3"
    var g: String = "1"
    var h: String = "3"
    var j: String = "3"
    var l: String = "F"

    var o1: Float = 3.4f
    var o2: Float = 6.6f
    var o3: Float = 2.3f
    var o4: Float = 1.9f
    var o5: Float = 0.8f

    var part: Float = o1 + o2 + o3 + o4 + o5

    var radius: Float = part + part

    private fun initFloating() {
        val frameLayout = FrameLayout(baseContext)
        val relativeLayout = RelativeLayout(baseContext)
        mCollapsed = RelativeLayout(baseContext)
        mExpanded = LinearLayout(baseContext)
        patches = LinearLayout(baseContext)
        Btns = LinearLayout(baseContext)
        frameLayout.layoutParams = FrameLayout.LayoutParams(-2, -2)
        relativeLayout.layoutParams = FrameLayout.LayoutParams(-2, -2)
        mCollapsed!!.layoutParams = RelativeLayout.LayoutParams(-2, -2)
        mCollapsed!!.visibility = View.VISIBLE
        val imageView = ImageView(baseContext)
        startimage = imageView

        imageView.layoutParams = RelativeLayout.LayoutParams(-2, -2)
        view1 = LinearLayout(baseContext)
        view2 = LinearLayout(baseContext)
        mButtonPanel = LinearLayout(baseContext)

        gradientDrawable = GradientDrawable()
        gradientDrawable!!.shape = GradientDrawable.RECTANGLE
        gradientDrawable!!.setColor(Color.parseColor(a + b + c + d + e + f + g + h + j))
        gradientDrawable!!.cornerRadius = radius

        val gradientDrawable2 = GradientDrawable()
        gradientDrawable!!.shape = GradientDrawable.RECTANGLE
        gradientDrawable2.setColor(Color.TRANSPARENT)
        gradientDrawable2.setStroke(0, -1)
        gradientDrawable2.cornerRadius = 0.0f

        val gradientDrawable3 = GradientDrawable()
        gradientDrawable3.shape = GradientDrawable.RECTANGLE
        gradientDrawable3.setColor(Color.TRANSPARENT)
        gradientDrawable3.cornerRadius = 0.0f

        startimage = ImageView(baseContext)
        startimage!!.layoutParams = RelativeLayout.LayoutParams(-2, -2)
        val applyDimension = TypedValue.applyDimension(1, 57f, resources.displayMetrics).toInt()
        startimage!!.layoutParams.height = applyDimension
        startimage!!.layoutParams.width = applyDimension
        startimage!!.requestLayout()
        startimage!!.scaleType = ImageView.ScaleType.FIT_XY

        startimage!!.setImageResource(R.drawable.icon_mack)
        startimage!!.imageAlpha = 500
        (startimage!!.layoutParams as MarginLayoutParams).topMargin = convertDipToPixels(10)

        mExpanded!!.visibility = View.INVISIBLE
        mExpanded!!.background = gradientDrawable
        mExpanded!!.gravity = 17
        mExpanded!!.orientation = LinearLayout.VERTICAL
        mExpanded!!.layoutParams = LinearLayout.LayoutParams(800, 800) //-1


        val scrollView = ScrollView(baseContext)
        scrollView.layoutParams = LinearLayout.LayoutParams(-1, 625)

        patches!!.layoutParams = LinearLayout.LayoutParams(-1, -1)
        patches!!.setBackgroundColor(Color.parseColor("#000000"))
        patches!!.background = gradientDrawable2
        patches!!.orientation = LinearLayout.VERTICAL

        Btns!!.layoutParams = LinearLayout.LayoutParams(-1, -2)
        Btns!!.setBackgroundColor(0)
        Btns!!.gravity = 5
        Btns!!.setPadding(0, 0, 5, 0)
        Btns!!.orientation = LinearLayout.HORIZONTAL

        textView = TextView(baseContext)
        textView!!.text = engine()
        textView!!.typeface = google()
        if (dpi() > 400) textView!!.textSize = 21.0f
        else textView!!.textSize = 20.0f
        textView!!.gravity = 17
        textView!!.setPadding(0, 0, 0, 25)
        textView!!.layoutParams = LinearLayout.LayoutParams(-1, -2)
        textView!!.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        textView!!.scaleX = neg
                        textView!!.scaleY = neg
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        textView!!.scaleX = neg
                        textView!!.scaleY = neg
                    }
                    animator2.start()
                }

                MotionEvent.ACTION_CANCEL -> {
                    val animator3 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator3.setDuration(150)
                    animator3.interpolator = DecelerateInterpolator()
                    animator3.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        textView!!.scaleX = neg
                        textView!!.scaleY = neg
                    }
                    animator3.start()
                }
            }
            false
        }
        val layoutParams = LinearLayout.LayoutParams(-2, -2)
        layoutParams.gravity = 17

        val textvieww = TextView(this)
        textvieww.text = manf()
        textvieww.textSize = 15.0f
        textvieww.setTextColor(Color.WHITE)
        textvieww.setTypeface(google())
        textvieww.gravity = 17
        textvieww.layoutParams = LinearLayout.LayoutParams(-1, -2)
        textvieww.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        textvieww.scaleX = neg
                        textvieww.scaleY = neg
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        textvieww.scaleX = neg
                        textvieww.scaleY = neg
                    }
                    animator2.start()
                }

                MotionEvent.ACTION_CANCEL -> {
                    val animator3 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator3.setDuration(150)
                    animator3.interpolator = DecelerateInterpolator()
                    animator3.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        textvieww.scaleX = neg
                        textvieww.scaleY = neg
                    }
                    animator3.start()
                }
            }
            false
        }

        close = Button(this)
        close!!.setBackgroundColor(Color.TRANSPARENT)
        close!!.text = Decrypt("Q8qf4bSPc+G0hw==")
        close!!.textSize = 17.0f
        close!!.isAllCaps = false
        close!!.gravity = 17
        close!!.background = gradientDrawable3
        close!!.setTextColor(Color.WHITE)
        val layoutParams2 = LinearLayout.LayoutParams(-1, dp(35))
        close!!.layoutParams = layoutParams2

        frameLayout.addView(relativeLayout)
        relativeLayout.addView(this.mCollapsed)
        relativeLayout.addView(this.mExpanded)
        mCollapsed!!.addView(startimage)
        mExpanded!!.addView(textView)
        mExpanded!!.addView(textvieww)
        mExpanded!!.addView(scrollView)
        scrollView.addView(patches)

        this.mFloatingView = frameLayout
        params = if (Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams(-2, -2, 2038, 8, -3)
        } else {
            WindowManager.LayoutParams(-2, -2, 2002, 8, -3)
        }
        params!!.gravity = 8388659
        params!!.x = 0
        params!!.y = 100
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(overlayView, espParams)
        mWindowManager!!.addView(mFloatingView, params)
        val relativeLayout2: RelativeLayout = mCollapsed as RelativeLayout
        val linearLayout: LinearLayout = mExpanded as LinearLayout
        frameLayout.setOnTouchListener(onTouchListener())
        startimage!!.setOnTouchListener(onTouchListener())
        initMenuButton(relativeLayout2, linearLayout)
        CreateMenuList()
        startimage?.animation = fadeout()
        startimage?.animate()
    }

    private fun onTouchListener(): OnTouchListener {
        return object : OnTouchListener {
            val collapsedView: View? = mCollapsed
            val expandedView: View? = mExpanded
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var initialX = 0
            private var initialY = 0

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params!!.x
                        initialY = params!!.y
                        initialTouchX = motionEvent.rawX
                        initialTouchY = motionEvent.rawY
                        val animator = ValueAnimator.ofFloat(1f, 0.85f)
                        animator.setDuration(150)
                        animator.interpolator = DecelerateInterpolator()
                        animator.addUpdateListener { animation: ValueAnimator ->
                            val neg = animation.animatedValue as Float
                            startimage!!.scaleX = neg
                            startimage!!.scaleY = neg
                        }
                        animator.start()
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val rawX = (motionEvent.rawX - initialTouchX).toInt()
                        val rawY = (motionEvent.rawY - initialTouchY).toInt()

                        if (rawX < 10 && rawY < 10 && isViewCollapsed) {
                            collapsedView!!.animation =
                                fadein()
                            val handler = Handler()
                            handler.postDelayed({ collapsedView.visibility = View.GONE }, 300)
                            expandedView!!.visibility = View.VISIBLE
                            expandedView.animation =
                                fadeout()
                        }
                        val animator2 = ValueAnimator.ofFloat(0.85f, 1f)
                        animator2.setDuration(150)
                        animator2.interpolator = DecelerateInterpolator()
                        animator2.addUpdateListener { animation: ValueAnimator ->
                            val neg = animation.animatedValue as Float
                            startimage!!.scaleX = neg
                            startimage!!.scaleY = neg
                        }
                        animator2.start()
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params!!.x = initialX + ((motionEvent.rawX - initialTouchX).toInt())
                        params!!.y = initialY + ((motionEvent.rawY - initialTouchY).toInt())
                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        return true
                    }

                    else -> return false
                }
            }
        }
    }

    private var textView2: TextView? = null
    private var featureNameExt: String? = null
    private var featureNum = 0
    private var txtValue: edittextvalue? = null

    inner class edittextvalue {
        var value: Int = 0
    }

    private fun addTextField(featureName: String, feature: Int, interInt: InterfaceInt) {
        val relativeLayout2 = RelativeLayout(this)
        relativeLayout2.layoutParams = RelativeLayout.LayoutParams(-2, -1)
        relativeLayout2.setPadding(10, 5, 10, 5)
        relativeLayout2.setVerticalGravity(16)

        val layoutParams = RelativeLayout.LayoutParams(-2, -2)
        layoutParams.topMargin = 30
        layoutParams.leftMargin = 10

        val textView = TextView(this)
        textView.text = Html.fromHtml("$featureName: <font color='red'>Null</font>")
        textView.setTextColor(Color.WHITE)
        textView.typeface = google()
        textView.layoutParams = layoutParams

        val edittextneg = edittextvalue()

        val layoutParams2 = RelativeLayout.LayoutParams(220, 110)
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

        val button2 = Button(this)
        button2.layoutParams = layoutParams2
        button2.background = resources.getDrawable(R.drawable.btn_def)
        button2.text = "Modify"
        button2.typeface = google()
        button2.setTextColor(Color.WHITE)
        button2.layoutParams = layoutParams2
        button2.gravity = Gravity.CENTER
        button2.setOnClickListener {
            alert!!.show()
            textView2 = textView
            featureNum = feature
            featureNameExt = featureName
            txtValue = edittextneg
            edittextnegue!!.setText(edittextneg.value.toString())
        }
        button2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.85f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.85f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator2.start()
                }

                MotionEvent.ACTION_CANCEL -> {
                    val animator3 = ValueAnimator.ofFloat(0.85f, 1f)
                    animator3.setDuration(150)
                    animator3.interpolator = DecelerateInterpolator()
                    animator3.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator3.start()
                }
            }
            false
        }

        relativeLayout2.addView(textView)
        relativeLayout2.addView(button2)
        patches!!.addView(relativeLayout2)
    }

    private fun initAlertDialog() {
        val linearLayout1 = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(-1, -1)
        linearLayout1.setPadding(10, 5, 0, 5)
        linearLayout1.orientation = LinearLayout.VERTICAL
        linearLayout1.gravity = 17
        linearLayout1.layoutParams = layoutParams
        linearLayout1.setBackgroundColor(Color.TRANSPARENT)

        val i = if (Build.VERSION.SDK_INT >= 26) 2038 else 2002
        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(-1, -1)
        linearLayout.orientation = LinearLayout.VERTICAL
        val frameLayout = FrameLayout(this)
        frameLayout.layoutParams = FrameLayout.LayoutParams(-2, -2)
        frameLayout.addView(linearLayout)

        val textView = TextView(this)
        textView.text = "Tap \"Set Value\" to apply changes, tap outside to cancel."
        textView.setTextColor(Color.WHITE)
        textView.typeface = google()
        textView.layoutParams = layoutParams

        edittextnegue = EditText(this)
        edittextnegue!!.layoutParams = layoutParams
        edittextnegue!!.maxLines = 1
        edittextnegue!!.width = convertDipToPixels(300)
        edittextnegue!!.setBackgroundColor(Color.TRANSPARENT)
        edittextnegue!!.setTextColor(Color.WHITE)
        edittextnegue!!.typeface = google()
        edittextnegue!!.textSize = 21.0f
        edittextnegue!!.inputType = InputType.TYPE_CLASS_NUMBER
        edittextnegue!!.keyListener = DigitsKeyListener.getInstance("0123456789")

        val FilterArray = arrayOfNulls<InputFilter>(1)
        FilterArray[0] = LengthFilter(10)
        edittextnegue!!.filters = FilterArray

        val button = Button(this)
        button.setBackgroundColor(Color.TRANSPARENT)
        button.setTextColor(Color.WHITE)
        button.typeface = google()
        button.text = "Set Value"
        button.setOnClickListener {
            Call(featureNum, edittextnegue!!.text.toString().toInt())
            txtValue!!.value = edittextnegue!!.text.toString().toInt()
            textView2!!.text =
                Html.fromHtml(featureNameExt + ": <font color='#41c300'>" + edittextnegue!!.text.toString() + "</font>")
            val animator = ValueAnimator.ofFloat(1f, 1.3f, 1f)
            animator.setDuration(150)
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { animation: ValueAnimator ->
                val neg = animation.animatedValue as Float
                textView2!!.scaleX = neg
                textView2!!.scaleY = neg
            }
            animator.start()
            alert!!.dismiss()
        }

        alert = AlertDialog.Builder(this, 2).create()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alert?.getWindow())?.setType(i)
        }
        linearLayout1.addView(textView)
        linearLayout1.addView(edittextnegue)
        linearLayout1.addView(button)
        alert?.setView(linearLayout1)
    }

    private fun initJsonDialog() {
        val linearLayout1 = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(-1, -1)
        linearLayout1.setPadding(10, 5, 0, 5)
        linearLayout1.orientation = LinearLayout.VERTICAL
        linearLayout1.gravity = 17
        linearLayout1.layoutParams = layoutParams
        linearLayout1.setBackgroundColor(Color.TRANSPARENT)

        val i = if (Build.VERSION.SDK_INT >= 26) 2038 else 2002
        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(-1, -1)
        //linearLayout.setBackgroundColor(Color.parseColor("#14171f"));
        linearLayout.orientation = LinearLayout.VERTICAL
        val frameLayout = FrameLayout(this)
        frameLayout.layoutParams = FrameLayout.LayoutParams(-2, -2)
        frameLayout.addView(linearLayout)

        val textView = TextView(this)
        textView.text =
            "Paste \"pastebin\" URL of your JSON configuration and click apply, tap outside to cancel."
        textView.setTextColor(Color.WHITE)
        textView.typeface = google()
        textView.layoutParams = layoutParams

        val edittextneg = EditText(this)
        edittextneg.layoutParams = layoutParams
        edittextneg.maxLines = 1
        edittextneg.width = convertDipToPixels(300)
        edittextneg.setBackgroundColor(Color.TRANSPARENT)
        edittextneg.setTextColor(Color.WHITE)
        edittextneg.typeface = google()
        edittextneg.hint = "https://pastebin.com/raw/…"
        edittextneg.setHintTextColor(Color.LTGRAY)
        edittextneg.textSize = 17.0f
        val value = prefs!!.getString("surl", "")!!
        edittextneg.setText(value)

        val button = Button(this)
        button.setBackgroundColor(Color.TRANSPARENT)
        button.setTextColor(Color.WHITE)
        button.typeface = google()
        button.text = "Apply"
        button.setOnClickListener(View.OnClickListener {
            val txtneg = edittextneg.text.toString()
            if (!txtneg.contains("https")) {
                Toast.makeText(applicationContext, "Please enter valid URL!", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            val editor = prefs!!.edit()
            editor.putString("surl", txtneg)
            editor.apply()
            aalert!!.dismiss()
            val gtr: Thread = object : Thread() {
                override fun run() {
                    AddS(txtneg)
                }
            }
            gtr.start()
        })

        aalert = AlertDialog.Builder(this, 2).create()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(aalert?.getWindow())?.setType(i)
        }
        linearLayout1.addView(textView)
        linearLayout1.addView(edittextneg)
        linearLayout1.addView(button)
        aalert?.setView(linearLayout1)
        aalert?.show()
    }

    private var ics = false
    private var hide = false
    private fun initMenuButton(view2: View, view3: View) {
        startimage!!.setOnClickListener {
            view2.visibility = View.GONE
            view3.visibility = View.VISIBLE
        }

        textView!!.setOnClickListener {
            if (hide) {
                view2.visibility = View.VISIBLE
                view2.alpha = 0f
                view3.animation = fadein()
                val handler = Handler()
                handler.postDelayed({ view3.visibility = View.GONE }, 300)
            } else {
                view2.visibility = View.VISIBLE
                view2.alpha = 0.90f
                view2.animation = fadeout()
                view3.animation = fadein()
                val handler = Handler()
                handler.postDelayed({ view3.visibility = View.GONE }, 300)
            }
            if (ics) {
                startimage!!.setImageResource(R.drawable.icon_mack)
            } else {
                startimage!!.setImageResource(R.drawable.icon_cat)
            }
            ics = !ics
        }
    }

    fun farv(parent: ViewGroup, searchString: String) {
        for (i in parent.childCount - 1 downTo 0) {
            val child = parent.getChildAt(i)
            if (child is Button && child.text.toString().contains(searchString)) {
                parent.removeViewAt(i)
            } else if (child is TextView && child.text.toString().contains(searchString)) {
                parent.removeViewAt(i)
            } else if (child is ViewGroup) {
                farv(child, searchString)
            }
        }
    }

    private fun CreateMenuList() {
        val listFT = getFeatureList()
        if (showrage) {
            farv(patches!!, "WARNING")
            farv(patches!!, "Unlock Rage Features")
            addTextN("⚠️ RAGE FEATURES")
        }
        for (i in listFT.indices) {
            val feature = i
            val str = listFT[i]
            if (!showrage) {
                if (str.contains(Decrypt("U2Vla0Jhcl8="))) {
                    val split =
                        str.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    addSeekBarN(
                        feature,
                        split[1], split[2].toInt(), split[3].toInt(), object : InterfaceInt {
                            override fun OnWrite(i: Int) {
                                Call(feature, i)
                            }
                        })
                } else if (str.contains(Decrypt("QnV0dG9uXw=="))) {
                    addButtonN(str.replace(Decrypt("QnV0dG9uXw=="), ""), object : InterfaceBtn {
                        override fun OnWrite() {
                            Call(feature, 0)
                        }
                    })
                } else if (str.contains(Decrypt("QnV0dG9uQ18="))) {
                    addButtonN2(str.replace(Decrypt("QnV0dG9uQ18="), ""), object : InterfaceBtn {
                        override fun OnWrite() {
                            Call(feature, 0)
                        }
                    })
                } else if (str.contains(Decrypt("QnV0dG9uSlNf"))) {
                    addButtonN2(str.replace(Decrypt("QnV0dG9uSlNf"), ""), object : InterfaceBtn {
                        override fun OnWrite() {
                            initJsonDialog()
                        }
                    })
                } else if (str.contains(Decrypt("VGV4dF8="))) {
                    addTextN(str.replace(Decrypt("VGV4dF8="), ""))
                } else if (str.contains(Decrypt("SW5wdXRWYWx1ZV8="))) {
                    addTextField(
                        str.replace(Decrypt("SW5wdXRWYWx1ZV8="), ""),
                        feature,
                        object : InterfaceInt {
                            override fun OnWrite(i: Int) {
                                Call(feature, 0)
                            }
                        })
                }
            }
            if (str.contains(Decrypt("QnV0dG9uRV8="))) {
                if (showrage) {
                    addButtonN(str.replace(Decrypt("QnV0dG9uRV8="), ""), object : InterfaceBtn {
                        override fun OnWrite() {
                            Call(feature, 0)
                        }
                    })
                }
            }
        }
        if (!showrage) {
            addTextN("Settings")
            addTextN("")
            addTextN("⚠️ WARNING: Using Rage features might result ban of your account, \"sooner or later\"")
            addButtonU("Unlock Rage Features", object : InterfaceBtn {
                override fun OnWrite() {}
            })
            addButtonN("Hide Menu Icon", object : InterfaceBtn {
                override fun OnWrite() {
                    hide = !hide
                }
            })
        }
    }

    fun google(): Typeface {
        return Typeface.createFromAsset(assets, "google.ttf")
    }

    fun addTextN(str: String): TextView {
        val textView = TextView(this)
        textView.text = Html.fromHtml("<u><b>$str</b></u>")
        textView.setTextColor(Color.WHITE)
        textView.textSize = 12.3f
        textView.typeface = google()
        textView.gravity = 3
        textView.layoutParams = LinearLayout.LayoutParams(-1, -2)
        textView.setPadding(10 + 10, 0, 0, 0)
        patches!!.addView(textView)
        return textView
    }

    fun addSeekBarN(featurenum: Int, feature: String, prog: Int, max: Int, interInt: InterfaceInt) {
        val linearLayout = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(-1, -1)
        linearLayout.setPadding(10, 5, 0, 5)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = 17
        linearLayout.layoutParams = layoutParams

        //Textview
        val textView = TextView(this)
        val str = SliderString(featurenum, 0)
        textView.typeface = google()

        if (str != null)  //Show text progress instead number
            textView.text = "$feature : $str"
        else  //If string is null, show number instead
            textView.text = "$feature : $prog"
        textView.textSize = 13.0f
        textView.gravity = 3
        textView.setTextColor(Color.WHITE)
        textView.layoutParams = LinearLayout.LayoutParams(-1, -2)
        textView.setPadding(5, 0, 0, 0)

        //Seekbar
        val seekBar = SeekBar(this)
        seekBar.progressDrawable.setColorFilter(
            Color.parseColor("#ffff" + "ffff"),
            PorterDuff.Mode.MULTIPLY
        )
        seekBar.thumb.setColorFilter(Color.parseColor("#ffff" + "ffff"), PorterDuff.Mode.MULTIPLY)
        seekBar.max = max
        seekBar.setPadding(25, 10, 35, 10)
        seekBar.layoutParams = LinearLayout.LayoutParams(-1, -1)
        val layoutParams2 = LinearLayout.LayoutParams(-1, -2)
        layoutParams2.bottomMargin = 10
        seekBar.layoutParams = layoutParams2
        seekBar.progress = prog
        seekBar.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        seekBar.scaleX = neg
                        seekBar.scaleY = neg
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        seekBar.scaleX = neg
                        seekBar.scaleY = neg
                    }
                    animator2.start()
                }

                MotionEvent.ACTION_CANCEL -> {
                    val animator3 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator3.setDuration(150)
                    animator3.interpolator = DecelerateInterpolator()
                    animator3.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        seekBar.scaleX = neg
                        seekBar.scaleY = neg
                    }
                    animator3.start()
                }
            }
            false
        }

        val textView2 = textView
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }

            var lastp: Int = 0

            override fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {
                if (seekBar.progress >= max) {
                    val colorAnim: ValueAnimator = ObjectAnimator.ofInt(
                        TextView(baseContext),
                        "textColor",
                        Color.WHITE,
                        Color.parseColor("#ff0000")
                    )
                    colorAnim.setDuration(200)
                    colorAnim.setEvaluator(ArgbEvaluator())
                    colorAnim.addUpdateListener { animation ->
                        seekBar.progressDrawable.setColorFilter(
                            (animation.animatedValue as Int),
                            PorterDuff.Mode.MULTIPLY
                        )
                        seekBar.thumb.setColorFilter(
                            (animation.animatedValue as Int),
                            PorterDuff.Mode.MULTIPLY
                        )
                    }
                    colorAnim.start()
                } else if (seekBar.progress <= (max - 1) && lastp == max) {
                    val colorAnim: ValueAnimator = ObjectAnimator.ofInt(
                        TextView(baseContext),
                        "textColor",
                        Color.parseColor("#ff0000"),
                        Color.WHITE
                    )
                    colorAnim.setDuration(200)
                    colorAnim.setEvaluator(ArgbEvaluator())
                    colorAnim.addUpdateListener { animation ->
                        seekBar.progressDrawable.setColorFilter(
                            (animation.animatedValue as Int),
                            PorterDuff.Mode.MULTIPLY
                        )
                        seekBar.thumb.setColorFilter(
                            (animation.animatedValue as Int),
                            PorterDuff.Mode.MULTIPLY
                        )
                    }
                    colorAnim.start()
                } else {
                    seekBar.progressDrawable.setColorFilter(
                        Color.parseColor("#ffffffff"),
                        PorterDuff.Mode.MULTIPLY
                    )
                    seekBar.thumb.setColorFilter(
                        Color.parseColor("#ffffffff"),
                        PorterDuff.Mode.MULTIPLY
                    )
                }
                lastp = seekBar.progress

                val str = SliderString(featurenum, i)

                interInt.OnWrite(i)
                val textView = textView2

                if (str != null) textView.text = "$feature : $str"
                else textView.text = "$feature : $i"
            }
        })

        linearLayout.addView(textView)
        linearLayout.addView(seekBar)
        patches!!.addView(linearLayout)
    }

    fun addButtonN(feature: String, interfaceBtn: InterfaceBtn) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        val str2 = "#ffffffff"
        gradientDrawable.setColor(Color.parseColor(str2))
        gradientDrawable.setStroke(3, Color.parseColor(str2))
        gradientDrawable.cornerRadius = 8.0f
        val gradientDrawable2 = GradientDrawable()
        gradientDrawable2.shape = GradientDrawable.RECTANGLE
        gradientDrawable2.setColor(0)
        gradientDrawable2.setStroke(3, Color.parseColor(str2))
        gradientDrawable2.cornerRadius = 8.0f

        val button = Button(this)
        val layoutParams = LinearLayout.LayoutParams(-1, -2)
        layoutParams.setMargins(7, 5, 7, 5)
        button.text = feature
        button.setTextColor(Color.WHITE)
        button.typeface = google()
        button.textSize = 14.4f
        button.isAllCaps = false
        button.setBackgroundColor(Color.TRANSPARENT)
        val layoutParams2 = LinearLayout.LayoutParams(-1, dp(40))
        button.setPadding(3, 3, 3, 3)
        layoutParams2.bottomMargin = 0
        button.layoutParams = layoutParams2
        val gays2 = feature
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button.scaleX = neg
                        button.scaleY = neg
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button.scaleX = neg
                        button.scaleY = neg
                    }
                    animator2.start()
                }

                MotionEvent.ACTION_CANCEL -> {
                    val animator3 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator3.setDuration(150)
                    animator3.interpolator = DecelerateInterpolator()
                    animator3.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button.scaleX = neg
                        button.scaleY = neg
                    }
                    animator3.start()
                }
            }
            false
        }
        button.setOnClickListener(object : View.OnClickListener {
            private var isActive = true
            override fun onClick(v: View) {
                interfaceBtn.OnWrite()
                if (isActive) {
                    button.text = Html.fromHtml("<b>$gays2</b>")
                    button.setTextColor(Color.WHITE)
                    val sanim = ObjectAnimator.ofFloat(14.4f, 16.7f)
                    sanim.setDuration(250)
                    sanim.addUpdateListener { animation ->
                        button.textSize = animation.animatedValue as Float
                    }
                    sanim.start()
                    //button.setTextSize(15.7f);
                    val colorAnim: ValueAnimator = ObjectAnimator.ofInt(
                        TextView(baseContext),
                        "textColor",
                        Color.TRANSPARENT,
                        Color.parseColor("#30FFFFFF")
                    )
                    colorAnim.setDuration(250)
                    colorAnim.setEvaluator(ArgbEvaluator())
                    colorAnim.addUpdateListener { animation ->
                        button.setBackgroundColor(
                            (animation.animatedValue as Int)
                        )
                    }
                    colorAnim.start()
                    isActive = false
                    return
                }
                button.text = gays2
                button.setTextColor(Color.WHITE)
                val sanim = ObjectAnimator.ofFloat(16.7f, 14.4f)
                sanim.setDuration(250)
                sanim.addUpdateListener { animation ->
                    button.textSize = animation.animatedValue as Float
                }
                sanim.start()
                val colorAnim: ValueAnimator = ObjectAnimator.ofInt(
                    TextView(baseContext),
                    "textColor",
                    Color.parseColor("#30FFFFFF"),
                    Color.TRANSPARENT
                )
                colorAnim.setDuration(250)
                colorAnim.setEvaluator(ArgbEvaluator())
                colorAnim.addUpdateListener { animation ->
                    button.setBackgroundColor(
                        (animation.animatedValue as Int)
                    )
                }
                colorAnim.start()
                button.setBackgroundColor(Color.TRANSPARENT)
                isActive = true
            }
        })
        patches!!.addView(button)
    }

    fun addButtonN2(feature: String, interfaceBtn: InterfaceBtn) {
        //if (WrapperReceiver.check) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        val str2 = "#ffffffff"
        gradientDrawable.setColor(Color.parseColor(str2))
        gradientDrawable.setStroke(3, Color.parseColor(str2))
        gradientDrawable.cornerRadius = 8.0f
        val gradientDrawable2 = GradientDrawable()
        gradientDrawable2.shape = GradientDrawable.RECTANGLE
        gradientDrawable2.setColor(0)
        gradientDrawable2.setStroke(3, Color.parseColor(str2))
        gradientDrawable2.cornerRadius = 8.0f

        val button2 = Button(this)
        val layoutParams = LinearLayout.LayoutParams(-1, -2)
        layoutParams.setMargins(7, 5, 7, 5)
        button2.text = feature
        button2.setTextColor(Color.WHITE)
        button2.textSize = 14.4f
        button2.typeface = google()
        button2.isAllCaps = false
        button2.background = resources.getDrawable(R.drawable.btn_def)
        val layoutParams2 = LinearLayout.LayoutParams(-1, dp(48))
        button2.setPadding(3, 3, 3, 3)
        layoutParams2.bottomMargin = 0
        button2.layoutParams = layoutParams2
        button2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator2.start()
                }

                MotionEvent.ACTION_CANCEL -> {
                    val animator3 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator3.setDuration(150)
                    animator3.interpolator = DecelerateInterpolator()
                    animator3.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator3.start()
                }
            }
            false
        }
        val gays2 = feature
        button2.setOnClickListener {
            val anim = ObjectAnimator.ofFloat(14.4f, 16.7f, 14.4f)
            anim.setDuration(150)
            anim.addUpdateListener { animation ->
                button2.textSize = animation.animatedValue as Float
            }
            anim.start()
            button2.text = Html.fromHtml("<b>$gays2</b>")
            val handler = Handler()
            handler.postDelayed({
                button2.text = gays2
            }, 75)
            interfaceBtn.OnWrite()
        }
        patches!!.addView(button2)
        //}
    }

    fun addButtonU(feature: String, interfaceBtn: InterfaceBtn?) {
        //if (WrapperReceiver.check) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        val str2 = "#ffffffff"
        gradientDrawable.setColor(Color.parseColor(str2))
        gradientDrawable.setStroke(3, Color.parseColor(str2))
        gradientDrawable.cornerRadius = 8.0f
        val gradientDrawable2 = GradientDrawable()
        gradientDrawable2.shape = GradientDrawable.RECTANGLE
        gradientDrawable2.setColor(0)
        gradientDrawable2.setStroke(3, Color.parseColor(str2))
        gradientDrawable2.cornerRadius = 8.0f

        val button2 = Button(this)
        val layoutParams = LinearLayout.LayoutParams(-1, -2)
        layoutParams.setMargins(7, 5, 7, 5)
        button2.text = feature
        button2.setTextColor(Color.WHITE)
        button2.textSize = 14.4f
        button2.typeface = google()
        button2.isAllCaps = false

        button2.background = resources.getDrawable(R.drawable.btn_def)
        val layoutParams2 = LinearLayout.LayoutParams(-1, dp(48))
        button2.setPadding(3, 3, 3, 3)
        layoutParams2.bottomMargin = 0
        button2.layoutParams = layoutParams2
        button2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator2.start()
                }

                MotionEvent.ACTION_CANCEL -> {
                    val animator3 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator3.setDuration(150)
                    animator3.interpolator = DecelerateInterpolator()
                    animator3.addUpdateListener { animation: ValueAnimator ->
                        val neg = animation.animatedValue as Float
                        button2.scaleX = neg
                        button2.scaleY = neg
                    }
                    animator3.start()
                }
            }
            false
        }
        val gays2 = feature

        button2.setOnClickListener {
            val anim = ObjectAnimator.ofFloat(14.4f, 16.7f, 14.4f)
            anim.setDuration(150)
            anim.addUpdateListener { animation ->
                button2.textSize = animation.animatedValue as Float
            }
            anim.start()
            button2.text = Html.fromHtml("<b>$gays2</b>")
            val handler = Handler()
            handler.postDelayed({
                button2.text = gays2
            }, 75)
            showrage = true
            CreateMenuList()
        }
        patches!!.addView(button2)
        //}
    }

    val isViewCollapsed: Boolean
        get() = mFloatingView == null || mCollapsed!!.visibility == View.VISIBLE

    private fun convertDipToPixels(i: Int): Int {
        return (((i.toFloat()) * resources.displayMetrics.density) + 0.5f).toInt()
    }

    private fun dpi(): Int {
        val metrics = resources.displayMetrics
        return (metrics.density * 160f).toInt()
    }

    private fun dp(i: Int): Int {
        if (dpi() > 400) return TypedValue.applyDimension(1, i.toFloat(), resources.displayMetrics)
            .toInt() - 20
        return TypedValue.applyDimension(1, i.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        val view = mFloatingView
        if (view != null) {
            mWindowManager!!.removeView(view)
        }
    }

    private val isNotInGame: Boolean
        get() {
            val runningAppProcessInfo = RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(runningAppProcessInfo)
            return runningAppProcessInfo.importance != 100
        }

    override fun onTaskRemoved(intent: Intent) {
        stopSelf()
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        super.onTaskRemoved(intent)
    }

    /* access modifiers changed from: private */
    fun Thread() {
        if (mFloatingView == null) {
            return
        }
        if (isNotInGame) {
            //mFloatingView.setVisibility(View.INVISIBLE);
        } else {
            mFloatingView!!.visibility = View.VISIBLE
        }
    }

    interface InterfaceBtn {
        fun OnWrite()
    }

    interface InterfaceInt {
        fun OnWrite(i: Int)
    }

    private interface InterfaceBool {
        fun OnWrite(z: Boolean)
    }

    private interface InterfaceStr {
        fun OnWrite(s: String?)
    }

    companion object {
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

        external fun DrawOn(espView: MenuCanvas?, canvas: Canvas?)

        external fun getFeatureList(): Array<String>

        external fun Call(feature: Int, value: Int)

        external fun AddS(url: String)

        external fun engine(): String?

        external fun manf(): String?

        external fun SliderString(feature: Int, value: Int): String?

        var overlayView: MenuCanvas? = null

        var espParams: WindowManager.LayoutParams? = null

        fun setCornerRadius(gradientDrawable: GradientDrawable, f: Float) {
            gradientDrawable.cornerRadius = f
        }

        fun setCornerRadius(
            gradientDrawable: GradientDrawable,
            f: Float,
            f2: Float,
            f3: Float,
            f4: Float
        ) {
            gradientDrawable.cornerRadii = floatArrayOf(f, f, f2, f2, f3, f3, f4, f4)
        }
    }
}
