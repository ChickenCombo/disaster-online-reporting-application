# Disaster Online Reporting Application v4

**Disaster Online Reporting Application v4 (DORA v4)** is an Android-based mobile application that aims to enhance the features of the previous version of the application and add features that will enable the user to collaborate with other people. The purpose of DORA v4 is to provide disaster warnings, allow communication between users, and provide support assistance to the affected areas.

**Members:**
* Castro, Michaela Marie N.
* Cuadra, John Lester G.
* Maruno, Hitomi B.
* Quitor, Cedric John D.

## Getting Started
### Project Setup
Clone the repository:

    gh repo clone ChickenCombo/disaster-online-reporting-application-v4

Then open the project using Android Studio.

### Firebase Setup

* Create your own Firebase project and register `com.app.dorav4` as the Android package name and download the `google-services.json` file.

* Switch to *Project* view in Android Studio and paste `google-services.json` into `app/` directory.

* Configure the following on your Firebase dashboard: Authentication, Realtime Database, and Storage.

### Maps SDK for Android Setup

* Go to Google Cloud Platform and select your Firebase project.

* Navigate to APIs and Services and enable *Maps SDK for Android*.

* Navigate to Credentials to get your API key.

## Installation

Connect an Android device or an emulator on your development machine.

### Android Studio

* Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar
* Select the device you wish to run the app on and click 'OK'
