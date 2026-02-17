## Updating Version Catalog File

When Updating the `libs.versions.toml` file, you should follow these guidelines:
1. Make sure to check for existing entries to avoid duplicates and conflicts.
2. Use clear and descriptive variable names for both versions and plugins to maintain readability.
3. Ensure that the version numbers are accurate and follow semantic versioning where applicable.

## Adding new package to a module

To add a new package to a module, you can follow these steps:
1. Update `libs.versions.toml` to include the new package and its version.
2. Update the `build.gradle` file of the module to include the new package as a dependency.
    - When adding a new implementation dependency, use `libs.<package-name>` to reference the version defined in `libs.versions.toml`.
3. Sync the project to ensure that the new dependency is properly added.

## Adding new plugin to a module

To add a new plugin to a module, you can follow these steps:
1. Update `libs.versions.toml` to include the new plugin and its version.
   - Version Format: `<pluginVersionVariable> = <pluginVersion>`
   - Plugin format: `<pluginNameVariable> = { id = <plugin.id>, version.ref = <versionRef> }"`.
2. Update the `build.gradle` file of the module to apply the new plugin.
   - Inside the `plugins {}` block, insert `alias(libs.plugins.<pluginNameVariable>)` to apply the plugin using the alias defined in `libs.versions.toml`.

## Do not use Java imports under commonMain

When working within the `commonMain` source set, avoid using Java imports as they are not compatible with all platforms. Instead, use Kotlin's multiplatform capabilities to ensure that your code can run on various platforms without issues. This approach promotes better code sharing and maintainability across different target environments.
If there's a need to use platform-specific code, consider using `expect` and `actual` declarations to provide platform-specific implementations while keeping the common code clean and platform-agnostic.

## When updating KMP

### If focused on iOS

When doing commonMain and iosMain updates, make sure to run `linkDebugFrameworkIosArm64` on the affected module to ensure that the changes are properly linked and tested on iOS devices.
If there are any build issues or errors, please be smart enough to debug and fix them before finalizing the changes.

### If focused on Android

When doing commonMain and androidMain updates, make sure to run `assembleDebug` on the affected module to ensure that the changes are properly built and tested on Android devices.
If there are any build issues or errors, please be smart enough to debug and fix them before

### If working on both iOS and Android

When working on both iOS and Android, it's crucial to ensure that your changes are compatible with both platforms. After making updates to `commonMain`, run the appropriate build commands for both platforms:
- For iOS: `linkDebugFrameworkIosArm64`
- For Android: `assembleDebug`

## Module-level Agentic Actions

When doing module-level agentic actions, refer to individual module's AGENT.md:
- webCore: `packages/webCore/AGENT.md`