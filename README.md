![Mockup](https://i.imgur.com/nWus4Zf.jpg)

# Disaster Online Reporting Application

**Disaster Online Reporting Application (DORA)** version 4 is an Android-based mobile application that allows users to easily and quickly report disasters in real-time. With this app, users can provide critical information about the disaster, including its location, type, and images allowing emergency responders to emergency responders to quickly and effectively respond to the situation. This app also allows users to receive real-time updates about the disaster and any relevant safety information.

A web app for Disaster Online Reporting Application v4 was also made which serves as the admin panel for Local Government Units. It features disaster report monitoring, managing of evacuation centers, sending of advisories via push notification, and similar to the mobile app, it has a map for monitoring disaster reports and evacuation centers.

More information about the web app can be found here: [Disaster Online Reporting Application Web App](https://github.com/cedricquitor/disaster-online-reporting-application-v4-web-app)

## 📄 About

This capstone project was granted the "3rd Best Capstone Award" during the IT Research Colloquium 2023 held at the University of Santo Tomas.

**ACM Conference Paper:** [DORA v4 Conference Paper](https://drive.google.com/file/d/1NkkymFQ7Tw61rF5l68LpiJRzfJdXonWZ/view?usp=sharing)

**Members:**

* Castro, Michaela Marie N.
* Cuadra, John Lester G.
* Maruno, Hitomi B.
* Quitor, Cedric John D.

**Technical Advisor:**

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
