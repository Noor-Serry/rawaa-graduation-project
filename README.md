# Rawaa (رواء) — University Management Platform

> A graduation project Android application that provides a unified academic management experience for Egyptian universities, serving students, teachers, university admins, and super admins from a single app.

---

## Table of Contents

- [Overview](#overview)
- [Screenshots](#screenshots)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API & Backend](#api--backend)
- [User Roles](#user-roles)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

**Rawaa** is an Android application built as a graduation project that digitalizes and organizes university life. It allows students to track lectures, assignments, and grades in one place, while providing teachers and admins with powerful tools for scheduling, attendance, grading, and user management.

The app supports **four distinct user roles**, each with its own navigation flow and set of screens, all driven by a single backend API.

---

## Screenshots

### Onboarding & Authentication
<p align="center">
<img width="180"  alt="image" src="https://github.com/user-attachments/assets/57c90065-a2cf-46a8-bc15-05c5194e6396" />
<img width="180"  alt="image" src="https://github.com/user-attachments/assets/7703b874-f424-4722-98d0-fab9949d7131" />
<img width="180"  alt="image" src="https://github.com/user-attachments/assets/da48212b-3516-4017-909e-5d053c5b0be1" />
<img width="180"  alt="image" src="https://github.com/user-attachments/assets/32d6a8ef-ff75-4e69-adc8-1b759f360f00" />
</p>



### Screens

<p align="center">
  <img src="https://github.com/user-attachments/assets/9a99044f-622e-436a-81a2-86af5fff8306" width="180"/>
  <img src="https://github.com/user-attachments/assets/00b44475-d51f-4da0-bbe5-0286a4f4cc2c" width="180"/>
  <img src="https://github.com/user-attachments/assets/e3cd48b9-30ca-4ab4-b769-16b4d3d2ebfd" width="180"/>
  <img src="https://github.com/user-attachments/assets/31fdd773-6d92-47c3-8ad5-6a1017751d76" width="180"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/b8e6ea9b-b725-45bb-82cf-eb39a4ae9a60" width="180"/>
  <img src="https://github.com/user-attachments/assets/c4006c0a-a5c4-4f15-8767-602c4229af31" width="180"/>
  <img src="https://github.com/user-attachments/assets/fefd4d6b-9dd8-47a0-8b15-60d10a82d84a" width="180"/>
  <img src="https://github.com/user-attachments/assets/9d64cf21-08fc-4992-910a-ca0925d659a5" width="180"/>
</p>
<p align="center">
  <img src="https://github.com/user-attachments/assets/eb1caa73-097b-4c92-b821-c2da8e9affaf" width="180"/>
  <img src="https://github.com/user-attachments/assets/d1fc6fc1-6d6b-4359-8282-5713258b8b01" width="180"/>
  <img src="https://github.com/user-attachments/assets/8ad54aae-a27e-4e8e-a53b-68addfccc657" width="180"/>
  <img src="https://github.com/user-attachments/assets/399575bc-b63f-4960-b175-80b916d1eeea" width="180"/>
</p>


<p align="center">
  <img src="https://github.com/user-attachments/assets/3d8c9198-4d9b-4aa1-822b-4b03d8dfed7e" width="180"/>
  <img src="https://github.com/user-attachments/assets/219c20e4-5195-4325-b1f4-81779015d86f" width="180"/>
  <img src="https://github.com/user-attachments/assets/c7a26832-e16a-4ed1-bb3a-8ecd3da736cd" width="180"/>
  <img src="https://github.com/user-attachments/assets/a379dfa0-6c96-40dd-a104-43aa83a60ca0" width="180"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/f7272e6a-62d8-4213-ba68-0c1f1b8b5c09" width="180"/>
</p>

---

## Features

### Student
- 📚 View enrolled courses and course materials
- 📅 Access weekly schedule
- ✅ Track attendance records
- 🏆 View grades and academic reports
- 🔔 Receive notifications
- 👤 Manage personal profile

### Teacher (Doctor)
- 🏠 Dashboard with class overview
- 📋 View students list per course
- ✅ Record and manage attendance
- 📝 Grade students
- 📅 View teaching schedule
- 👤 Profile management

### University Admin
- 👥 Full user management (add, edit, view, delete)
- 📚 Course management
- 🏛️ Department management
- ✅ Attendance overview
- 📅 Schedule management
- 📊 Reports and analytics
- ⚙️ University settings
- 📝 Exam management

### Super Admin
- 🏛️ Manage all universities on the platform
- ➕ Add new universities
- 📊 Platform-wide statistics

---

## Architecture

Rawaa follows **MVI (Model–View–Intent)** architecture with a clean layered separation:

```
UI Layer
├── Screen (Composable)
├── ViewModel (state holder)
├── UiState (immutable state data class)
├── InteractionListener (user events interface)
└── Effect (one-time side effects: navigation, toasts)

Data Layer
├── UniversityRepository (single source of truth)
├── ApiDtos (Kotlinx Serialization data classes)
└── TokenDataStore (DataStore Preferences for JWT)

DI Layer (Koin)
├── appModule
├── dataModule  (Ktor client, repository, DataStore)
└── uiModule    (ViewModels)
```

Each screen follows the same pattern:
- **UiState** — what to display
- **InteractionListener** — what the user can do
- **Effect** — navigate / show snackbar / etc.

---

## Tech Stack

| Layer | Library / Tool |
|---|---|
| Language | **Kotlin** |
| UI | **Jetpack Compose** |
| Navigation | **Navigation3** (androidx.navigation3) |
| Architecture | **MVI + ViewModel** |
| DI | **Koin** |
| Networking | **Ktor Client** (with Bearer auth) |
| Serialization | **Kotlinx Serialization** |
| Local Storage | **DataStore Preferences** |
| Build System | **Gradle (Kotlin DSL)** |
| Min SDK | Android API (see `build.gradle`) |

---

## Project Structure

```
src/main/java/noor/serry/rawaa/
│
├── App.kt                        # Koin initialization
├── MainActivity.kt               # Single-activity entry point
├── RawaaApp.kt                   # Root composable + role-based routing
│
├── data/
│   ├── dto/ApiDtos.kt            # All API request/response models
│   ├── local/TokenDataStore.kt   # JWT token persistence
│   └── repository/
│       └── UniversityRepository.kt  # All API calls (grouped by role)
│
├── di/
│   ├── appModule.kt              # Root Koin module
│   ├── dataModule.kt             # Ktor + DataStore + Repository
│   ├── uiModule.kt               # Student/Teacher/SuperAdmin ViewModels
│   └── universityAdminUiModule.kt # Admin ViewModels
│
└── ui/
    ├── base/                     # BaseViewModel, DispatcherProvider
    ├── navigation/
    │   ├── base/                 # AppRoute, appEntryProvider
    │   ├── student/              # StudentEntryPoint, HomeNavTab
    │   ├── teatcher/             # TeacherEntryPoint, TeacherNavTab
    │   ├── university_admin/     # AdminEntryPoint, AdminNavTab
    │   └── super_admin/          # AdminEntryPoint, AdminNavTab
    └── screens/
        ├── onboarding/
        ├── login/
        ├── home_student/
        ├── home_teacher/
        ├── home_super_admin/
        ├── courses_student/
        ├── courses_teacher/
        ├── courses_admin/
        ├── schedule/
        ├── schedules_admin/
        ├── attendance_admin/
        ├── grading_teacher/
        ├── students_teacher/
        ├── student_profile_teacher/
        ├── studentScreens/
        ├── profile_student/
        ├── profile_teacher/
        ├── departments_admin/
        ├── exams_admin/
        ├── reports_admin/
        ├── settings_admin/
        ├── users_admin/
        ├── universities_super_admin/
        ├── add_university_super_admin/
        └── notifications/
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17+
- A running instance of the backend API (PHP)

### Clone & Open

```bash
git clone https://github.com/YOUR_USERNAME/rawaa.git
cd rawaa
```

Open the project in Android Studio and let Gradle sync.

### Configure the Backend URL

In `src/main/java/noor/serry/rawaa/di/dataModule.kt`, update the base URL to point to your deployed backend:

```kotlin
private const val BASE_URL = "http://YOUR_SERVER_IP/university-api-v3"
```

### Build & Run

1. Connect an Android device or start an emulator.
2. Press **Run ▶** in Android Studio, or run:

```bash
./gradlew assembleDebug
```

---

## API & Backend

The app communicates with a PHP REST API. All endpoints are grouped by user role:

| Group | Base Path | Description |
|---|---|---|
| Auth | `/api/auth/` | Login, register, logout |
| Super Admin | `/api/super/` | Platform stats, university management |
| Admin | `/api/admin/` | University-level administration |
| Students | `/api/students/` | Student data and dashboard |
| Employees/Doctors | `/api/employees/` | Teacher data and dashboard |
| Departments | `/api/departments/` | Department CRUD |
| Courses | `/api/courses/` | Course management |
| Registrations | `/api/registrations/` | Course enrollment |
| Attendance | `/api/attendance/` | Attendance records |
| Schedules | `/api/schedules/` | Class schedules |
| Exams | `/api/exams/` | Exam management |
| Questions | `/api/questions/` | Exam questions |
| Notifications | `/api/notifications/` | Push notifications |

Authentication uses **JWT Bearer tokens**, stored locally with DataStore and automatically injected by the Ktor `Auth` plugin.

---

## User Roles

The app routes users to different entry points based on the role returned after login:

| Role String | Entry Point |
|---|---|
| `student` | Student dashboard |
| `doctor` | Teacher dashboard |
| `admin` | University Admin panel |
| `super` | Super Admin panel |

---

## Contributing

This is a graduation project. If you'd like to contribute improvements:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Open a Pull Request

---

## License

This project is open-source and available under the [MIT License](LICENSE).

---

_Built with ❤️ as a graduation project — Rawaa (رواء)_
