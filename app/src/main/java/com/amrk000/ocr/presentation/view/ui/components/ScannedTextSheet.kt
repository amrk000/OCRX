package com.amrk000.ocr.presentation.view.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.amrk000.ocr.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannedTextSheet(modalSheetState: SheetState, scanResult: String, onDismiss: () -> Unit, onShare: () -> Unit, onCopy: () -> Unit, onClose: () -> Unit){
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(),
            windowInsets = WindowInsets(0,0,0,0),
            sheetState = modalSheetState,
            onDismissRequest = onDismiss
        ) {
            Box(modifier = Modifier.fillMaxSize()){
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 60.dp)
                ) {
                    SelectionContainer {
                        Text(
                            scanResult,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                Row (modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .wrapContentHeight()) {
                    Button(modifier = Modifier.weight(1f), shape = CutCornerShape(0.dp), colors = ButtonDefaults.buttonColors(
                        Color(0,0,0,210), Color.White),onClick = onShare) {
                        Icon(painter =  painterResource(R.drawable.baseline_share_24), null, Modifier.padding(15.dp))
                    }

                    Button(modifier = Modifier.weight(1f), shape = CutCornerShape(0.dp), colors = ButtonDefaults.buttonColors(
                        Color(0,0,0,210), Color.White),onClick = onCopy) {
                        Icon(painter =  painterResource(R.drawable.baseline_content_copy_24), null, Modifier.padding(15.dp))
                    }

                    Button(modifier = Modifier.weight(1f), shape = CutCornerShape(0.dp), colors = ButtonDefaults.buttonColors(
                        Color(0,0,0,210), Color.Red),onClick = onClose) {
                        Icon(painter =  painterResource(R.drawable.baseline_close_24), null, Modifier.padding(15.dp))
                    }

                }

            }
        }
}