# eParking: Smart Parking Finder and Booking App

An Android application for finding, listing and booking parking spaces. Drivers see nearby parking places on a map and pay online. Parking owners list their spaces with photos and track activity live.

Undergraduate project, B.E. Computer Engineering, Gujarat Technological University (2019).

## Features

**For drivers**
- 🔐 Sign up and log in (Firebase Authentication)
- 🗺️ View parking places on Google Maps based on the current location
- 📋 Browse a list of available parking places
- 💳 Pay for parking online with Razorpay
- 📍 Live location tracking with a background service

**For parking owners**
- ➕ Add a new parking place with location and photos (camera or gallery)
- 🖼️ Photos stored in Firebase Storage
- 📊 Owner dashboard (navigation drawer) to manage listings

## Screenshots

| Login | Map | Parking list | Payment |
|---|---|---|---|
| ![](screenshots/login.png) | ![](screenshots/map.png) | ![](screenshots/list.png) | ![](screenshots/payment.png) |

## Tech stack

| Area | Tools |
|---|---|
| Language / IDE | Java, Android Studio, Gradle |
| Backend | Firebase Authentication, Realtime Database, Storage, FirebaseUI |
| Maps and location | Google Maps SDK, Google Play Services Location |
| Payments | Razorpay Checkout |
| UI | AndroidX, Material Components, RecyclerView, CardView, Glide, LovelyDialog, MaterialEditText |
| Target | minSdk 19 (Android 4.4), targetSdk 28 (Android 9) |

## App structure

```
app/src/main/java/com/example/e_parking/
├── activities/
│   ├── SplashActivity          # launcher screen
│   ├── InitialActivity         # choose user type
│   ├── LoginActivity           # Firebase authentication
│   ├── MapsActivity            # parking places on Google Maps
│   ├── ParkingListActivity     # list of parking places
│   ├── NewParkingPlaceActivity # owners add a parking place
│   ├── GalleryActivity         # parking place photos
│   └── RazorpayActivity        # online payment
├── livetracking/
│   ├── LiveTrackingActivity
│   ├── DisplayMapsActivity
│   └── TrackerService          # background location updates
├── OwnerDrawerActivity         # owner dashboard
└── other/MyApplication
```

## Setup

1. Clone the repository and open it in **Android Studio**:
   ```bash
   git clone https://github.com/arifex/eParking.git
   ```
2. **Firebase:** create a Firebase project, add an Android app with package name `com.example.e_parking`, download `google-services.json` and place it in `app/`. Enable Email/Password Authentication, Realtime Database and Storage.
3. **Google Maps:** create a Maps SDK for Android API key in Google Cloud Console.
4. **Razorpay:** create a test API key in the Razorpay dashboard.
5. Add both keys to your local `gradle.properties` (not committed):
   ```
   MAPS_API_KEY=your_google_maps_key
   RAZORPAY_KEY=your_razorpay_key
   ```
   Then reference them in `AndroidManifest.xml` through `manifestPlaceholders`.
6. Sync Gradle and run on an emulator or a device (Android 4.4+).

## Author

**Md. Arifuzzaman** — Assistant Professor, Department of CSE, Leading University, Sylhet, Bangladesh
[GitHub](https://github.com/arifex) · [Google Scholar](https://scholar.google.com/citations?hl=en&user=ngntKZ8AAAAJ)
