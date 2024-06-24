# ToDo List App

# Description
This Android application provides a simple and efficient way to manage your daily tasks. It allows you to create, edit, delete, and mark tasks as complete. The app also supports task prioritization, optional reminders with notifications, and potential cloud synchronization for data backup and cross-device access.

# Features
* **Task Management:** Create, edit, delete, and mark tasks as complete.
* **Prioritization:** Assign priority levels (High, Medium, Low) to tasks.
* **Reminders:** Set reminders for tasks and receive notifications at the specified time.
* **Filtering and Sorting:** Filter tasks by completion status and sort them by priority.
* **Cloud Synchronization (Optional):** Back up task data to the cloud and access it across multiple devices (implementation in progress).

# Technologies Used
* **Kotlin:** The primary programming language for the app.
* **Android SDK:** The framework for building Android applications.
* **Android View:** For building the UI
* **ViewModel:** For managing UI-related data and logic.
* **LiveData:** For observing changes in data and updating the UI accordingly.
* **Room:** For persisting transaction data locally.
* **RecyclerView:** For displaying the transaction history in a list.
* **Coroutines:** For handling asynchronous operations (like database interactions).
* **Firebase Authentication:** For user sign-in and registration.

# Getting Started
* **Clone the repository:** git clone https://github.com/felixyoma4u/TODO_APP.git
* **Set up Firebase:** Create a Firebase project and connect it to your Android app. Follow the instructions in the [Firebase documentation](https://firebase.google.com/docs/android/setup).
* **Open the project in Android Studio.**
* **Build and run the app on an Android emulator or device.**

# Project Structure

* **`ui`:** Contains the UI components (Activities).
* **`viewModel`:** Contains the ViewModels for managing UI data and logic.
* **`repository`:** Contains the repository classes for interacting with data sources (like the Room database).
* **`database`:** Contains the Room database setup (entities, DAOs, database class).
* **`model`:** Contains data classes representing the app's data (e.g., Account, Transaction).

# Testing
The project includes unit tests for the `TaskViewModel` and potentially other components. To run the tests:
1. Open Android Studio and navigate to the test directory.
2. Right-click on a test class or method and select "Run".

# Future Enhancements
* **Improved Task Reminders:** Customizable notification settings, recurring reminders, and snooze functionality.
* **Task Categorization:** Support for categorizing tasks into different lists or tags.
* **Enhanced UI/UX:** Animations, custom views, and more intuitive navigation.
* **Complete Cloud Synchronization:** Full implementation of Firebase Firestore integration for data backup and cross-device access.

# Contributing
Contributions are welcome! Feel free to open issues or submit pull requests.

# License
This project is licensed under the MIT License