package ge.nikka.stclient

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import ge.nikka.stclient.ui.theme.STClientTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(this)
        setContent {
            STClientTheme {
                MainScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Process.killProcess(Process.myPid())
    }

    companion object {
        external fun start(): Int
        external fun stopc()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var statusText by remember { mutableStateOf("Status: Not connected") }
    var isServiceRunning by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 24.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.mfont))
                    )
                }
            },
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(end = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                statusText,
                fontSize = 20.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.google))
            )

            Spacer(modifier = Modifier.height(64.dp))

            ActionButton(
                text = "START MENU SERVICE",
                onClick = {
                    val stat = MainActivity.start()
                    if (stat == 0) {
                        context.startService(Intent(context, FloatingWindow::class.java))
                        statusText = "Status: Started"
                        isServiceRunning = true
                    } else {
                        Toast.makeText(context, "Failed to connect!", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isServiceRunning
            )

            Spacer(modifier = Modifier.height(4.dp))

            ActionButton(
                text = "STOP MENU SERVICE",
                onClick = {
                    MainActivity.stopc()
                    context.stopService(Intent(context, FloatingWindow::class.java))
                    isServiceRunning = false
                    statusText = "Status: Disconnected"
                    Process.killProcess(Process.myPid())
                },
                enabled = isServiceRunning
            )
        }
    }
}


@Composable
fun ActionButton(text: String, onClick: () -> Unit, enabled: Boolean) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.95f else 1f, animationSpec = tween(150))

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.graphicsLayer(
            scaleX = scale,
            scaleY = scale)
            .clickable { pressed = true }
            .padding(2.dp)
    ) {
        Text(text, fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.googlebold)))
    }
}

fun requestPermissions(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!Settings.canDrawOverlays(activity)) {
            activity.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${activity.packageName}")))
            activity.finish()
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 5738)
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 8573)
        }
    }
    val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
    if (!pm.isIgnoringBatteryOptimizations(activity.packageName)) {
        activity.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${activity.packageName}")))
    }
}
