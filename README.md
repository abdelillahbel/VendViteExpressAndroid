# VendVite Express Mobile App

##### We are updating our UI 💫

<table>
  <tr>
    <td><img src="app/src/main/ic_launcher-playstore.png" alt="Logo"></td>
    <td>VendVite Express is a cutting-edge mobile application designed to revolutionize the way you buy and sell vehicles. With a focus on convenience, efficiency, and user experience, VendVite Express connects clients with nearby car and vehicle buyers in real-time, making the process of selling or buying a vehicle as seamless as possible.</td>
  </tr>
</table>

## Screenshots

<table>
  <tr>
    <td align="center">
      <img src="screenshots/screenshot1.png" width="200" alt="Screenshot 1">
    </td>
    <td align="center">
      <img src="screenshots/screenshot2.png" width="200" alt="Screenshot 2">
    </td>
    <td align="center">
      <img src="screenshots/screenshot3.png" width="200" alt="Screenshot 3">
    </td>
    <td align="center">
      <img src="screenshots/screenshot4.png" width="200" alt="Screenshot 4">
    </td>
  </tr>
</table>

## Key Features

###### **Tested on API VERSION 33**

### _The app still under development.._

* **Real-time Buyer Matching:** VendVite Express uses advanced location-based services to connect you with potential buyers in your vicinity. No more endless searching - find interested buyers at the touch of a button.
* **Secure Transactions:** Safety is our top priority. VendVite Express ensures secure transactions with verified buyers, providing you with peace of mind.
* **User-friendly Interface:** With its intuitive design and easy-to-navigate interface, VendVite Express makes buying and selling vehicles a breeze.
* **Efficient Negotiation Process:** Communicate directly with potential buyers through the app. Discuss prices, arrange viewings, and finalize transactions in a few simple steps.
* **Detailed Vehicle Listings:** Create comprehensive vehicle listings with detailed descriptions and high-quality images. Make your vehicle stand out and attract the right buyers.

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
* A Firebase Project with Firestore and Authentication enabled (Email & Password)
* A Google Maps API key (Enable the API from google cloud console)

**Installation**

1. Clone this repository: `git clone https://github.com/devunionorg/VendViteExpressAndroid`
2. Open the project in Android Studio.
3. Obtain your Google Maps API key and add it to your project's configuration inside `secrets.properties` file, don't forget to setup `local.defaults.properties` file following instructions from official doc's.
4. Set up your Firebase project and link it to the Android app.

## Project Structure

* `app/src/main/java/com/ensb/vendviteexpress`
    * `data`: Data models, repositories, and data sources.
    * `view`: Views (Activities/Fragments), ViewModels, Adapters.
    * `utils`: Helper classes and extension functions.
* `app/build.gradle`: Project dependencies and configurations.

#### This project is a part of Google Developer Student Clubs Solution Challenge !

### About US :
* ##### [GDSC ENSB](https://github.com/gdsc-ensb) - National school of biotechnology - Algeria
* ##### [DevUnionHQ](https://github.com/devunionorg) - Software development company - France
* ##### [ABDEL ILLAH B](https://github.com/abdelillahbel) : GDSC ENSB Core team member and IT manager at DevUnion & VendVite App owner & developer.