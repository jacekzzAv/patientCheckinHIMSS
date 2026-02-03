package com.avalue.checkin.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PatientInfo(
    val name: String = "John Smith",
    val dateOfBirth: String = "January 15, 1985",
    val appointment: String = "Dr. Williams - 2:30 PM"
)

data class CheckInState(
    val patientInfo: PatientInfo = PatientInfo(),
    val facePhoto: Bitmap? = null,
    val insuranceFront: Bitmap? = null,
    val insuranceBack: Bitmap? = null,
    val signature: Bitmap? = null,
    val isComplete: Boolean = false
)

class CheckInViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(CheckInState())
    val state: StateFlow<CheckInState> = _state.asStateFlow()
    
    fun setFacePhoto(bitmap: Bitmap) {
        _state.value = _state.value.copy(facePhoto = bitmap)
    }
    
    fun setInsuranceFront(bitmap: Bitmap) {
        _state.value = _state.value.copy(insuranceFront = bitmap)
    }
    
    fun setInsuranceBack(bitmap: Bitmap) {
        _state.value = _state.value.copy(insuranceBack = bitmap)
    }
    
    fun setSignature(bitmap: Bitmap) {
        _state.value = _state.value.copy(
            signature = bitmap,
            isComplete = true
        )
    }
    
    fun resetSession() {
        // Recycle bitmaps to free memory
        _state.value.facePhoto?.recycle()
        _state.value.insuranceFront?.recycle()
        _state.value.insuranceBack?.recycle()
        _state.value.signature?.recycle()
        
        _state.value = CheckInState()
    }
    
    override fun onCleared() {
        super.onCleared()
        resetSession()
    }
}
