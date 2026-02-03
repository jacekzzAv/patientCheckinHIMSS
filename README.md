# Patient Check-In Kiosk Demo

A demo Android application for HIMSS tradeshow showcasing a patient check-in workflow on Avalue panel PCs.

## Target Device
- **Device**: Avalue WT1539
- **SoC**: Rockchip RK3576
- **Display**: 1080x1920 (portrait)
- **OS**: Android 15 (API 35)
- **Cameras**: 2x 5MP (front + rear)

## Features

### Check-In Flow
1. **Welcome Screen** - Branded splash with touch-to-start
2. **Face Capture** - Live camera preview with oval face guide overlay
3. **Patient Verification** - Display captured photo with demo patient info
4. **Insurance Card Front** - Card capture with alignment template
5. **Insurance Card Back** - Card capture with alignment template
6. **Insurance Review** - Review both captured card images
7. **Consent & Signature** - Scrollable consent text with touch signature pad
8. **Confirmation** - Success screen with auto-return to start

### Kiosk Features
- Full-screen immersive mode
- System UI hidden
- Back button disabled
- Screen always on
- Device admin support for enterprise lockdown

## Building the Project

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35

### Setup
1. Clone/download the project
2. Open in Android Studio
3. Sync Gradle files
4. Connect the Avalue WT1539 device via USB (ADB enabled)
5. Build and run

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug

# Release build
./gradlew assembleRelease
```

## Project Structure

```
app/src/main/
├── java/com/avalue/checkin/
│   ├── MainActivity.kt          # Entry point with kiosk setup
│   ├── admin/
│   │   └── KioskDeviceAdminReceiver.kt
│   ├── components/
│   │   ├── Buttons.kt           # Primary, Secondary, Success buttons
│   │   ├── CameraPreview.kt     # CameraX preview composable
│   │   ├── CardOverlay.kt       # Insurance card alignment overlay
│   │   ├── FaceOvalOverlay.kt   # Face capture oval guide
│   │   └── SignatureCanvas.kt   # Touch signature pad
│   ├── navigation/
│   │   └── NavGraph.kt          # Compose Navigation setup
│   ├── screens/
│   │   ├── WelcomeScreen.kt
│   │   ├── FaceCaptureScreen.kt
│   │   ├── PatientVerifyScreen.kt
│   │   ├── InsuranceCardScreen.kt
│   │   ├── InsuranceReviewScreen.kt
│   │   ├── ConsentScreen.kt
│   │   └── ConfirmationScreen.kt
│   ├── ui/theme/
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/
│       └── CheckInViewModel.kt
└── res/
    ├── values/
    │   ├── strings.xml
    │   ├── colors.xml
    │   └── themes.xml
    └── xml/
        └── device_admin.xml
```

## Customization

### Branding
- Update colors in `res/values/colors.xml` and `ui/theme/Theme.kt`
- Replace logo placeholder in `WelcomeScreen.kt`
- Modify strings in `res/values/strings.xml`

### Demo Patient Data
Edit `CheckInViewModel.kt` to change the default patient info:
```kotlin
data class PatientInfo(
    val name: String = "John Smith",
    val dateOfBirth: String = "January 15, 1985",
    val appointment: String = "Dr. Williams - 2:30 PM"
)
```

### Camera Selection
The app uses the front camera by default (appropriate for L-shaped kiosk form factor). To use rear camera, modify `useFrontCamera` parameter in the screen files.

## Kiosk Mode Setup

### Basic Lockdown (included)
The app includes basic kiosk features:
- Immersive full-screen mode
- Back button disabled
- Screen kept on

### Enterprise Lockdown (requires additional setup)
For complete lockdown using Device Owner mode:

1. Factory reset the device
2. Set up via ADB before any account setup:
```bash
adb shell dpm set-device-owner com.avalue.checkin/.admin.KioskDeviceAdminReceiver
```

3. The app can then use Device Policy Manager to:
   - Lock to single app mode (pinning)
   - Disable status bar pulls
   - Disable notifications
   - Block other apps

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Jetpack Compose | BOM 2024.06 | UI framework |
| CameraX | 1.3.4 | Camera handling |
| Navigation Compose | 2.7.7 | Screen navigation |
| Accompanist Permissions | 0.34.0 | Runtime permissions |

## Notes for HIMSS Demo

- The app uses demo/mock data - no real patient info is stored
- Insurance card images are captured but not processed or transmitted
- Signature is captured but not stored persistently
- The confirmation screen auto-returns to welcome after 10 seconds
- All captured bitmaps are cleared when returning to the welcome screen

## Troubleshooting

### Camera not showing
- Ensure camera permission is granted
- Check that no other app is using the camera
- Verify camera hardware via `adb shell dumpsys media.camera`

### App exits kiosk mode
- The basic kiosk mode can still be exited via recent apps
- For production, use Device Owner mode for full lockdown

### Build errors
- Ensure JDK 17 is configured in Android Studio
- Sync Gradle files after any changes
- Clean and rebuild: `./gradlew clean assembleDebug`
