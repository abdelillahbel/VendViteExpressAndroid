***

# VendVite Express Mobile App

**VendVite Express App is an Android application designed to revolutionize the distribution process between sales points and distributors. 
Built with Kotlin and leveraging the power of Google Technologies Include Firebase, Google Maps API, and modern Android development best practices, this app makes the distribution experience faster, smoother, and more efficient.**

### Test user credentials

#### Distributor
* Email : `io@email.com`
* Password : `123456`
#### Seller
* Email : `ioi@email.com`
* Password : `123456`

## Key Features

###### **Tested on API VERSION 33**

### _~~The app still under development..~~_

* **Streamlined Sales Point Management:** Easily manage sales points, including adding, updating,
  and viewing locations for improved logistics.
* **Smart Route Optimization:** Integrated Google Maps API ensures the most efficient delivery
  routes, saving time and fuel costs.
* **Real-time Orders Tracking:** Stay updated on order statuses with comprehensive in-app order
  tracking features.
* **Inventory Management:** Keep tabs on stock levels and manage inventory to prevent shortages and
  unnecessary surpluses.
* **Seamless Communication:** Facilitate direct communication between sales points and distributors
  for greater transparency.

## Technologies

* **Kotlin:** Modern, concise, and safe programming language for robust Android development.
* **Firebase Auth:** Secure and easy-to-implement user authentication system.
* **Firebase Firestore:**  Scalable NoSQL cloud database for real-time data synchronization.
* **Google Maps API:** Integration for location management and route optimization.
* **View Binding:** Eliminates findViewById calls, making code cleaner.
* **RxJava:** Streamline asynchronous operations and event handling.
* **Coroutines:** Simplify asynchronous programming with easy-to-read code.
* **Navigation Component:** Structuring in-app navigation for intuitive user experiences.

## Getting Started

**Prerequisites**

* Android Studio (latest recommended)
* A Firebase Project with Firestore and Authentication enabled
* A Google Maps API key (Enable the API from google cloud console)

**Installation**
##### Note : The `google-services.json` file is included in the project so you just need to obtain your google api key and it's done.
##### Or follow the instructions bellow if you want to use your own firebase api, but don't forget to setup rules in firestore!

1. Clone this repository: `git clone https://github.com/gdsc-ensb/VendViteExpressAndroid.git`
2. Open the project in Android Studio.
3. Obtain your Google Maps API key and add it to your project's configuration inside `secrets.properties` file, don't forget to setup `local.defaults.properties` file following instructions from official doc's.
4. Set up your Firebase project and link it to the Android app.
## Project Structure

* `app/src/main/java/com/ensb/vendviteexpress`
    * `data`: Data models, repositories, and data sources.
    * `view`: `[SELLER] [DISTRIBUTOR]` Views (Activities/Fragments), ViewModels, Adapters.
    * `utils`: Helper classes and extension functions.
* `app/build.gradle`: Project dependencies and configurations.

## This project is a part of Google Developer Student Clubs Solution Challenge !

### About US :
* ##### [GDSC ENSB](https://github.com/gdsc-ensb) - National school of biotechnology - Algeria
* ##### [ABDEL ILLAH B](https://github.com/abdelillahbel) : GDSC ENSB Core team member and IT manager & VendVite App owner & developer.
