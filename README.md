#  Visual Pill Alarm - Medication Reminder App

A robust Android application designed to help patients adhere to their medication schedules. The app allows users to capture images of their pills, set precise alarms for specific days of the week, and ensures reliable notifications even when the device is in sleep mode.

## Features

* **Visual Medication Management:** Capture and save photos of medication using the device camera for easy identification.
* **Flexible Scheduling:** Set alarms for specific days of the week (e.g., "Every Monday and Wednesday at 8:00 AM").
* **Reliable Alarms:** Uses `AlarmManager` with `setExactAndAllowWhileIdle` to cut through Android's Doze mode and ensure the alarm rings exactly on time.
* **Full-Screen Alerts:** Wakes up the device screen with a full-screen activity showing the medicine name and image, even if the phone is locked.
* **Hybrid Data Architecture:**
    * **Offline First:** Uses **Room Database** to save alarms locally, ensuring the app works without internet.
    * **Cloud Sync:** Syncs data to **Google Firestore** and uploads images to **Firebase Storage** when connectivity is available.

## Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Modern native UI)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Local Database:** Room (SQLite abstraction)
* **Cloud Backend:** Firebase Firestore & Firebase Storage
* **Asynchronous Programming:** Kotlin Coroutines & Flow
* **Image Loading:** Coil
* **Navigation:** Jetpack Navigation Compose

## Setup & Installation

1.  **Clone the Repository**
    
    git clone [https://github.com/pavan-8374/PillAlarm]
    
2.  **Open in Android Studio**
    * Open Android Studio and select "Open an existing Project".
    * Select the cloned folder.
3.  **Firebase Configuration**
    * Create a project in the [Firebase Console](https://console.firebase.google.com/).
    * Add an Android App with package name: `com.example.pillalarm`.
    * Download the `google-services.json` file.
    * Place the file in the `app/` directory of the project.
4.  **Build and Run**
    * Sync Gradle files.
    * Connect a physical device or start an Emulator.
    * Click **Run**.

##  Permissions Explained

This app requires specific permissions to function correctly as a medical reminder:

* `SCHEDULE_EXACT_ALARM`: **Critical.** Allows the app to schedule alarms at precise times.
* `USE_FULL_SCREEN_INTENT`: Allows the alarm activity to take over the screen when the phone is locked.
* `POST_NOTIFICATIONS`: Required for Android 13+ to show the alarm notification.
* `CAMERA`: To take photos of the medication.
* `INTERNET`: To sync data with Firebase.

##  Device Specific Settings (Troubleshooting)

**Note for Oppo, Realme, OnePlus, and Xiaomi users:**
Modern Android manufacturers use aggressive battery optimization that may kill background alarms. For the app to function perfectly on these devices:

1.  Open **Settings > Apps > Pill Alarm**.
2.  Enable **"Allow Background Activity"**.
3.  Enable **"Display over other apps"** (This is required for the alarm screen to pop up).
4.  Ensure **"Auto-start"** is enabled.

##  Screenshots

![2 app terms](https://github.com/user-attachments/assets/ad55b614-0ade-409e-b699-660fba4224ca)
![Screenshot_2025-12-21-21-45-27-21_b6681e21b68ac5e5728833553a5bf718](https://github.com/user-attachments/assets/7239c020-b4be-4c91-90f1-4af108115d77)

![paracetamol](https://github.com/user-attachments/assets/5964ab23-38e2-412e-b4fb-1a666d6ea0eb)

![Screenshot_2026-01-02-17-27-26-17_b6681e21b68ac5e5728833553a5bf718](https://github.com/user-attachments/assets/f0df3e84-457d-495c-84c5-c12f6d21339c)

![Screenshot_2026-01-02-17-27-17-14_b6681e21b68ac5e5728833553a5bf718](https://github.com/user-attachments/assets/5e1145e0-2097-4674-a2da-9138273f3ec5)


# Developed by: Rallapalli Pavan
## Project Submitted for: Mobile App Development
