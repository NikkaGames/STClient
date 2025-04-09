package ge.nikka.stclient

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import ge.nikka.stclient.ui.theme.STClientTheme
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thiz = this
        MenuCanvas.display = this.display
        System.loadLibrary("qcomm")
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
        @JvmField
        var thiz : MainActivity? = null
        external fun start(): Int
        external fun stopc()
        external fun cp()
        external fun jmp()
        @JvmStatic
        fun grantUFS(ctx: Activity) {
            if (Build.VERSION.SDK_INT < 30 || Environment.isExternalStorageManager()) {
                return
            }
            ctx.startActivityForResult(
                Intent(
                    "android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION",
                    ("package:" + ctx.packageName).toUri()
                ), 102
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var statusText by remember { mutableStateOf("Status: idle") }
    var isServiceRunning by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showTimeSheet by remember { mutableStateOf(false) }
    var showFSheet by remember { mutableStateOf(false) }
    var applyTint by remember { mutableStateOf(false) }

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
                        contentScale = ContentScale.Crop,
                        colorFilter = if (applyTint) ColorFilter.tint(Color(0x8000FF00), BlendMode.SrcAtop) else null
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
                fontSize = 18.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.google))
            )

            Spacer(modifier = Modifier.height(64.dp))

            ActionButton(
                text = "Start Service",
                onClick = {
                    showBottomSheet = true
                    val th = Thread {
                        Thread.sleep(300)
                        val stat = MainActivity.start()
                        when (stat) {
                            -1 -> showTimeSheet = true
                            0 -> {
                                context.startService(Intent(context, FloatingWindow::class.java))
                                statusText = "Status: started"
                                isServiceRunning = true
                                applyTint = true
                            }
                            2 -> showTimeSheet = true
                            3 -> showFSheet = true
                            else -> showTimeSheet = true
                        }
                        showBottomSheet = false
                    }
                    th.start()
                },
                enabled = !isServiceRunning
            )

            Spacer(modifier = Modifier.height(4.dp))

            ActionButton(
                text = "Terminate",
                onClick = {
                    MainActivity.stopc()
                    context.stopService(Intent(context, FloatingWindow::class.java))
                    isServiceRunning = false
                    statusText = "Status: disconnected"
                    MainActivity.thiz?.finish()
                    Process.killProcess(Process.myPid())
                },
                enabled = isServiceRunning
            )

            if (showTimeSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showTimeSheet = false
                        MainActivity.thiz?.finish()
                        Process.killProcess(Process.myPid())
                    },
                    containerColor = Color(0xFF090909),
                    contentColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Timestamp mismatch or connection error!",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.google)),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            if (showFSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showFSheet = false
                        MainActivity.thiz?.finish()
                        Process.killProcess(Process.myPid())
                    },
                    containerColor = Color(0xFF090909),
                    contentColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Unknown Device",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily(Font(R.font.googlebold))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp),
                            text = "Your device is unrecognized and can't be allowed to continue!",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.google)),
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val coroutineScope = rememberCoroutineScope()
                            ActionButton(
                                onClick = {
                                    MainActivity.jmp()
                                    coroutineScope.launch {
                                        delay(300)
                                        showFSheet = false
                                        MainActivity.thiz?.finish()
                                        Process.killProcess(Process.myPid())
                                    }
                                },
                                text = "Get Access",
                                enabled = true
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ActionButton(
                                onClick = {
                                    MainActivity.cp()
                                    coroutineScope.launch {
                                        delay(555)
                                        showFSheet = false
                                        MainActivity.thiz?.finish()
                                        Process.killProcess(Process.myPid())
                                    }
                                },
                                text = "Copy UID",
                                enabled = true
                            )
                        }
                    }
                }
            }

            if (showBottomSheet) {
                val bottomSheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true,
                    confirmValueChange = { newState ->
                        newState != SheetValue.Hidden
                    }
                )
                ModalBottomSheet(
                    onDismissRequest = {},
                    sheetState = bottomSheetState,
                    containerColor = Color(0xFF090909),
                    contentColor = Color.White,
                    scrimColor = Color.Black.copy(alpha = 0.8f),
                    properties = ModalBottomSheetDefaults.properties(
                        shouldDismissOnBackPress = false
                    ),
                    dragHandle = {
                        Box(
                            modifier = Modifier
                                .width(0.dp)
                                .height(0.dp)
                                .background(Color.Black)
                        )
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier
                                    .size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Loading...",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.google)),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, enabled: Boolean) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (interactionSource.collectIsPressedAsState().value) 0.95f else 1f,
        animationSpec = tween(150)
    )
    val alpha = if (enabled) 1f else 0.4f
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alpha
            )
            .padding(2.dp)
    ) {
        Text(
            text,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.googlebold))
        )
    }
}

fun requestPermissions(activity: Activity) {
    if (!Settings.canDrawOverlays(activity)) {
        activity.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${activity.packageName}".toUri()))
        activity.finish()
    }
    if (Build.VERSION.SDK_INT >= 30) {
        MainActivity.grantUFS(activity)
    } else {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 5738)
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 8573)
        }
    }
    val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
    if (!pm.isIgnoringBatteryOptimizations(activity.packageName)) {
        activity.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            "package:${activity.packageName}".toUri()))
    }
}
