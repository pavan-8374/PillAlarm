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

![Screenshot_2025-12-21-21-45-37-49_b6681e21b68ac5e5728833553a5bf718](https://github.com/user-attachments/assets/d5e7c652-7079-42a8-ad75-0cfbf649ce68)
![Screenshot_2025-12-21-21-45-32-98_b6681e21b68ac5e5728833553a5bf718](https://github.com/user-attachments/assets/3cdc343b-9238-48ee-a7ab-3f60560566c2)
![Screenshot_2025-12-21-21-45-27-21_b6681e21b68ac5e5728833553a5bf718](https://github.com/user-attachments/assets/503e4750-11a7-46b8-938b-1a28bff4690d)
![Screenshot_2025-12-21-21-45-16-73_b6681e21b68ac5e5728833553a5bf718](https://github.com/user-attachments/assets/f47e08ec-345d-4f47-864f-8884eba4da98)
![App permissions](https://github.com/user-attachments/assets/cb750d11-0996-406a-ba6c-009b9151c16f)
<img width="164" height="346" alt="medicine cards" src="https://github.com/user-attachments/assets/698e3d9f-242c-4cf5-980d-72c6c607349c" />
![1000124650](https://github.com/user-attachments/assets/92f16ec8-b1a6-46da-b573-15942d9148b1)
<img width="158" height="350" alt="Schedule alarm" src="https://github.com/user-attachments/assets/e5a2e9b2-cbab-4f31-b54e-6ba6dc67422b" />
<img width="161" height="348" alt="set_alarm" src="https://github.com/user-attachments/assets/1afe497d-3659-4840-abff-03e292fd09ea" />




# Developed by: Rallapalli Pavan
## Project Submitted for: Mobile App Development (CIS4034)
