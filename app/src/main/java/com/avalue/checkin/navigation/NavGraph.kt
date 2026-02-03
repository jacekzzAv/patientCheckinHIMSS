package com.avalue.checkin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.avalue.checkin.screens.*
import com.avalue.checkin.viewmodel.CheckInViewModel

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object FaceCapture : Screen("face_capture")
    object PatientVerify : Screen("patient_verify")
    object InsuranceFront : Screen("insurance_front")
    object InsuranceBack : Screen("insurance_back")
    object InsuranceReview : Screen("insurance_review")
    object Consent : Screen("consent")
    object Confirmation : Screen("confirmation")
}

@Composable
fun CheckInNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: CheckInViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onStartCheckIn = {
                    viewModel.resetSession()
                    navController.navigate(Screen.FaceCapture.route)
                }
            )
        }
        
        composable(Screen.FaceCapture.route) {
            FaceCaptureScreen(
                onPhotoCapture = { bitmap ->
                    viewModel.setFacePhoto(bitmap)
                    navController.navigate(Screen.PatientVerify.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.PatientVerify.route) {
            PatientVerifyScreen(
                viewModel = viewModel,
                onConfirm = {
                    navController.navigate(Screen.InsuranceFront.route)
                },
                onStartOver = {
                    viewModel.resetSession()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.InsuranceFront.route) {
            InsuranceCardScreen(
                isFront = true,
                onCapture = { bitmap ->
                    viewModel.setInsuranceFront(bitmap)
                    navController.navigate(Screen.InsuranceBack.route)
                },
                onBack = {
                    navController.popBackStack()
                },
                onSkip = {
                    navController.navigate(Screen.Consent.route)
                }
            )
        }
        
        composable(Screen.InsuranceBack.route) {
            InsuranceCardScreen(
                isFront = false,
                onCapture = { bitmap ->
                    viewModel.setInsuranceBack(bitmap)
                    navController.navigate(Screen.InsuranceReview.route)
                },
                onBack = {
                    navController.popBackStack()
                },
                onSkip = null // Can't skip back after capturing front
            )
        }
        
        composable(Screen.InsuranceReview.route) {
            InsuranceReviewScreen(
                viewModel = viewModel,
                onConfirm = {
                    navController.navigate(Screen.Consent.route)
                },
                onRetake = {
                    navController.navigate(Screen.InsuranceFront.route) {
                        popUpTo(Screen.InsuranceFront.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Consent.route) {
            ConsentScreen(
                onAgree = { signatureBitmap ->
                    viewModel.setSignature(signatureBitmap)
                    navController.navigate(Screen.Confirmation.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Confirmation.route) {
            ConfirmationScreen(
                viewModel = viewModel,
                onDone = {
                    viewModel.resetSession()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
