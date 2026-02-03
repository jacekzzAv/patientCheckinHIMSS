package com.avalue.checkin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avalue.checkin.R
import com.avalue.checkin.components.SecondaryButton
import com.avalue.checkin.ui.theme.Success
import com.avalue.checkin.ui.theme.SuccessLight
import com.avalue.checkin.viewmodel.CheckInViewModel
import kotlinx.coroutines.delay

@Composable
fun ConfirmationScreen(
    viewModel: CheckInViewModel,
    onDone: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val patientInfo = state.patientInfo
    
    // Auto-return to welcome after 10 seconds
    LaunchedEffect(Unit) {
        delay(10_000)
        onDone()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Success,
                        SuccessLight
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Large checkmark
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 80.sp,
                    color = Success
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Success message
            Text(
                text = stringResource(R.string.confirmation_title),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Patient name
            Text(
                text = patientInfo.name,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Instruction
            Text(
                text = stringResource(R.string.confirmation_message),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Appointment info
            Text(
                text = patientInfo.appointment,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Manual done button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 64.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.large
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.done_button),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Auto-return notice
            Text(
                text = "This screen will automatically return to the start",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        
        // Tap anywhere to return
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            androidx.compose.material3.TextButton(onClick = onDone) {
                Text(
                    text = "Tap to start new check-in",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
