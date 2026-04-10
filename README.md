<img src="extras/shapeshifter/logo.svg" width="200" height="200" align="center">

A modern Android TV app for IPTV service.

### 🛠 Architecture & Tech Stack
*   **📺 UI Framework:** Currently utilizes the **Leanback UI Toolkit** (Legacy). 
*   **🚀 Modernization:** Integration of **Jetpack Compose for TV** is in progress/planned to replace deprecated Leanback components.
*   **💉 DI:** Hilt (Dagger)
*   **📦 Local Storage:** Room Database
*   **🌐 Networking:** Retrofit + GSON

---

### 🎨 Flavors
*   ✅ **online:** Production version. Uses real network calls and local Room database.
*   🧪 **demo:** Mocked version. Uses static local data for UI/UX testing without network requirements.
*   🛠️ **dev:** Development version. Same as `online` but uses a separate package name (`.dev`) to allow side-by-side installation.

---

### 🏗️ Modules
*   📦 **core:** Framework-agnostic logic. Contains ViewModels, Repositories, and Data sources (Room/Retrofit).
*   📺 **leanback-app:** The legacy UI module containing Activities, Fragments, and Presenters based on `androidx.leanback`.
*   🎨 **compose-app (Planned):** The modern UI module leveraging **Compose for TV** for a more flexible and declarative UI.

---

### 📥 Downloads
[![Download APK](https://img.shields.io/badge/Download-APK-green.svg)](https://bit.ly/4tHBq8v)

<img src="extras/github_3RockTV-app.apk_qrcode.png" width="120" height="120" align="center">

`https://bit.ly/4tHBq8v`

[![AFTVnews](https://img.shields.io/badge/aftv.news_Code-4262342-orange?style=for-the-badge&logo=android)](https://aftv.news/4262342)


Check the **Releases** panel on the right for older versions.

---

### 💻 Tech Summary
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![SQLite](https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white)
