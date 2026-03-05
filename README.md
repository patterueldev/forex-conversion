This is a Kotlin Multiplatform project targeting Android, iOS, Web, Server.

## Quick Start

### Prerequisites
- **Docker** (for running server and web via `docker compose`)
- **JDK 21** (for local development)
- **Node.js 20+** (for web app development)
- **Android SDK & Emulator** (for mobile development)
- **Xcode** (for iOS development on macOS)

### Setup Environment

Create a `.env` file in the root directory with your configuration:

```bash
cp .env.example .env
```

Edit `.env` and add your Unirate API key:

```env
UNIRATE_API_KEY=your_api_key_here
ALLOWED_HOSTS=localhost,thursday.local
ALLOWED_PORTS=80,5173
```

### Docker Compose (Recommended for Quick Testing)

Run server and web with a single command:

```bash
docker compose up -d
```

- **Server**: http://localhost:8080
- **Web**: http://localhost

Or with environment variables:

```bash
UNIRATE_API_KEY=your_key docker compose up -d
```

### Local Development

#### Server (Ktor)

```bash
./gradlew :apps:server:run
```

Server runs on `http://localhost:8080`

#### Web App (React + Vite)

```bash
cd apps/web
npm install
npm run dev
```

Web runs on `http://localhost:5173`

#### Mobile Apps (iOS & Android)

Start iOS Simulator and Android Emulator, then run:

```bash
bash run-mobile.sh
```

This script:
- Opens iOS Simulator and boots the first available device
- Starts the first available Android AVD
- Builds and installs both mobile apps

---

## Project Structure

  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

* [/iosApp](apps/iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:

- for the Wasm target (faster, modern browsers):
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
      ```
- for the JS target (slower, supports older browsers):
    - on macOS/Linux
      ```shell
      ./gradlew :composeApp:jsBrowserDevelopmentRun
      ```
    - on Windows
      ```shell
      .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
      ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](apps/iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack
channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).