package com.avalue.checkin.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

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
                try {
                    val bitmap = image.toBitmapSafe()

                    if (bitmap == null) {
                        image.close()
                        onError("Failed to decode captured image")
                        return
                    }

                    // Mirror the image if using front camera
                    val finalBitmap = if (useFrontCamera) {
                        val matrix = Matrix().apply { preScale(-1f, 1f) }
                        val mirrored = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                        // Recycle original bitmap to prevent memory leak (only if a new bitmap was created)
                        if (mirrored != bitmap) {
                            bitmap.recycle()
                        }
                        mirrored
                    } else {
                        bitmap
                    }

                    image.close()
                    onCaptured(finalBitmap)
                } catch (e: Exception) {
                    Log.e("CameraCapture", "Failed to process captured image", e)
                    image.close()
                    onError("Failed to process photo: ${e.message}")
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraCapture", "Photo capture failed", exception)
                onError("Failed to capture photo: ${exception.message}")
            }
        }
    )
}

/**
 * Safely converts an ImageProxy to a Bitmap, handling both JPEG and YUV formats.
 * Returns null if conversion fails instead of throwing an exception.
 */
private fun ImageProxy.toBitmapSafe(): Bitmap? {
    return try {
        when (format) {
            ImageFormat.JPEG -> {
                // For JPEG format, the first plane contains the complete JPEG data
                val buffer = planes[0].buffer
                buffer.rewind()
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
            ImageFormat.YUV_420_888 -> {
                // For YUV format, we need to convert through YuvImage
                yuvToRgbBitmap(this)
            }
            else -> {
                // Try JPEG decoding as fallback (ImageCapture defaults to JPEG)
                val buffer = planes[0].buffer
                buffer.rewind()
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        }
    } catch (e: Exception) {
        Log.e("CameraCapture", "Failed to convert ImageProxy to Bitmap", e)
        null
    }
}

/**
 * Converts a YUV_420_888 ImageProxy to an RGB Bitmap.
 */
private fun yuvToRgbBitmap(image: ImageProxy): Bitmap? {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // Copy Y plane
    yBuffer.get(nv21, 0, ySize)
    // Copy VU interleaved (NV21 format)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 90, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
