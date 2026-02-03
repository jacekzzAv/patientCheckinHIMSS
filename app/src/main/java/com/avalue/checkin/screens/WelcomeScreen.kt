package com.avalue.checkin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avalue.checkin.R
import com.avalue.checkin.components.PrimaryButton
import com.avalue.checkin.ui.theme.Primary
import com.avalue.checkin.ui.theme.PrimaryDark
import com.avalue.checkin.ui.theme.PrimaryLight
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WelcomeScreen(
    onStartCheckIn: () -> Unit
) {
    var currentDate by remember { mutableStateOf("") }
    var currentTime by remember { mutableStateOf("") }
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentDate = dateFormatter.format(now)
            currentTime = timeFormatter.format(now)
            delay(60_000)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Primary,
                        PrimaryDark
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onStartCheckIn()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section - Date/Time
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp)
            ) {
                Text(
                    text = currentDate,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
            }
            
            // Center section - Main content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo placeholder - replace with actual logo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.large
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.logo_placeholder),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Text(
                    text = stringResource(R.string.welcome_title),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(R.string.welcome_subtitle),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
            
            // Bottom section - CTA
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 32.dp)
                        .background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.large
                        )
                        .clickable { onStartCheckIn() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.touch_to_checkin),
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Pulsing hint
                Text(
                    text = stringResource(R.string.welcome_tap_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
