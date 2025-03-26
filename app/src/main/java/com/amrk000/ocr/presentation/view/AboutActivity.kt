package com.amrk000.ocr.presentation.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.amrk000.ocr.R
import com.amrk000.ocr.presentation.view.ui.theme.OCRTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OCRTheme {
              AboutActivityUi()
            }
        }
    }
}

@Composable
fun AboutActivityUi() {
    val context = LocalContext.current
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
       Box(modifier = Modifier
           .padding(innerPadding)
           .fillMaxSize()){

           IconButton(onClick = {
               onBackPressedDispatcher?.onBackPressed()
           }, Modifier.align(Alignment.TopEnd)) {
               Icon(painter = painterResource(id = R.drawable.baseline_close_24), tint = MaterialTheme.colorScheme.primary, contentDescription = null)
           }

           Column(modifier = Modifier
               .align(Alignment.Center)
               .padding(bottom = 200.dp)){
               Image(painter = painterResource(R.drawable.logo),
                   modifier = Modifier
                       .padding(5.dp)
                       .size(85.dp)
                       .background(
                           MaterialTheme.colorScheme.surfaceDim,
                           shape = RoundedCornerShape(10.dp)
                       ),
                   contentScale = ContentScale.Crop,
                   contentDescription = null)

               Text(text =  LocalContext.current.applicationInfo.loadLabel(
                   LocalContext.current.packageManager).toString(),
                   modifier = Modifier
                       .padding(top = 10.dp)
                       .align(Alignment.CenterHorizontally),
                   fontSize = 26.sp,
                   color =  MaterialTheme.colorScheme.primary
               )

               Text(text = "Version " + LocalContext.current.packageManager.getPackageInfo(
                   LocalContext.current.packageName, 0
               ).versionName,
                   modifier = Modifier.align(Alignment.CenterHorizontally),
                   fontSize = 16.sp, fontWeight = FontWeight.Light)
           }

           var aboutCardVisible by remember {
               mutableStateOf(false)
           }
           LaunchedEffect(key1 = Unit, block = {
               delay(200L)
               aboutCardVisible = true
           })

           AnimatedVisibility(visible = aboutCardVisible, Modifier.align(Alignment.BottomCenter),
               enter = fadeIn(tween(
                   durationMillis = 700,
                   delayMillis = 600,
                   easing = EaseOutQuad
               ), initialAlpha = 0.0f) + slideInVertically(
                   tween(
                       durationMillis = 1200,
                       delayMillis = 600,
                       easing = EaseOutQuad
                   ),
                   initialOffsetY = { it * 2 }
               )) {

               Box(
                   modifier = Modifier
                       .align(Alignment.BottomCenter)
                       .fillMaxWidth()
                       .fillMaxHeight(0.35f)
                       .background(Color(39, 39, 39, 255))
               ) {
                   Column(
                       modifier = Modifier
                           .padding(15.dp)
                           .fillMaxWidth()
                           .wrapContentHeight(),
                       horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.spacedBy(10.dp)
                   ) {

                       Text(
                           text = "Made By",
                           fontSize = 20.sp,
                           fontWeight = FontWeight.Light,
                           color = Color(255, 255, 255, 178)
                       )

                       Image(
                           painter = painterResource(id = R.drawable.amrk000),
                           contentDescription = null,
                           Modifier
                               .fillMaxWidth(0.45f)
                               .wrapContentHeight()
                               .padding(bottom = 12.dp),
                           contentScale = ContentScale.Fit
                       )

                       Button(
                           onClick = {
                               val googlePlayLink =
                                   Uri.parse("https://play.google.com/store/apps/dev?id=5289896800613171020")
                               val intent = Intent(Intent.ACTION_VIEW, googlePlayLink)
                               startActivity(context, intent, null)
                           }, colors = ButtonDefaults.buttonColors(
                               Color(26, 139, 0, 255)
                           ), modifier = Modifier.fillMaxWidth(0.95f)
                       ) {
                           Icon(
                               painter = painterResource(id = R.drawable.googleplay_icon),
                               contentDescription = null
                           )
                           Text(
                               text = "Google Play Apps",
                               fontSize = 22.sp,
                               color = Color.White,
                               modifier = Modifier.padding(8.dp)
                           )
                       }

                       Button(
                           onClick = {
                               val githubRepoLink = Uri.parse("https://www.github.com")
                               val intent = Intent(Intent.ACTION_VIEW, githubRepoLink)
                               startActivity(context, intent, null)
                           }, colors = ButtonDefaults.buttonColors(
                               Color(24, 24, 24, 255)
                           ), modifier = Modifier.fillMaxWidth(0.95f)
                       ) {
                           Icon(
                               painter = painterResource(id = R.drawable.github_icon),
                               contentDescription = null
                           )
                           Text(
                               text = "Github Repo",
                               fontSize = 22.sp,
                               color = Color.White,
                               modifier = Modifier.padding(8.dp)
                           )
                       }
                   }
               }
           }
       }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OCRTheme {
        AboutActivityUi()
    }
}