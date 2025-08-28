/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.witchayut.whereismycar.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.tooling.preview.devices.WearDevices
import com.witchayut.whereismycar.presentation.theme.WhereIsMyCarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("parking_prefs", Context.MODE_PRIVATE)
    }

    val floorOptions = remember { listOf("B3", "B2", "B1", "G", "2", "3", "4", "5") }

    // Load initial floor from SharedPreferences or default to the first option
    val initialLoadedFloor = remember {
        sharedPreferences.getString("saved_floor_key", floorOptions[0]) ?: floorOptions[0]
    }

    val pickerState = rememberPickerState(
        initialNumberOfOptions = floorOptions.size,
    )

    // State to hold the currently displayed floor text
    var displayedFloorText by remember { mutableStateOf(initialLoadedFloor) }

    // Auto-save when picker selection changes
    LaunchedEffect(pickerState.selectedOption) {
        val newSelectedFloor = floorOptions[pickerState.selectedOption]
        if (newSelectedFloor != displayedFloorText) {
            with(sharedPreferences.edit()) {
                putString("saved_floor_key", newSelectedFloor)
                apply()
            }
            displayedFloorText = newSelectedFloor
        }
    }

    // Set initial picker position to the loaded floor
    LaunchedEffect(initialLoadedFloor, floorOptions) {
        val initialIndex = floorOptions.indexOf(initialLoadedFloor)
        if (initialIndex != -1) {
            pickerState.scrollToOption(initialIndex)
        }
    }

    WhereIsMyCarTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Parked at",
                    style = MaterialTheme.typography.caption1,
                )

                Picker(
                    state = pickerState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    separation = 4.dp,
                ) { optionIndex ->
                    val isSelected = optionIndex == pickerState.selectedOption
                    val textStyle = if (isSelected) MaterialTheme.typography.display2 else MaterialTheme.typography.title1
                    val textColor = if (isSelected) Color(0xFF48CAE4) else MaterialTheme.colors.onSurface
                    Text(
                        text = floorOptions[optionIndex],
                        style = textStyle,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
