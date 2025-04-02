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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thiz = this
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var statusText by remember { mutableStateOf("Status: Not connected") }
    var isServiceRunning by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

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
                fontSize = 18.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.google))
            )

            Spacer(modifier = Modifier.height(64.dp))

            val coroutineScope = rememberCoroutineScope()

            ActionButton(
                text = "START SERVICE",
                onClick = {
                    showBottomSheet = true
                    val th = Thread {
                        Thread.sleep(300)
                        val stat = MainActivity.start()
                        if (stat == 0) {
                            context.startService(Intent(context, FloatingWindow::class.java))
                            statusText = "Status: Started"
                            isServiceRunning = true
                        } else {
                            Toast.makeText(context, "Failed to connect!", Toast.LENGTH_SHORT).show()
                        }
                        showBottomSheet = false
                    }
                    th.start()
                },
                enabled = !isServiceRunning
            )

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
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    scrimColor = Color.Black.copy(alpha = 0.7f),
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
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.height(4.dp))

            ActionButton(
                text = "STOP SERVICE",
                onClick = {
                    MainActivity.stopc()
                    context.stopService(Intent(context, FloatingWindow::class.java))
                    isServiceRunning = false
                    statusText = "Status: Disconnected"
                    MainActivity.thiz?.finish()
                    Process.killProcess(Process.myPid())
                },
                enabled = isServiceRunning
            )
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
