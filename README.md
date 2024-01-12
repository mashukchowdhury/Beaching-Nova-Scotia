# Beaching Nova Scotia (BeachingNS)

# BeachingNS Android App Setup Instructions

## Prerequisites
- **Android Studio (HedgeHog version)**: This is the latest version of Android Studio and is required for the setup. You can download it from the official Android Developer website. Ref: https://developer.android.com/studio?utm_source=android-studio

## Steps to Setup

1. **Download and Install Android Studio**: If you’re new to Android Studio, it’s an IDE that allows you to create Android apps. Please  download the latest version (HedgeHog) from the official Android Developer website. (Link provided under Prerequisites). Please follow the instructions provided on the website to install it on your system. Make sure your system meets the minimum requirements for running Android Studio. For more information: https://developer.android.com/codelabs/basic-android-kotlin-compose-install-android-studio#0

2. **Extract the Project Files**: If you have received a zip folder containing the BeachingNS Android App project files. Extract this folder to a location on your computer. Please make sure to remember this location as you will need it in the next step.

3. **Open the Project in Android Studio**: Open Android Studio and select 'Open an existing Android Studio project' from the welcome screen. Navigate to the location where you extracted the project files and select the project. Android Studio will take some time to import the project and set up the necessary files.

4. **Setup Android Emulator**: The Android Emulator simulates Android devices on your computer, allowing you to test your app on different devices and Android API levels. Here’s how to set it up:
    - In Android Studio, click on `Tools > Device Manager`.
    - Click on `Create Virtual Device`. (Usually a "+" symbol)
    - Choose a device definition that best suits your needs and click `Next`. For best results, please select a Pixel 7 Pro.
    - Select the system image called UpsideDownCake with API level 34, and click `Next`.
    - Verify the configuration, please make sure startup orientation is "Portrait" and then click `Finish`. Your virtual device is now ready for use.

5. **Run the App**: Now, you can run the app by clicking on the green triangle (Run) button in the toolbar at the top of Android Studio. Select the emulator you just created from the target device dropdown. The app will launch on the emulator after a short build process.

**How to run the app via USB Debugging** (Recommended if you have an Android Device)

1) Complete steps `1,2 and 3` from the previous instructions to download and install Android Studio and open the extract the project files in the IDE.
2) If you have your physical Android device ready, connect it to your computer using a USB cable.
3) On your Android device, go to `Settings > About Phone`. Tap on Build Number seven times to enable Developer Options.
4) Go back to `Settings > Developer Options`. Enable `USB Debugging`.
5) If you are running Android Studio on Windows, please use this link to install your OS specific OEM USB driver. Ref: https://developer.android.com/studio/run/oem-usb
6) On your computer, open Android Studio and click on the App module in the Project pane.
7) Click on `Run > Run ‘app’ `or press Shift + F10.
8) In the Select Deployment Target dialog, select your device, and click OK.
9) Android Studio will install the app on your connected device and start it.

More Information: https://developer.android.com/studio/run/device


## File Structure Wiki

1. **Login Activity**: app/src/main/java/com/example/beachingns/Login.java consists the source code and app/src/main/res/layout/activity_login.xml handles the UI elements for this activity.
2. **Registration Activity**: app/src/main/java/com/example/beachingns/Registration.java consists the source code and app/src/main/res/layout/activity_registration.xml handles the UI elements for this activity.
3. **Password Reset Activity**: app/src/main/java/com/example/beachingns/PasswordReset.java consists the source code and app/src/main/res/layout/activity_password_reset.xml handles the UI elements for this activity.
4. **Homepage/Landing Page**: This is the starting page of the application and will load as soon as the app launches successfully. This page involves the following:

a. **MainActivity.java**: app/src/main/java/com/example/beachingns/MainActivity.java (This is the primary java file that drives the various functions of the homepage. The filters and search, recyclerview for displaying the main beach list, toolbar buttons to change pages,etc. are controlled from here. This file also contains code that adjusts the layout of activity_main.xml when there's a user logged in. A bottom navigation bar appears when there's a user logg in. For more details please checkout the illustrations detailed in the Final Report.)

b. **activity_main.xml**: app/src/main/res/layout/activity_main.xml (This contains the UI elements for search, filters, etc. that appear on the homepage)

c. **MasterBeachListAdapter.java**: app/src/main/java/com/example/beachingns/MasterBeachListAdapter.java (Responsible for binding BeachItem object with the arrayList that populates the RecyclerView)

d. **BeachItem.java**: app/src/main/java/com/example/beachingns/BeachItem.java (This is the BeachItem object that stores all the attributes of the beaches)

e. **beachitem.xml**:  app/src/main/res/layout/beach_list.xml (each beach is displayed on the recyclerview with the help of this file)

f. **bottom_navigation_menu.xml**: app/src/main/res/menu/bottom_navigation_menu.xml (the UI for the nav menu on the bottom which displays for registered users)

5. **Beach Landing Page**: This page contains all the information related weather and beach description. This page passes the location coordinates of beaches to Google Maps when "Get Directions" is clicked. There's also a "Set Favorite" button that only appears if you are logged in. The button sends the selected beach to the user's personal favorites list.

app/src/main/java/com/example/beachingns/beachLandingPage.java (Drives the code for the activity)

app/src/main/res/layout/beach_landing.xml (Handles the UI that displays all the weather information for a particular beach)

6. **Preferred Beaches Activity**: app/src/main/java/com/example/beachingns/preferredBeaches.java (Contains code that reads from the Firestore db and updates the beachList adapter to display only the few beaches that are marked as favorites)

app/src/main/res/layout/activity_preferred_beaches.xml (contains the UI elements for the preferred beach list page)

7. **User Profile**:  app/src/main/java/com/example/beachingns/UserProfile.java (Contains the source code of this activity to read from the db and displays the logged in user's information)

app/src/main/res/layout/user_profile.xml (Contains the UI code)


**Other Important files**:

1) Manifest.xml = app/src/main/AndroidManifest.xml (Contains essential information about activities that are actively being used in the project, information about targetAPI, app icons, etc.)

2) build.gradle (app) = app/build.gradle (App specific config information and dependecies that have been implemented)

3) build.gradle (project) = found in the project root  (contains project specific dependencies and plugins)

4) google-services.json = app/google-services.json (File connects the project to the Firestore Database)


**Credentials for Use**:

User: mosmanchy@gmail.com
Password: Password123!

P.S: You may register through the app if you prefer having your own account.
