# 🚀 LiveLink — Realtime Location Sharing App

> A modern Android realtime tracking application built with Jetpack Compose & Firebase.

LiveLink is a realtime Android application that allows two users to connect through a QR code and track each other's location while chatting in a private room.

This project is designed as a **portfolio-level Android application** demonstrating modern Android architecture and realtime systems.

---

# ✨ Features

### 📍 Realtime Location Tracking
Users can see each other's position on the map in realtime.

### 💬 Realtime Chat
Two users can chat instantly inside the same room.

### 🏠 Room System
Users can:
- Create a room
- Share room using QR code
- Join an existing room

Room states:
- `WAITING`
- `ACTIVE`
- `CLOSED`

### 🟢 Presence System
Detects:
- Online / Offline status
- Last seen timestamp

### 🔄 Background Tracking
Location continues updating even when the app goes to background using a **Foreground Service**.

### 🚪 Exit Confirmation
When a user wants to exit:
- Exit request is sent
- The other user confirms
- Room closes safely

---

# 🧠 Architecture

This project follows **Clean Architecture + MVVM**.
Architecture goals:

- Separation of concerns
- Testable business logic
- Replaceable backend
- Scalable structure

---

# 🏗 Project Structure
core/
util/
base/
di/

data/
remote/firebase/
repository/
mapper/

domain/
model/
repository/
usecase/

presentation/
auth/
home/
room/
map/
chat/
navigation/

service/
LocationService.kt


---

# 📡 Firestore Database Structure

### rooms


rooms/{roomId}
hostId
guestId
status
exitRequestBy


### locations


rooms/{roomId}/locations/{userId}
lat
lng
updatedAt


### messages


rooms/{roomId}/messages/{messageId}
senderId
content
timestamp


### users


users/{userId}
online
lastSeen


---

# ⚡ Realtime Implementation

Firestore snapshot listeners are wrapped using **Kotlin Flow**.

Example:


Firestore Snapshot
↓
callbackFlow
↓
StateFlow
↓
Jetpack Compose UI


This ensures:
- Reactive UI updates
- Lifecycle safety
- Clean separation of layers

---

# 📦 Tech Stack

Language  
Kotlin

UI  
Jetpack Compose

Architecture  
Clean Architecture + MVVM

Async  
Kotlin Coroutines + Flow

Backend  
Firebase

Realtime Database  
Cloud Firestore

Authentication  
Firebase Auth

Location  
FusedLocationProviderClient

Background Service  
Foreground Service

---

# 🎯 Learning Goals

This project demonstrates:

- Realtime data synchronization
- Clean Architecture implementation
- SOLID principles
- Firestore realtime streaming
- Flow / StateFlow reactive state
- Background-safe location tracking

---

# 🚀 Future Improvements

- Push Notifications (Firebase Cloud Messaging)
- Multi-user room support
- Typing indicator
- Location history
- Offline caching using Room Database
- Unit testing & UI testing

---

# 👨‍💻 Author

Android Developer focusing on modern mobile architecture and realtime systems.
---------------------
# 🏛 Clean Architecture

This project follows **Clean Architecture** principles to separate concerns and make the codebase scalable and testable.

---

# Layer Structure


Presentation Layer
↓
Domain Layer
↓
Data Layer


---

# Presentation Layer

Contains:

- Jetpack Compose UI
- ViewModels
- UI state

Responsibilities:

- Display data
- Collect StateFlow
- Send user actions to ViewModel

No business logic exists in UI.

---

# Domain Layer

Contains:

- UseCases
- Repository interfaces
- Domain models

Responsibilities:

- Business logic
- Application rules
- UseCase orchestration

Domain layer does **not depend on Android or Firebase**.

---

# Data Layer

Contains:

- Firebase implementation
- Repository implementations
- DTO mappers

Responsibilities:

- Data retrieval
- Network calls
- Database access

---

# Dependency Rule

Dependencies always point inward:


Presentation → Domain → Data


Domain never depends on external frameworks.

---

# Benefits

- Testable business logic
- Replaceable backend
- Cleaner codebase
- Easier maintenance
----------------------------
# 📡 Realtime System Design

This document explains how the realtime system of the application works.

---

# Room Creation Flow

User A:

1. Create room
2. Room document created in Firestore
3. Room ID converted to QR code

User B:

1. Scan QR
2. Join room
3. Room status changes to ACTIVE

---

# Realtime Location Flow

Location updates occur every few seconds.

Flow:


FusedLocationProvider
↓
LocationService
↓
Repository
↓
Firestore update
↓
Snapshot Listener
↓
Flow / StateFlow
↓
Jetpack Compose UI


Result:
Both users see each other's marker update on the map.

---

# Chat System

Message flow:


User sends message
↓
Firestore message subcollection
↓
Snapshot listener
↓
Realtime UI update


Messages are ordered using timestamps.

---

# Presence System

Presence detection tracks user activity.

When app starts:


users/{userId}
online = true


When app closes:


online = false
lastSeen = timestamp


Other users listen to this field to update presence status.

---

# Exit Room Flow

User A presses Exit:


exitRequestBy = userA


User B receives request:


Show confirmation dialog


If confirmed:


room.status = CLOSED


Both users leave the room.

---

# Realtime Technologies Used

- Firebase Firestore
- Snapshot Listener
- Kotlin Coroutines
- Flow / StateFlow

These provide reactive updates across the application.