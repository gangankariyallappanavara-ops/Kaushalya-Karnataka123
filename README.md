# Project Planner Android App

Native Kotlin Android app generated from the provided project planning PDF.

## What It Includes

- Splash screen
- Login, sign up, and password recovery flow
- Dashboard with project quality metrics
- Screen-by-screen planning module
- Implementation checklist module
- Profile and settings module
- Offline-only sample data, so the app runs without a backend

## Build

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio2\jbr'
$env:ANDROID_HOME='C:\Users\GANGA\AppData\Local\Android\Sdk'
.\gradlew.bat assembleDebug --offline
```

The debug APK is created at:

```text
app/build/outputs/apk/debug/app-debug.apk
```
