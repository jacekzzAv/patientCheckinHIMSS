package com.avalue.checkin.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avalue.checkin.R
import com.avalue.checkin.components.BackButton
import com.avalue.checkin.components.CameraPreview
import com.avalue.checkin.components.CardOverlay
import com.avalue.checkin.components.PrimaryButton
import com.avalue.checkin.components.capturePhoto
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InsuranceCardScreen(
    isFront: Boolean,
    onCapture: (Bitmap) -> Unit,
    onBack: () -> Unit,
    onSkip: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    
    var isCapturing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val title = if (isFront) {
        stringResource(R.string.insurance_front_title)
    } else {
        stringResource(R.string.insurance_back_title)
    }
    
    val instruction = if (isFront) {
        stringResource(R.string.insurance_front_instruction)
    } else {
        stringResource(R.string.insurance_back_instruction)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            BackButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            
            // Skip button (only on front card screen)
            if (onSkip != null) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = stringResource(R.string.skip_insurance),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        if (cameraPermissionState.status.isGranted) {
            // Instruction text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(vertical = 16.dp, horizontal = 32.dp)
            ) {
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Camera preview section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Using front camera for the L-shaped kiosk form factor
                // The user will hold the card in front of them
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    useFrontCamera = true,
                    imageCapture = imageCapture,
                    onError = { errorMessage = it }
                )
                
                CardOverlay(
                    modifier = Modifier.fillMaxSize()
                )
                
                // Error message
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .padding(horizontal = 32.dp)
                    )
                }
            }
            
            // Step indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(vertical = 8.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                StepIndicator(
                    step = 1,
                    label = stringResource(R.string.step_front),
                    isActive = isFront,
                    isComplete = !isFront
                )
                Spacer(modifier = Modifier.width(32.dp))
                StepIndicator(
                    step = 2,
                    label = stringResource(R.string.step_back),
                    isActive = !isFront,
                    isComplete = false
                )
            }
            
            // Capture button section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(vertical = 32.dp)
            ) {
                PrimaryButton(
                    text = if (isCapturing) stringResource(R.string.capturing_text) else stringResource(R.string.capture_button),
                    onClick = {
                        if (!isCapturing) {
                            isCapturing = true
                            errorMessage = null
                            capturePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                useFrontCamera = true,
                                onCaptured = { bitmap ->
                                    isCapturing = false
                                    onCapture(bitmap)
                                },
                                onError = { error ->
                                    isCapturing = false
                                    errorMessage = error
                                }
                            )
                        }
                    },
                    enabled = !isCapturing,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            // Permission not granted - same as FaceCaptureScreen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.camera_permission_required),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PrimaryButton(
                    text = stringResource(R.string.grant_permission),
                    onClick = { cameraPermissionState.launchPermissionRequest() }
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(
    step: Int,
    label: String,
    isActive: Boolean,
    isComplete: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = when {
                        isComplete -> MaterialTheme.colorScheme.primary
                        isActive -> MaterialTheme.colorScheme.primary
                        else -> Color.Gray
                    },
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (isComplete) "✓" else step.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive || isComplete) Color.White else Color.Gray
        )
    }
}
