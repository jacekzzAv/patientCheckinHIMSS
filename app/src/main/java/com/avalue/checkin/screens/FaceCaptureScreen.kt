package com.avalue.checkin.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.avalue.checkin.components.FaceOvalOverlay
import com.avalue.checkin.components.PrimaryButton
import com.avalue.checkin.components.capturePhoto
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceCaptureScreen(
    onPhotoCapture: (Bitmap) -> Unit,
    onBack: () -> Unit
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
                text = stringResource(R.string.face_capture_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        if (cameraPermissionState.status.isGranted) {
            // Camera preview section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    useFrontCamera = true,
                    imageCapture = imageCapture,
                    onError = { errorMessage = it }
                )
                
                FaceOvalOverlay(
                    modifier = Modifier.fillMaxSize()
                )
                
                // Instruction text overlay
                Text(
                    text = stringResource(R.string.face_capture_instruction),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp)
                        .padding(horizontal = 32.dp)
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
                                    onPhotoCapture(bitmap)
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
            // Permission not granted
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
