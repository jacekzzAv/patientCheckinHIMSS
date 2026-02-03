package com.avalue.checkin.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    useFrontCamera: Boolean = true,
    imageCapture: ImageCapture,
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    DisposableEffect(useFrontCamera) {
        val cameraProvider = cameraProviderFuture.get()
        
        val preview = Preview.Builder()
            .build()
            .also {
                it.surfaceProvider = previewView.surfaceProvider
            }
        
        val cameraSelector = if (useFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraPreview", "Camera binding failed", e)
            onError("Failed to start camera: ${e.message}")
        }
        
        onDispose {
            cameraProvider.unbindAll()
        }
    }
    
    Box(modifier = modifier) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    useFrontCamera: Boolean,
    onCaptured: (Bitmap) -> Unit,
    onError: (String) -> Unit
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = image.toBitmap()
                
                // Mirror the image if using front camera
                val finalBitmap = if (useFrontCamera) {
                    val matrix = Matrix().apply { preScale(-1f, 1f) }
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }
                
                image.close()
                onCaptured(finalBitmap)
            }
            
            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraCapture", "Photo capture failed", exception)
                onError("Failed to capture photo: ${exception.message}")
            }
        }
    )
}

private fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: throw IllegalStateException("Could not decode image")
}
