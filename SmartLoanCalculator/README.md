# Smart Loan Calculator

An offline-first Kotlin/Jetpack Compose Android app for EMI, interest, affordability, prepayment planning, amortization, calculation history, and local settings.

## Requirements

- Android Studio Ladybug or newer
- JDK 17
- Android SDK Platform 35

## Build

Open this folder in Android Studio and wait for Gradle sync. Then use **Run** for a debug build, or run:

```bash
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew bundleRelease
```

Release artifacts are written below `app/build/outputs/`. The default release is unsigned; configure an Android signing config before publishing.

## Architecture

- `domain`: validated financial formulas and amortization calculator
- `data`: Room history database, DataStore preferences, repository
- `ui`: Compose screens, Navigation Compose, StateFlow ViewModel
- `util`: CSV and PDF export helpers

The app requires no Internet permission. Data remains on-device.
