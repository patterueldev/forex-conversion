#!/usr/bin/env bash
set -euo pipefail

# run-mobile.sh
# Opens the macOS iOS Simulator and starts the first available Android AVD (if present),
# then builds and runs the apps at apps/iosApp and apps/androidApp.
# Usage: bash open-sim-and-emu.sh

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

echo "Starting iOS Simulator..."
if command -v xcrun >/dev/null 2>&1; then
  # Try to find an iPhone device UDID and boot it, else just open Simulator app
  device_udid=$(xcrun simctl list devices | grep -m1 -E "iPhone .* \(([-A-Fa-f0-9]+)\)" | sed -E 's/.*\(([0-9A-Fa-f-]+)\).*/\1/' || true)
  if [ -n "${device_udid}" ]; then
    echo "Booting iOS device UDID: ${device_udid}"
    xcrun simctl boot "${device_udid}" >/dev/null 2>&1 || true
    open -a Simulator
    xcrun simctl bootstatus "${device_udid}" -b >/dev/null 2>&1 || true
  else
    echo "No iPhone device UDID found; opening Simulator app"
    open -a Simulator
    sleep 2
    device_udid=$(xcrun simctl list devices | grep -m1 -E "iPhone .* \(([-A-Fa-f0-9]+)\)" | sed -E 's/.*\(([0-9A-Fa-f-]+)\).*/\1/' || true)
  fi
else
  echo "xcrun not found; ensure Xcode command line tools are installed"
fi

# Build and run iOS app if possible
if [ -n "${device_udid:-}" ]; then
  IOS_APP_DIR="apps/iosApp"
  PROJECT_DIR=""
  WORKSPACE=""
  if [ -d "${IOS_APP_DIR}/iosApp.xcodeproj" ]; then
    PROJECT_DIR="${IOS_APP_DIR}/iosApp.xcodeproj"
  fi
  if [ -d "${IOS_APP_DIR}/iosApp.xcworkspace" ]; then
    WORKSPACE="${IOS_APP_DIR}/iosApp.xcworkspace"
  fi

  if [ -n "${WORKSPACE}" ]; then
    list_output=$(xcodebuild -workspace "$WORKSPACE" -list 2>/dev/null || true)
  elif [ -n "${PROJECT_DIR}" ]; then
    list_output=$(xcodebuild -project "$PROJECT_DIR" -list 2>/dev/null || true)
  else
    list_output=""
  fi

  scheme=$(echo "$list_output" | awk '/Schemes:/{flag=1; next} flag && NF{print $1; exit}' || true)
  if [ -z "$scheme" ]; then
    scheme=$(basename "$IOS_APP_DIR")
  fi

  echo "Building iOS scheme: ${scheme}"
  BUILD_DIR="${ROOT_DIR}/build/iosBuild"
  mkdir -p "$BUILD_DIR"
  if [ -n "${WORKSPACE}" ]; then
    xcodebuild -workspace "$WORKSPACE" -scheme "$scheme" -destination "id=${device_udid}" -configuration Debug -derivedDataPath "$BUILD_DIR" build || true
  elif [ -n "${PROJECT_DIR}" ]; then
    xcodebuild -project "$PROJECT_DIR" -scheme "$scheme" -destination "id=${device_udid}" -configuration Debug -derivedDataPath "$BUILD_DIR" build || true
  fi

  app_path=$(find "$BUILD_DIR" -name "*.app" -type d | head -n1 || true)
  if [ -n "$app_path" ]; then
    echo "Installing iOS app: $app_path"
    xcrun simctl install "$device_udid" "$app_path" || true
    if [ -x "/usr/libexec/PlistBuddy" ]; then
      bundle_id=$(/usr/libexec/PlistBuddy -c "Print :CFBundleIdentifier" "${app_path}/Info.plist" 2>/dev/null || true)
    else
      bundle_id=$(defaults read "${app_path}/Info" CFBundleIdentifier 2>/dev/null || true)
    fi
    if [ -n "$bundle_id" ]; then
      echo "Launching iOS app bundle: $bundle_id"
      xcrun simctl launch "$device_udid" "$bundle_id" || true
    else
      echo "Could not determine bundle id; app installed but not launched"
    fi
  else
    echo "iOS app .app not found after build"
  fi
else
  echo "Skipping iOS build/run because device UDID not found"
fi

echo ""
echo "Starting Android emulator..."
# Locate emulator binary
emulator_bin=""
if [ -n "${ANDROID_SDK_ROOT:-}" ] && [ -x "${ANDROID_SDK_ROOT}/emulator/emulator" ]; then
  emulator_bin="${ANDROID_SDK_ROOT}/emulator/emulator"
elif [ -n "${ANDROID_HOME:-}" ] && [ -x "${ANDROID_HOME}/emulator/emulator" ]; then
  emulator_bin="${ANDROID_HOME}/emulator/emulator"
elif command -v emulator >/dev/null 2>&1; then
  emulator_bin=$(command -v emulator)
elif [ -x "${HOME}/Library/Android/sdk/emulator/emulator" ]; then
  emulator_bin="${HOME}/Library/Android/sdk/emulator/emulator"
fi

if [ -z "${emulator_bin}" ]; then
  echo "Android emulator binary not found. Set ANDROID_SDK_ROOT or ANDROID_HOME, or add emulator to PATH."
else
  avd_name=$("${emulator_bin}" -list-avds | head -n 1 || true)
  if [ -z "${avd_name}" ]; then
    echo "No Android AVDs found. Create one with avdmanager or Android Studio."
  else
    already_running=false
    if command -v adb >/dev/null 2>&1; then
      if adb devices | grep -q "emulator"; then
        already_running=true
      fi
    fi

    if [ "${already_running}" = true ]; then
      echo "An Android emulator appears to be already running."
      device_serial=$(adb devices | awk '/emulator/{print $1; exit}' || true)
    else
      echo "Starting emulator: ${avd_name}"
      nohup "${emulator_bin}" -avd "${avd_name}" -netdelay none -netspeed full >/dev/null 2>&1 &
      disown || true
      echo "Emulator launching in background"
      sleep 5
      device_serial=$(adb devices | awk '/emulator/{print $1; exit}' || true)
    fi

    if [ -n "${device_serial}" ]; then
      echo "Waiting for Android emulator (${device_serial}) to be ready..."
      adb -s "${device_serial}" wait-for-device
      timeout=120
      while [ $timeout -gt 0 ]; do
        boot_completed=$(adb -s "${device_serial}" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)
        if [ "$boot_completed" = "1" ]; then
          break
        fi
        sleep 2
        timeout=$((timeout-2))
      done

      echo "Building and installing Android app via Gradle"
      if [ -x "./gradlew" ]; then
        ./gradlew :apps:androidApp:installDebug -x test
      else
        gradle :apps:androidApp:installDebug -x test
      fi

      pkg=$(grep -oP 'applicationId\\s*=\\s*"\\K[^\"]+' apps/androidApp/build.gradle.kts || true)
      if [ -z "$pkg" ]; then
        pkg="dev.patteruel.forexconversion.android"
      fi
      echo "Launching Android app $pkg"
      adb -s "${device_serial}" shell monkey -p "$pkg" -c android.intent.category.LAUNCHER 1 || adb -s "${device_serial}" shell am start -n "$pkg/.MainActivity" || true
    else
      echo "Could not detect emulator serial; skipping Android install/run"
    fi
  fi
fi

echo ""
echo "All done. iOS Simulator should be open and app launched, and Android emulator should have the app installed and launched (if available)."
