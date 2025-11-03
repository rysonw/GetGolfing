package com.rysonw.getgolfing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Fade background from white → beige
                var started by remember { mutableStateOf(false) }
                val bg by animateColorAsState(
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
    var reveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1000) // sit centered for 1s
        reveal = true
    }

    val transition = updateTransition(targetState = reveal, label = "reveal")

    val textOffset by transition.animateDp(
        transitionSpec = { tween(durationMillis = 1500, easing = FastOutSlowInEasing) },
        label = "textOffset"
    ) { shown -> if (shown) (-20).dp else 80.dp }

    val btnAlpha by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 1500,
                delayMillis = 150,
                easing = FastOutSlowInEasing
            )
        },
        label = "btnAlpha"
    ) { shown -> if (shown) 1f else 0f }

    val btnScale by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 1500,
                delayMillis = 150,
                easing = FastOutSlowInEasing
            )
        },
        label = "btnScale"
    ) { shown -> if (shown) 1f else 0.96f }

    val playfair = FontFamily(Font(resId = R.font.playfair_display_regular))

    // --- 2) State for API results / loading ---
    var isLoading by remember { mutableStateOf(false) }
    var result1 by remember { mutableStateOf<String?>(null) }
    var result2 by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Coroutine scope tied to this Composable
    val scope = rememberCoroutineScope()

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

                Spacer(Modifier.height(24.dp))

                when {
                    error != null -> Text("Error: $error", color = Color(0xFFB00020))
                    result1 != null || result2 != null -> {
                        Text("API #1: ${result1 ?: "-"}")
                        Text("API #2: ${result2 ?: "-"}")
                    }
                }
            }
        }
    }
}

suspend fun callWeatherAPI(): String {
    val targetURL = "${Constants.BASE_WEATHER_URL}"
    val client = OkHttpClient()
    val weatherRequest = Request.Builder().url(targetURL).build()

    return withContext(Dispatchers.IO) {
        client.newCall(weatherRequest).execute().use { res ->
            if (!res.isSuccessful) return@withContext "Error: ${res.code}"

            val body = res.body?.string() ?: return@withContext "Empty response"
            val json = JSONObject(body)

            val current = json.getJSONObject("current")

            val temp = current.optDouble("temperature_2m", Double.NaN)
            val rain = current.optDouble("rain", 0.0)
            val wind = current.optDouble("wind_speed_10m", 0.0)
            val cloud = current.optDouble("cloudcover", 0.0)

            return@withContext when {
                rain > 0.5 -> "Not good to golf – it's raining."
                wind > 20 -> "Windy day – expect tough conditions."
                cloud > 80 -> "Overcast but playable."
                temp < 8 -> "Too cold to golf comfortably."
                else -> "Good to golf today!"
            }
        }
    }
}

suspend fun callGolfAPI(): String {
    delay(300)
    return "Course=Open"
}