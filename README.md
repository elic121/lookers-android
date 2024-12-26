# MVVM Architecture with Room and Retrofit in Kotlin

## Overview

This project is an Android application that implements the **Model-View-ViewModel (MVVM)** architecture using Kotlin. It utilizes **Room** for local database management and **Retrofit** for network operations.

---

## Features

- **MVVM Architecture**: Separates concerns for easier maintenance and testing.
- **Room Database**: Persistent local storage for app data.
- **Retrofit**: Simplified network requests for fetching data from APIs.
- **Kotlin Coroutines**: Asynchronous programming to improve performance and responsiveness.

---

## Project Structure

```bash
├── app/
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/template/
│   │   │   │   ├── di/
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   └── NetworkModule.kt
│   │   │   │   ├── model/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── local/
│   │   │   │   │   ├── network/
│   │   │   │   │   └── repository/
│   │   │   │   ├── view/
│   │   │   │   └── viewmodel/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
├── build.gradle.kts
└── settings.gradle.kts
```

### Dependency Injection (DI)

Dependency Injection (DI) is a design pattern that achieves **Inversion of Control (IoC)** between classes and their dependencies. This pattern allows for:

- **Better Separation of Concerns**: Each component has a distinct responsibility.
- **Easier Testing**: Mock dependencies can be easily injected for unit testing.
- **Improved Code Maintainability**: Changes in one part of the application have minimal impact on others.

In this project, we have two key DI modules:

- **`DatabaseModule`**
- **`NetworkModule`**

---

### DatabaseModule

The **`DatabaseModule`** is responsible for providing the **Room database instance** and the associated **Data Access Object (DAO)** for the application. Key features include:

- **Singleton Instance**: Utilizes Dagger Hilt to ensure that the `AppDatabase` is a singleton, maintaining a single instance throughout the application lifecycle.
- **Efficient Data Handling**: Facilitates efficient local data storage and retrieval by providing the `ExampleDao`.

---

### NetworkModule

The **`NetworkModule`** configures the networking layer of the application using **Retrofit** and **OkHttp**. Key features include:

- **Singleton Retrofit Instance**: Provides a singleton instance of `Retrofit`, set up with a base URL and a Gson converter for JSON serialization.
- **Custom Timeout Settings**: Configures the `OkHttpClient` with custom timeout settings to ensure efficient handling of network requests.
- **Seamless API Communication**: Enables the application to communicate with external APIs while maintaining clean and manageable code.

---

### Architectural Components

- **Model**: Contains data classes and data handling logic (Room entities, Retrofit services).
- **ViewModel**: Contains the business logic and serves as a bridge between the View and Model.
- **View**: Represents the UI components, observing LiveData from the ViewModel.

## Installation

```bash
git clone https://github.com/elic121/android_mvvm_template.git
```
