#!/bin/bash

set -e

GRADLE_FILE="packages/webCore/build.gradle.kts"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "📦 NPM Publish Script"
echo "===================="

# Get current version
CURRENT_VERSION=$(grep "version = " "$GRADLE_FILE" | sed 's/version = "//' | sed 's/".*//')
echo "Current version: $CURRENT_VERSION"

# Parse version components
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"

# Increment patch version
NEW_PATCH=$((PATCH + 1))
NEW_VERSION="$MAJOR.$MINOR.$NEW_PATCH"

echo "New version: $NEW_VERSION"
echo ""

# Update version in gradle file
echo "🔄 Updating version in $GRADLE_FILE..."
sed -i '' "s/version = \"$CURRENT_VERSION\"/version = \"$NEW_VERSION\"/" "$GRADLE_FILE"
echo "✅ Version updated"
echo ""

# Build the package
echo "🏗️  Building the package..."
./gradlew :packages:forex-web-sdk:build -x test --no-daemon > /dev/null 2>&1
echo "✅ Build successful"
echo ""

# Publish the package
echo "🚀 Publishing to NPM..."
./gradlew :packages:forex-web-sdk:publishJsPackageToNpmjsRegistry -x test --no-daemon

if [ $? -eq 0 ]; then
  echo ""
  echo "✅ Successfully published v$NEW_VERSION to NPM!"
  echo ""
  echo "Next steps:"
  echo "1. Update apps/web/package.json to use forex-web-sdk@^$NEW_VERSION"
  echo "2. Run: cd apps/web && npm install"
else
  echo ""
  echo "❌ Publishing failed. Check the error above."
  echo "Reverting version change..."
  sed -i '' "s/version = \"$NEW_VERSION\"/version = \"$CURRENT_VERSION\"/" "$GRADLE_FILE"
  exit 1
fi
