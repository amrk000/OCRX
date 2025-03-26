package com.amrk000.ocr.presentation.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amrk000.ocr.R
import com.amrk000.ocr.data.model.HistoryScan
import com.amrk000.ocr.presentation.view.ui.components.ScannedTextSheet
import com.amrk000.ocr.presentation.view.ui.theme.OCRTheme
import com.amrk000.ocr.presentation.viewModel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OCRTheme {
                HistoryActivityUi()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryActivityUi() {
    val context =  LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: HistoryViewModel = viewModel()
    viewModel.loadHistory()

    val items by viewModel.getHistoryObserver().observeAsState()
    val scan by viewModel.getScanObserver().observeAsState(initial = "No Text Detected")

    var isSheetOpen by remember {
        mutableStateOf(false)
    }

    viewModel.getScanObserver().observe(lifecycleOwner) {
        isSheetOpen = true
    }

    val modalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)){
            Header()

            if(items!!.isNotEmpty()){
               HistoryList(items!!){ item ->
                   viewModel.setScan(item.text)
                   isSheetOpen = true
               }
            }else {
                Box(modifier = Modifier.fillMaxSize()){
                    Text(text = "No Scans History Yet!", modifier = Modifier.align(Alignment.Center))
                }
            }

            if(isSheetOpen) {
                ScannedTextSheet(modalSheetState, scan,
                    onDismiss = {
                        isSheetOpen = false
                    },
                    onShare = {
                        val share: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, scan)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(share, "Share Text"))
                    },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val textData = ClipData.newPlainText("OCR: Text", scan)
                        clipboard.setPrimaryClip(textData)
                    },
                    onClose = {
                        isSheetOpen = false
                    })
           }

        }
    }
}

@Composable
fun Header(){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 25.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)){
        Row(modifier= Modifier
            .align(Alignment.Center)
            .wrapContentWidth()) {
            Icon(painter = painterResource(id = R.drawable.baseline_history_24), contentDescription = "", modifier = Modifier.size(35.dp))
            Text(
                text = "History",
                modifier = Modifier.padding(start = 5.dp),
                style = TextStyle(fontSize = 28.sp)
            )
        }
    }
}

@Composable
fun HistoryList(items: List<HistoryScan>, onItemClick: (item: HistoryScan) -> Unit){
    LazyColumn(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
    items(items, key = {it.id}){ item ->

            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .requiredHeight(100.dp)
                .clickable(enabled = true,
                    onClick = { onItemClick(item) },
                    indication = rememberRipple(bounded = true, color = Color.Red, radius = 180.dp),
                    interactionSource = remember { MutableInteractionSource() }
                ),
                colors = CardDefaults.cardColors(Color.hsv(0f,0.0f,0.5f,0.1f)),
                ) {

                Box(modifier = Modifier
                    .fillMaxSize()){

                    Text(maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        text = item.text,
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp),
                    )

                    Box(modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .height(30.dp)){
                        Image(modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxWidth(0.55f)
                            .fillMaxHeight(),
                            painter = painterResource(id = R.drawable.date_back_design),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "")

                        Row(modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .wrapContentHeight()) {

                            Text(
                                style = TextStyle(color = Color.White),
                                text = item.date,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Icon(painter = painterResource(id = R.drawable.outline_calendar_today_24), contentDescription = "", modifier = Modifier
                                .size(25.dp)
                                .padding(end = 3.dp, start = 3.dp), tint = Color.White)
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryActivityPreview() {
    OCRTheme {
        HistoryActivityUi()
    }
}