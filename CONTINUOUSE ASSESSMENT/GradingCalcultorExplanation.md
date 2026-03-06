# GradingCalcultorExplanation

This document explains how to simulate the Android Studio emulator from the terminal (PowerShell) and run the `Student Grade Calculator` app.

## 1) Prepare terminal session
Set the SDK root and add `emulator` and `platform-tools` to PATH for the current PowerShell session:

```powershell
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
$env:PATH += ";$env:ANDROID_SDK_ROOT\emulator;$env:ANDROID_SDK_ROOT\platform-tools"
```

If your SDK lives in a different folder, change the path accordingly.

## 2) List available AVDs
```powershell
emulator -list-avds
```
This prints AVD names (e.g., `Pixel_3_API_30`, `Android25`).

## 3) Start an emulator from terminal
Foreground (useful for logs):
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd <AVD_NAME>
```
Start in background (PowerShell):
```powershell
Start-Process -FilePath "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -ArgumentList '-avd','<AVD_NAME>','-no-snapshot-load' -NoNewWindow
```
Recommended flags you may add:
- `-no-snapshot-load` (avoid loading snapshots)
- `-netdelay none -netspeed full` (network settings)
- `-gpu swiftshader_indirect` (software renderer if GPU problems)

## 4) Wait for emulator to be fully available to ADB
Start adb server and check devices:
```powershell
adb start-server
adb devices
```
Poll until emulator state becomes `device` (example script):
```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
for ($i=0; $i -lt 60; $i++) {
  $out = & $adb devices
  if ($out -match 'emulator-\d+\s+device') { Write-Host 'emulator ready'; break }
  Start-Sleep -Seconds 2
}
```

## 5) Install the app onto the emulator
From your project root you can install directly with Gradle (build + install):
```powershell
.\gradlew.bat installDebug
```
Or build then install with adb:
```powershell
.\gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 6) Launch the app from terminal
Use the package name and main activity to start:
```powershell
adb shell am start -n com.example.gradeapp/.MainActivity
```
If you don't know the package or want a quick launch:
```powershell
adb shell monkey -p com.example.gradeapp -c android.intent.category.LAUNCHER 1
```

## 7) View logs (optional, for debugging)
Run logcat and filter by package name:
```powershell
adb logcat | Select-String "com.example.gradeapp"
```
Or use Android Studio's Logcat tool for a richer view.

## 8) Quick troubleshooting
- `emulator` not found: ensure `$env:ANDROID_SDK_ROOT` is correct and that `emulator` folder exists.
- ADB authorization prompt on device/emulator: accept the PC prompt on the device/emulator UI.
- `ERROR: Unable to connect to adb daemon on port: 5037`: run `adb kill-server` then `adb start-server`.
- GPU/OpenGL errors: pass `-gpu swiftshader_indirect` or use an x86 system image with hardware acceleration enabled.

## 9) Useful commands summary
```powershell
# Prepare session
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
$env:PATH += ";$env:ANDROID_SDK_ROOT\emulator;$env:ANDROID_SDK_ROOT\platform-tools"

# List AVDs
emulator -list-avds

# Start emulator (replace NAME)
Start-Process -FilePath "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -ArgumentList '-avd','NAME','-no-snapshot-load' -NoNewWindow

# Wait & check
adb start-server
adb devices

# Install app
.\gradlew.bat installDebug
# or
.\gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Launch app
adb shell am start -n com.example.gradeapp/.MainActivity

# View logs
adb logcat
```

---
File created at project root: `GradingCalcultorExplanation.md`.

If you want, I can run `adb shell am start -n com.example.gradeapp/.MainActivity` now to open the app on the running emulator. Tell me `yes` to launch it now.