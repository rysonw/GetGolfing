package com.rysonw.getgolfing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rysonw.getgolfing.ui.theme.Beige
import com.rysonw.getgolfing.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Fade background from white → beige
                var started by remember { mutableStateOf(false) }
                val bg by androidx.compose.animation.animateColorAsState(
                    targetValue = if (started) Beige else Color.White,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "bgFade"
                )

                LaunchedEffect(Unit) { started = true }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = bg
                ) {
                    InitialScreen()
                }
            }
        }
    }
}

@Composable
fun InitialScreen() {
    // Phase 1: idle (centered). After 1s → Phase 2: animate text up + show button
    var reveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000) // sit centered for 1s
        reveal = true
    }

    // One transition controls both text offset and button appearance
    val transition = updateTransition(targetState = reveal, label = "reveal")

    val textOffset by transition.animateDp(
        transitionSpec = { tween(durationMillis = 1500, easing = FastOutSlowInEasing) },
        label = "textOffset"
    ) { shown -> if (shown) (-20).dp else 80.dp }

    // Slightly delay the button so it appears just after the text starts moving
    val btnAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 1500, delayMillis = 150, easing = FastOutSlowInEasing) },
        label = "btnAlpha"
    ) { shown -> if (shown) 1f else 0f }

    val btnScale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 1500, delayMillis = 150, easing = FastOutSlowInEasing) },
        label = "btnScale"
    ) { shown -> if (shown) 1f else 0.96f }

    val playfair = FontFamily(Font(resId = R.font.playfair_display_regular))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Should I Golf Today?",
            fontFamily = playfair,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Color(0xFF3B3B3B),
            modifier = Modifier.offset(y = textOffset)
        )

        Spacer(Modifier.height(40.dp))

        // Reserve space so layout height never changes → no “jump”
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B3B3B),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        alpha = btnAlpha
                        scaleX = btnScale
                        scaleY = btnScale
                    }
            ) {
                Text(
                    text = "Check Conditions",
                    fontSize = 18.sp,
                    fontFamily = playfair,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
