package com.avalue.checkin.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.avalue.checkin.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PatientInfo(
    val name: String,
    val dateOfBirth: String,
    val appointment: String
)

data class CheckInState(
    val patientInfo: PatientInfo,
    val facePhoto: Bitmap? = null,
    val insuranceFront: Bitmap? = null,
    val insuranceBack: Bitmap? = null,
    val signature: Bitmap? = null,
    val isComplete: Boolean = false
)

class CheckInViewModel(application: Application) : AndroidViewModel(application) {

    private fun getDefaultPatientInfo(): PatientInfo {
        val context = getApplication<Application>()
        return PatientInfo(
            name = context.getString(R.string.demo_patient_name),
            dateOfBirth = context.getString(R.string.demo_patient_dob),
            appointment = context.getString(R.string.demo_appointment)
        )
    }

    private val _state = MutableStateFlow(CheckInState(patientInfo = getDefaultPatientInfo()))
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

        _state.value = CheckInState(patientInfo = getDefaultPatientInfo())
    }
    
    override fun onCleared() {
        super.onCleared()
        resetSession()
    }
}
