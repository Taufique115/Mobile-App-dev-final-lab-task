# UserSettingsApp — Lab Task 11

## Setup Instructions

1. Open **Android Studio** (Hedgehog or newer recommended)
2. Click **File → Open** and select the `UserSettingsApp` folder
3. Wait for Gradle sync to complete
4. In `local.properties`, set your SDK path:
   ```
   sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk   (Windows)
   sdk.dir=/Users/YourName/Library/Android/sdk                  (Mac)
   sdk.dir=/home/YourName/Android/Sdk                           (Linux)
   ```
   *(Android Studio usually sets this automatically on first open)*
5. Click **Run ▶** to build and install on emulator or device

## Features

- **Screen 1 (MainActivity):** Settings Dashboard with name, theme, notifications, language, font size
- **Screen 2 (SettingsViewerActivity):** Shows all saved settings with timestamp in CardView rows
- **Screen 3 (ProfileActivity):** Student profile with ID, name, department, year, email

## SharedPreferences

| File | Purpose |
|------|---------|
| `AppSettings` | Theme, notifications, language, font size |
| `ProfilePrefs` | Student name, ID, department, year, email |

All data persists across app restarts via `apply()`.
