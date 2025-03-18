package ge.nikka.stclient;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.*;
import android.os.*;

import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.res.ResourcesCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import ge.nikka.edk.*;
import android.provider.*;
import com.google.android.material.bottomnavigation.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ge.nikka.stclient.databinding.ActivityMainBinding;
import ge.nikka.stclient.ui.home.EditTextCursorWatcher;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    
    public static void Storage(Activity ctx) {
        if (Build.VERSION.SDK_INT < 30 || Environment.isExternalStorageManager()) {
            return;
        }
        ctx.startActivityForResult(new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION", Uri.parse("package:" + ctx.getPackageName())), 102);
        // Check if the app has permission to install unknown apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!ctx.getPackageManager().canRequestPackageInstalls()) {
                // Request permission to install unknown apps
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent.setData(Uri.parse("package:" + ctx.getPackageName()));
                ctx.startActivityForResult(intent, 103);
            }
        }
    }
    
    public static int getPercentOfScreenWidthInPx(Context context, float percent) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidthPx = displayMetrics.widthPixels;
        int seventyFivePercentWidthPx = (int) (screenWidthPx * percent);
        return seventyFivePercentWidthPx;
    }
    
    public static boolean hello() {
        return true;
    }
    
    private native int start();
    private native void stopc();
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MenuCanvas.display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException ex) {}
        System.loadLibrary("qcomm");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        //setSupportActionBar(binding.toolbar);
        
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.removeAllViews();
        LinearLayout pla = new LinearLayout(this);
        pla.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        pla.setGravity(Gravity.CENTER);
        pla.setOrientation(LinearLayout.HORIZONTAL);
        pla.setPadding(-180, 0, 0, 0);
        
        ImageView pfp = new ImageView(this);
        pfp.setImageResource(R.drawable.ic_launcher_foreground);
        pfp.setScaleX(0.9f);
        pfp.setScaleY(0.9f);
        pfp.setPadding(4, 4, 4, 4);
        
        pla.addView(pfp);
        
        TextView label = new TextView(this);
        label.setText(getResources().getString(R.string.app_name));
        label.setTextSize(24);
        label.setTypeface(ResourcesCompat.getFont(this, R.font.mfont));
        label.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(-70, 0, 0, 0);
        
        label.setLayoutParams(lparams);
        
        pla.addView(label);
        
        toolbar.addView(pla);
        
        //final EditTextCursorWatcher ipvalue = binding.ipeditHome;
        //final EditTextCursorWatcher portvalue = binding.porteditHome;
        final TextView textView = binding.textHome;
        final Button button = binding.buttonHome;
        final Button sbutton = binding.sbuttonHome;
        
        //textView.setText(ui_dpi_scale + "");
        textView.setText("Status: Not connected");
        
        /*ipvalue.setHint("IP Address…");
        ipvalue.setText("127.0.0.1");
        ipvalue.setMaxLines(1);
        
        portvalue.setHint("Port…");
        portvalue.setText("55555");
        portvalue.setMaxLines(1);
        portvalue.setInputType(InputType.TYPE_CLASS_NUMBER);
        portvalue.setKeyListener(DigitsKeyListener.getInstance("0123456789"));*/
        
        button.setText("START MENU SERVICE");
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
                int stat = start();
                if (stat == 0) {
                    getApplicationContext().startService(new Intent(getApplicationContext(), FloatingWindow.class));
                    textView.setText("Status: Started");
                    button.setEnabled(false);
                    sbutton.setEnabled(true);
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.axlebolt.standoff2", "com.google.firebase.MessagingUnityPlayerActivity"));
                  //  startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to connect!", Toast.LENGTH_SHORT).show();
                }
				
			}
		});
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ValueAnimator animator = ValueAnimator.ofFloat(1.f, 0.95f);
                        animator.setDuration(150);
                        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            button.setScaleX(val);
                            button.setScaleY(val);
                        });
                        animator.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ValueAnimator animator2 = ValueAnimator.ofFloat(0.95f, 1.f);
                        animator2.setDuration(150);
                        animator2.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator2.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            button.setScaleX(val);
                            button.setScaleY(val);
                        });
                        animator2.start();
                        break;
                }
                return false;
            }
        });
        
        sbutton.setText("STOP MENU SERVICE");
        sbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
                stopc();
                stopService(new Intent(MainActivity.this, FloatingWindow.class));
                sbutton.setEnabled(false);
                //button.setEnabled(true);
                textView.setText("Status: Disconnected");
                try {
                    Runtime.getRuntime().exec("su -c kill $(pidof com.axlebolt.standoff2)");
                } catch (IOException ex) {}
                MainActivity.this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
        sbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ValueAnimator animator = ValueAnimator.ofFloat(1.f, 0.95f);
                        animator.setDuration(150);
                        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            sbutton.setScaleX(val);
                            sbutton.setScaleY(val);
                        });
                        animator.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        ValueAnimator animator2 = ValueAnimator.ofFloat(0.95f, 1.f);
                        animator2.setDuration(150);
                        animator2.setInterpolator(new android.view.animation.DecelerateInterpolator());
                        animator2.addUpdateListener(animation -> {
                            float val = (float)animation.getAnimatedValue();
                            sbutton.setScaleX(val);
                            sbutton.setScaleY(val);
                        });
                        animator2.start();
                        break;
                }
                return false;
            }
        });
        
        
        //sbutton.setEnabled(false);
        
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);*/
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
			startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())));
            finish();
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					//startService(new Intent(MainActivity.this, FloatingWindow.class));
				}
			}, 500);
        }
        Storage(this);
        // Check if the permission is not granted
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5738);
        }

        // Check if the permission is not granted
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 8573);
        }
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

}