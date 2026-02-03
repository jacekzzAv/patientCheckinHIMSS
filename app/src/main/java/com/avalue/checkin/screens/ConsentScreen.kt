package com.avalue.checkin.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.avalue.checkin.R
import com.avalue.checkin.components.BackButton
import com.avalue.checkin.components.SignatureCanvas
import com.avalue.checkin.components.rememberSignatureState
import com.avalue.checkin.components.SuccessButton

@Composable
fun ConsentScreen(
    onAgree: (Bitmap) -> Unit,
    onBack: () -> Unit
) {
    val signatureState = rememberSignatureState()
    val hasSignature = signatureState.hasSignature
    var signatureSize by remember { mutableStateOf(IntSize.Zero) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                text = stringResource(R.string.consent_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Consent text (scrollable)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.consent_text),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Signature section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Please sign below:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Signature canvas with clear button
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SignatureCanvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .onSizeChanged { signatureSize = it },
                        strokeColor = Color.Black,
                        strokeWidth = 4.dp,
                        backgroundColor = Color.White,
                        signatureState = signatureState
                    )
                    
                    // Clear button overlay
                    TextButton(
                        onClick = {
                            signatureState.clear()
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.clear_signature),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                if (!hasSignature) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.please_sign),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Bottom button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp)
        ) {
            SuccessButton(
                text = stringResource(R.string.i_agree_and_sign),
                onClick = {
                    val targetWidth = signatureSize.width.takeIf { it > 0 } ?: 800
                    val targetHeight = signatureSize.height.takeIf { it > 0 } ?: 200
                    onAgree(signatureState.toBitmap(targetWidth, targetHeight))
                },
                enabled = hasSignature,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
