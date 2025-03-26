package com.amrk000.ocr.presentation.view

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.TorchState
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LifecycleStopOrDisposeEffectResult
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amrk000.ocr.R
import com.amrk000.ocr.presentation.view.ui.components.CameraView
import com.amrk000.ocr.presentation.view.ui.components.ScannedTextSheet
import com.amrk000.ocr.presentation.view.ui.theme.OCRTheme
import com.amrk000.ocr.presentation.viewModel.MainViewModel
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition{ true }

        enableEdgeToEdge()
        setContent {
            LaunchedEffect(key1 = Unit) {
                delay(1000)
                splashScreen.setKeepOnScreenCondition { false }
            }
            OCRTheme { MainActivityUi() }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityUi(){
    val context =  LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: MainViewModel = viewModel()

    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val permission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { result: Boolean ->
        if (result) {
            Toast.makeText(context, "Camera Permission Allowed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        LaunchedEffect(key1 = Unit) {
            permission.launch(Manifest.permission.CAMERA)
        }
    }

    val scanResult by viewModel.getScanResultObserver().observeAsState(initial = "No Text Detected")

    var previewImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var isSheetOpen by remember {
        mutableStateOf(false)
    }

    BackHandler {
        viewModel.onBackPressed()
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val image = result.data?.data
            previewImage = MediaStore.Images.Media.getBitmap(context.contentResolver, image)

            val inputImage = InputImage.fromBitmap(previewImage!!, 0)
            viewModel.scanTextFromImage(inputImage)
        }
    }

    val camera = CameraView(context, lifecycleOwner, viewModel.getImageCapture())

    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 25.dp, start = 10.dp, end = 10.dp)) {
        Image(painter = painterResource(R.drawable.logo_dark),
            contentDescription = null,
            modifier = Modifier
                .wrapContentWidth()
                .height(90.dp)
                .align(Alignment.Center)
                .padding(top = 5.dp))

        IconButton(onClick = {
            val intent = Intent(context, AboutActivity::class.java)
            startActivity(context, intent, null)
        }, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(painterResource(R.drawable.baseline_info_outline_24), null, modifier = Modifier.size(35.dp), tint = Color.White)
        }

        IconButton(onClick = {
            val intent = Intent(context, HistoryActivity::class.java)
            startActivity(context, intent, null)
        }, modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(painterResource(R.drawable.baseline_history_24), null, modifier = Modifier.size(35.dp), tint = Color.White)
        }

    }

    CameraControls(
        captureImage = {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            viewModel.captureImageAndScan()
        },
        loadImage = {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imagePicker.launch(intent)
        },
        toggleFlash = {
            camera.cameraControl.enableTorch(camera.cameraInfo.torchState.value != TorchState.ON)
        })

    viewModel.getCapturedImageObserver().observe(lifecycleOwner) {
        previewImage = it
    }
    ScannedImageView(previewImage)


    viewModel.getScanResultObserver().observe(lifecycleOwner) {
        isSheetOpen = true
    }

    val modalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if(isSheetOpen) {
        ScannedTextSheet(modalSheetState, scanResult,
            onDismiss = {
                isSheetOpen = false
                previewImage = null
            },
            onShare = {
                val share: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, scanResult)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(share, "Share Text"))
            },
            onCopy = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val textData = ClipData.newPlainText("OCR: Text", scanResult)
                clipboard.setPrimaryClip(textData)
            },
            onClose = {
                isSheetOpen = false
                previewImage = null
            })
    }

}

@Composable
fun ScannedImageView(scannedImage: Bitmap?){

    val takenImageAlphaAnim by animateFloatAsState(
        targetValue = if (scannedImage != null) 1.0f else 0f,
        animationSpec = tween(500, easing = EaseInOutQuad)
    )

    val takenImageScaleAnim by animateFloatAsState(
        targetValue = if (scannedImage != null) 1.0f else 0.75f,
        animationSpec = tween(250, easing = EaseOutQuad)
    )

    if(scannedImage != null) {
        Image(
            bitmap = scannedImage?.asImageBitmap()!!,
            contentDescription = "image",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = takenImageAlphaAnim
                    scaleX = takenImageScaleAnim
                    scaleY = takenImageScaleAnim
                },
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun CameraControls(captureImage: () -> Unit, loadImage: () -> Unit, toggleFlash: () -> Unit) {
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp)){

        val cameraButton = createRef()
        val flashButton = createRef()
        val loadImageButton = createRef()

            FloatingActionButton(
                modifier = Modifier
                    .size(80.dp)
                    .constrainAs(cameraButton) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    },
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(4.dp),
                containerColor = Color.Red,
                contentColor = Color.White,
                onClick = captureImage
            ) {
                Icon(
                    painterResource(R.drawable.rounded_filter_center_focus_24),
                    null,
                    modifier = Modifier.size(35.dp)
                )
            }

            val flashOn = remember {
                mutableStateOf(false)
            }

            FloatingActionButton(
                modifier = Modifier
                    .size(60.dp)
                    .constrainAs(flashButton) {
                        start.linkTo(cameraButton.end)
                        end.linkTo(parent.end)
                        top.linkTo(cameraButton.top)
                        bottom.linkTo(cameraButton.bottom)
                    },
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(4.dp),
                containerColor = Color.White,
                contentColor = Color.Red,
                onClick = {
                    flashOn.value = !flashOn.value
                    toggleFlash()
                }) {

                var flashIcon = R.drawable.outline_flash_on_24
                if (flashOn.value) flashIcon = R.drawable.baseline_flash_on_24

                Icon(painterResource(flashIcon), null, modifier = Modifier.size(35.dp))
            }

        FloatingActionButton(
            modifier = Modifier
                .size(60.dp)
                .constrainAs(loadImageButton) {
                    start.linkTo(parent.start)
                    end.linkTo(cameraButton.start)
                    top.linkTo(cameraButton.top)
                    bottom.linkTo(cameraButton.bottom)
                },
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(4.dp),
            containerColor = Color.White,
            contentColor = Color.Red,
            onClick = loadImage) {
            Icon(painterResource(R.drawable.outline_image_24), null, modifier = Modifier.size(35.dp))
        }

    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun Preview() {
    OCRTheme {
        MainActivityUi()
    }
}
