## Publishing NPM Package

To publish the NPM package, follow these steps:
```bash
# Step 1: Build the package
./gradlew :webCore:assembleJsPackage
# Step 2: Navigate to the package directory
cd packages/webCore/build/packages/js/
# Step 3: Publish the package to NPM
npm publish --access public

# Error Handling:

# IF you encounter an error related to version, make sure to update the version from the gradle file
# Check the file at `packages/webCore/build.gradle.kts` and increment the third number by one and try republishing again with the same steps.
```