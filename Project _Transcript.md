# Keyboard Theme Maker & APK Builder

## Overview

This project is a **Keyboard Theme Maker App** that allows users to design custom keyboard themes for a specific keyboard (e.g., Better Keyboard). Users can customize colors, key styles, and choose to use either XML shapes or images for key backgrounds. The app generates all necessary XML resource files and assets, packages them into a ZIP file, and integrates with a template Android project. Finally, an automated GitHub Actions workflow builds the APK for the user.

---

## Features

### Theme Customization
- **Theme Name:** Users enter a name for their custom theme.
- **Color Pickers:** Select various colors including:
  - Text color
  - Shadow color
  - Suggestion color
- **Key Style Editor:** Customize key backgrounds with the option to:
  - Use XML shapes (with gradients, strokes, rounded corners) **or**
  - Use images for key backgrounds (normal, pressed, etc.)
- **Live Preview:** A real-time preview displays how the key will look based on the current selections.

### File Generation & Export
- **Dynamic XML Generation:** The app generates:
  - `colors.xml` (reflecting user-chosen colors)
  - `btn_keyboard_key.xml` (and other selector files for different key states)
  - `strings.xml` (containing the theme’s name)
  - `AndroidManifest.xml` template with required intent filters (e.g., `com.betterandroid.betterkeyboard.skins`)
- **Asset Organization:** All files and assets are organized into the correct folder structure (e.g., `res/values/`, `res/drawable/`).
- **ZIP Export:** Users can export the complete theme files and assets as a ZIP package.

### Integration with Template Android Project
- A ready-made **template Android Studio project** is provided, which includes:
  - A complete folder structure with the base `AndroidManifest.xml`, `Main.java`, `res/`, and Gradle build files.
- Users replace the template’s resource folders with their customized theme files.

### Automated APK Build with GitHub Actions
- **Continuous Integration:** When the theme files are pushed to a GitHub repository:
  - A GitHub Actions workflow automatically checks out the project.
  - It sets up the Java Development Kit.
  - Gradle builds the APK (using `./gradlew assembleRelease`).
  - The generated APK is uploaded as an artifact for easy download.

---

## GitHub Actions Workflow

A sample workflow file (`.github/workflows/build_apk.yml`) is provided in the repository:

```yaml
name: Build APK

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Setup JDK
        uses: actions/setup-java@v3
        with:
          java-version: '11'

      - name: Make gradlew executable
        run: chmod +x ./gradlew

      - name: Build APK
        run: ./gradlew assembleRelease

      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: Theme-APK
          path: app/build/outputs/apk/release/app-release.apk
```

---

## Development Roadmap

1. **Develop the Android App (Theme Maker):**
   - Build the UI with color pickers, key style options (XML shapes or images), sliders for border radius, and live preview.
   - Implement functionality to generate all necessary XML files (e.g., `colors.xml`, key selectors) and manage image assets.
   - Package the generated files and assets into a ZIP file.

2. **Create and Distribute the Template Android Project:**
   - Provide a ready-made Android Studio project with the necessary folder structure.
   - Instruct users to replace the template’s resource folders with their customized theme files.

3. **Automate APK Building via GitHub Actions:**
   - Set up a GitHub repository containing the template project.
   - Configure the GitHub Actions workflow to build the APK automatically on push.
   - Users download the final APK from the GitHub Actions artifacts.

4. **Optional Enhancements:**
   - Integrate GitHub API into the app for automatic upload of theme files and triggering the build.
   - Expand customization options within the app for an enhanced user experience.

---

## Usage

1. **Customize Theme:**
   - Open the app and enter a theme name.
   - Use the color pickers and key style editor to design your keyboard theme.
   - Preview your changes in real time.

2. **Generate Files:**
   - Click the "Generate Theme Files" button to create the XML files and package assets.
   - Export the package as a ZIP file.

3. **Build the APK:**
   - Replace the resource folders in the provided template Android project with your customized files.
   - Push the project to your GitHub repository.
   - GitHub Actions will build the APK automatically.
   - Download the APK from the Actions artifacts.

4. **Deploy and Enjoy:**
   - Install your newly built keyboard theme APK on your device.

---

## License

This project is open-source and available under the [MIT License](LICENSE).

---
