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

        //********** Gradients **********
        gradientDrawable = GradientDrawable()
        gradientDrawable!!.shape = GradientDrawable.RECTANGLE
        gradientDrawable!!.setColor(Color.parseColor(a + b + c + d + e + f + g + h + j))
        //gradientDrawable.setStroke(2, (int)colorAnim.getAnimatedValue());
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

        //********** Mod menu image **********
        startimage = ImageView(baseContext)
        startimage!!.layoutParams = RelativeLayout.LayoutParams(-2, -2)
        val applyDimension = TypedValue.applyDimension(1, 57f, resources.displayMetrics).toInt()
        startimage!!.layoutParams.height = applyDimension
        startimage!!.layoutParams.width = applyDimension
        startimage!!.requestLayout()
        startimage!!.scaleType = ImageView.ScaleType.FIT_XY
        //menu icon
        //byte[] decode = Base64.decode("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", 0);
        //startimage.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        startimage!!.setImageResource(R.drawable.icon_mack)
        startimage!!.imageAlpha = 500
        (startimage!!.layoutParams as MarginLayoutParams).topMargin = convertDipToPixels(10)

        //********** Mod menu box **********
        mExpanded!!.visibility = View.INVISIBLE
        mExpanded!!.background = gradientDrawable
        mExpanded!!.gravity = 17
        mExpanded!!.orientation = LinearLayout.VERTICAL
        mExpanded!!.layoutParams = LinearLayout.LayoutParams(800, 800) //-1


        val scrollView = ScrollView(baseContext)
        scrollView.layoutParams = LinearLayout.LayoutParams(-1, 625)

        //********** Feature list **********
        patches!!.layoutParams = LinearLayout.LayoutParams(-1, -1)
        patches!!.setBackgroundColor(Color.parseColor("#000000"))
        patches!!.background = gradientDrawable2
        patches!!.orientation = LinearLayout.VERTICAL

        Btns!!.layoutParams = LinearLayout.LayoutParams(-1, -2)
        Btns!!.setBackgroundColor(0)
        Btns!!.gravity = 5
        Btns!!.setPadding(0, 0, 5, 0)
        Btns!!.orientation = LinearLayout.HORIZONTAL

        //********** Title text **********
        textView = TextView(baseContext)
        textView!!.text = engine()
        textView!!.typeface = google()
        if (dpi() > 400) textView!!.textSize = 21.0f
        else textView!!.textSize = 20.0f
        //textView.setTextColor(Color.parseColor("#ffaf1800"));
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
        //textView.setOnTouchListener(onTouchListener());
        val layoutParams = LinearLayout.LayoutParams(-2, -2)
        layoutParams.gravity = 17

        val textvieww = TextView(this)
        textvieww.text = "Version: 0.33.0"
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

        //********** Close button **********
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

        //********** Add views **********
        //new LinearLayout.LayoutParams(-1, dp(80)).topMargin = dp(2);
        frameLayout.addView(relativeLayout)
        relativeLayout.addView(this.mCollapsed)
        relativeLayout.addView(this.mExpanded)
        mCollapsed!!.addView(startimage)
        mExpanded!!.addView(textView)
        mExpanded!!.addView(textvieww)
        mExpanded!!.addView(scrollView)
        scrollView.addView(patches)

        //this.mExpanded.addView(this.Btns);
        //this.Btns.addView(this.close);
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
                            //Toast.makeText(getApplicationContext(), Html.fromHtml("Mod Menu By ProCode & Nikka"), Toast.LENGTH_SHORT).show();
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

        //textView.setGravity(Gravity.CENTER | Gravity.LEFT);
        val edittextneg = edittextvalue()

        val layoutParams2 = RelativeLayout.LayoutParams(220, 110)
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

        val button2 = Button(this)
        button2.layoutParams = layoutParams2
        //button2.setBackgroundColor(Color.TRANSPARENT);
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
        //linearLayout.setBackgroundColor(Color.parseColor("#14171f"));
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

                //Toast.makeText(view.getContext(), Base64Utils.Decrypt("SeG0hOG0j8m0IMqcyarhtIXhtIXhtIfJtC4gUuG0h+G0jeG0h+G0jcqZ4bSHyoAg4bSbypzhtIcgypzJquG0heG0heG0h8m0IMmq4bSE4bSPybQg4bSY4bSPc8mq4bSbyarhtI/JtA=="), Toast.LENGTH_LONG).show();
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
        //if (WrapperReceiver.check) {
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
        //}
    }

    fun addButtonN(feature: String, interfaceBtn: InterfaceBtn) {
        //if (WrapperReceiver.check) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        val str2 = "#ffff" + "ffff"
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
        //button.setBackground(getResources().getDrawable(R.drawable.btn_def));
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
                    //button.setBackgroundColor(Color.parseColor("#30FFFFFF"));
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
                //button.setTextSize(14.4f);
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
        //}
    }

    fun addButtonN2(feature: String, interfaceBtn: InterfaceBtn) {
        //if (WrapperReceiver.check) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        val str2 = "#ffff" + "ffff"
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
        //button2.setBackgroundColor(Color.TRANSPARENT);
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
        button2.setOnClickListener { //button2.setBackgroundColor(Color.parseColor("#30FF" + "FFFF"));
            val anim = ObjectAnimator.ofFloat(14.4f, 16.7f, 14.4f)
            anim.setDuration(150)
            anim.addUpdateListener { animation ->
                button2.textSize = animation.animatedValue as Float
            }
            anim.start()
            //button2.setTextSize(16.7f);
            button2.text = Html.fromHtml("<b>$gays2</b>")
            val handler = Handler()
            handler.postDelayed({
                //button2.setBackgroundColor(Color.TRANSPARENT);
                button2.text = gays2
                //button2.setTextSize(14.4f);
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
        val str2 = "#ffff" + "ffff"
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
        //button2.setBackgroundColor(Color.TRANSPARENT);
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

        button2.setOnClickListener { //button2.setBackgroundColor(Color.parseColor("#30FF" + "FFFF"));
            val anim = ObjectAnimator.ofFloat(14.4f, 16.7f, 14.4f)
            anim.setDuration(150)
            anim.addUpdateListener { animation ->
                button2.textSize = animation.animatedValue as Float
            }
            anim.start()
            //button2.setTextSize(16.7f);
            button2.text = Html.fromHtml("<b>$gays2</b>")
            val handler = Handler()
            handler.postDelayed({
                //button2.setBackgroundColor(Color.TRANSPARENT);
                button2.text = gays2
                //button2.setTextSize(14.4f);
            }, 75)
            //FloatingWindow.this.patches.removeAllViews();
            showrage = true
            CreateMenuList()
        }
        patches!!.addView(button2)
        //}
    }

    val isViewCollapsed: Boolean
        get() = mFloatingView == null || mCollapsed!!.visibility == View.VISIBLE

    //For our image a little converter
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

    //Destroy our View
    override fun onDestroy() {
        super.onDestroy()
        val view = mFloatingView
        if (view != null) {
            mWindowManager!!.removeView(view)
        }
    }

    private val isNotInGame: Boolean
        //Check if we are still in the game. If now our Menu and Menu button will dissapear
        get() {
            val runningAppProcessInfo = RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(runningAppProcessInfo)
            return runningAppProcessInfo.importance != 100
        }

    //Same as above so it wont crash in the background and therefore use alot of Battery life
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
