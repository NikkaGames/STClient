package ge.nikka.stclient

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.MaterialToolbar
import ge.nikka.stclient.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private external fun start(): Int
    private external fun stopc()

    override fun onDestroy() {
        super.onDestroy()
        Process.killProcess(Process.myPid())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        current = this
        MenuCanvas.display = (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
        try {
            Runtime.getRuntime().exec("su")
        } catch (ex: IOException) {
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        //setSupportActionBar(binding.toolbar);

        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.removeAllViews()
        val pla = LinearLayout(this)
        pla.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        pla.gravity = Gravity.CENTER
        pla.orientation = LinearLayout.HORIZONTAL
        pla.setPadding(-180, 0, 0, 0)

        val pfp = ImageView(this)
        pfp.setImageResource(R.drawable.ic_launcher_foreground)
        pfp.scaleX = 0.9f
        pfp.scaleY = 0.9f
        pfp.setPadding(4, 4, 4, 4)

        pla.addView(pfp)

        val label = TextView(this)
        label.text = resources.getString(R.string.app_name)
        label.textSize = 24f
        label.typeface = ResourcesCompat.getFont(this, R.font.mfont)
        label.setTextColor(Color.WHITE)
        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lparams.setMargins(-70, 0, 0, 0)

        label.layoutParams = lparams

        pla.addView(label)

        toolbar.addView(pla)


        //final EditTextCursorWatcher ipvalue = binding.ipeditHome;
        //final EditTextCursorWatcher portvalue = binding.porteditHome;
        val textView = binding!!.textHome
        val button = binding!!.buttonHome
        val sbutton = binding!!.sbuttonHome


        //textView.setText(ui_dpi_scale + "");
        textView.text = "Status: Not connected"


        /*ipvalue.setHint("IP Address…");
        ipvalue.setText("127.0.0.1");
        ipvalue.setMaxLines(1);
        
        portvalue.setHint("Port…");
        portvalue.setText("55555");
        portvalue.setMaxLines(1);
        portvalue.setInputType(InputType.TYPE_CLASS_NUMBER);
        portvalue.setKeyListener(DigitsKeyListener.getInstance("0123456789"));*/
        button.text = "START MENU SERVICE"
        button.setOnClickListener {
            val stat = start()
            if (stat == 0) {
                applicationContext.startService(
                    Intent(
                        applicationContext,
                        FloatingWindow::class.java
                    )
                )
                textView.text = "Status: Started"
                button.isEnabled = false
                sbutton.isEnabled = true
                val intent = Intent()
                intent.setComponent(
                    ComponentName(
                        "com.axlebolt.standoff2",
                        "com.google.firebase.MessagingUnityPlayerActivity"
                    )
                )
                //  startActivity(intent);
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to connect!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        button.scaleX = `val`
                        button.scaleY = `val`
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        button.scaleX = `val`
                        button.scaleY = `val`
                    }
                    animator2.start()
                }
            }
            false
        }

        sbutton.text = "STOP MENU SERVICE"
        sbutton.setOnClickListener {
            stopc()
            stopService(
                Intent(
                    this@MainActivity,
                    FloatingWindow::class.java
                )
            )
            sbutton.isEnabled = false
            //button.setEnabled(true);
            textView.text = "Status: Disconnected"
            try {
                Runtime.getRuntime().exec("su -c kill $(pidof com.axlebolt.standoff2)")
            } catch (ex: IOException) {
            }
            this@MainActivity.finish()
            Process.killProcess(Process.myPid())
        }
        sbutton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val animator = ValueAnimator.ofFloat(1f, 0.95f)
                    animator.setDuration(150)
                    animator.interpolator = DecelerateInterpolator()
                    animator.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        sbutton.scaleX = `val`
                        sbutton.scaleY = `val`
                    }
                    animator.start()
                }

                MotionEvent.ACTION_UP -> {
                    val animator2 = ValueAnimator.ofFloat(0.95f, 1f)
                    animator2.setDuration(150)
                    animator2.interpolator = DecelerateInterpolator()
                    animator2.addUpdateListener { animation: ValueAnimator ->
                        val `val` = animation.animatedValue as Float
                        sbutton.scaleX = `val`
                        sbutton.scaleY = `val`
                    }
                    animator2.start()
                }
            }
            false
        }


        //sbutton.setEnabled(false);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                this
            )
        ) {
            startActivity(
                Intent(
                    "android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(
                        "package:$packageName"
                    )
                )
            )
            finish()
        } else {
            val handler = Handler()
            handler.postDelayed({
                //startService(new Intent(MainActivity.this, FloatingWindow.class));
            }, 500)
        }
        Storage(this)
        // Check if the permission is not granted
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 5738)
        }

        // Check if the permission is not granted
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 8573)
        }
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent()
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.setData(Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }

    companion object {
        fun Storage(ctx: Activity) {
            if (Build.VERSION.SDK_INT < 30 || Environment.isExternalStorageManager()) {
                return
            }
            ctx.startActivityForResult(
                Intent(
                    "android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION",
                    Uri.parse("package:" + ctx.packageName)
                ), 102
            )
            // Check if the app has permission to install unknown apps
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!ctx.packageManager.canRequestPackageInstalls()) {
                    // Request permission to install unknown apps
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    intent.setData(Uri.parse("package:" + ctx.packageName))
                    ctx.startActivityForResult(intent, 103)
                }
            }
        }

        fun getPercentOfScreenWidthInPx(context: Context, percent: Float): Int {
            val displayMetrics = context.resources.displayMetrics
            val screenWidthPx = displayMetrics.widthPixels
            val seventyFivePercentWidthPx = (screenWidthPx * percent).toInt()
            return seventyFivePercentWidthPx
        }

        fun hello(): Boolean {
            return true
        }

        var current: MainActivity? = null
    }
}