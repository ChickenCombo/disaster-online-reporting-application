![Mockup](https://i.imgur.com/nWus4Zf.jpg)

# Disaster Online Reporting Application

**Disaster Online Reporting Application (DORA) version 4** is an Android-based mobile application that allows users to easily and quickly report disasters in real-time. With this app, users can provide critical information about the disaster, including its location, type, and images allowing emergency responders to emergency responders to quickly and effectively respond to the situation. This app also allows users to receive real-time updates about the disaster and any relevant safety information.

## 📄 About

**Members:**

* Castro, Michaela Marie N.
* Cuadra, John Lester G.
* Maruno, Hitomi B.
* Quitor, Cedric John D.

**Technical Adviser:**

* Asst. Prof. Zhuo, Eugenia R., DIT

## 📸 Screenshots
<p align="center">
  <img src="https://i.imgur.com/vTQltdl.jpeg" width="20%" height="20%">
  <img src="https://i.imgur.com/I1iuOU5.jpeg" width="20%" height="20%">
  <img src="https://i.imgur.com/jG5jlge.jpeg" width="20%" height="20%">
</p>

<p align="center">
  <img src="https://i.imgur.com/eJPPaCK.jpeg" width="20%" height="20%">
  <img src="https://i.imgur.com/VT23vwQ.jpeg" width="20%" height="20%">
  <img src="https://i.imgur.com/bB8Pk3s.jpeg" width="20%" height="20%">
</p>

## ⚙️ Getting Started

### Project Setup

Clone the repository:

    git clone ChickenCombo/disaster-online-reporting-application

Then open the project using Android Studio.

### Firebase Setup

* Create your own Firebase project and register `com.app.dorav4` as the Android package name and download the `google-services.json` file.

* Switch to *Project* view in Android Studio and paste `google-services.json` into `app/` directory.

* Configure the following on your Firebase dashboard: Authentication, Realtime Database, and Storage.

### Cloud Messaging Setup

* After setting up Firebase, navigate to Project Settings > Cloud Messaging > Cloud Messaging API (Legacy) to get your API key.

* Return to the project directory and find `PushNotificationService.java`.

* Replace the API key with your own API key.

### Maps SDK for Android Setup

* Go to Google Cloud Platform and select your Firebase project.

* Navigate to APIs and Services and enable *Maps SDK for Android*.

* Navigate to Credentials to get your API key.

* Find `google_maps_api.xml` and replace the API key.

## 📱 Installation

Connect an Android device or an emulator on your development machine.

### Android Studio

* Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar.
* Select the device you wish to run the app on and click 'OK'.
