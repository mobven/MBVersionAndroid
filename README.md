# MBVersionOverlay

[![](https://jitpack.io/v/yourusername/mb-version-overlay.svg)](https://jitpack.io/#yourusername/mb-version-overlay)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A lightweight Android library that displays app version information as an overlay on all activities. Perfect for QA testing, internal builds, and debug environments.

<p align="center">
  <img src="https://user-images.githubusercontent.com/placeholder/demo.gif" width="300" alt="MBVersionOverlay Demo">
</p>

## ✨ Features

- 🎯 **Zero Configuration** - Works with just one line of code
- 🎨 **Fully Customizable** - Colors, position, text size, and content
- 🖱️ **Drag & Drop Support** - Users can drag overlay to any position
- 📱 **No Permissions Required** - Uses standard Android APIs
- 🔧 **Build Variant Aware** - Easy to enable/disable based on build types
- 💾 **Lightweight** - Minimal performance impact (~8KB)
- 🧩 **Framework Compatible** - Works with Views, Compose, Fragments
- 🔄 **Lifecycle Aware** - Automatic cleanup, no memory leaks

## 🚀 Quick Start

### Installation

Add JitPack repository to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.mobven:MBVersionAndroid:Tag")
}
```

### Basic Usage

Add to your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Enable only for debug builds
        MBVersionOverlay.init(this, BuildConfig.DEBUG)
    }
}
```

Don't forget to register your Application class in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ... >
```

**That's it!** 🎉 Version overlay will automatically appear on all activities.

## 🎨 Customization

### Basic Configuration

Configure before calling `init()`:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        MBVersionOverlay.configure(
            backgroundColor = ContextCompat.getColor(this, R.color.overlay_bg),
            textColor = Color.WHITE,
            textSize = 14f,
            position = MBVersionOverlay.Position.TOP,
            bottomMargin = 60, // Custom margin from bottom
            isDraggable = true // Enable drag & drop
        )
        
        MBVersionOverlay.init(this, BuildConfig.DEBUG)
    }
}
```

### Configuration Options

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `customText` | `String?` | `null` | Custom text (overrides version info) |
| `backgroundColor` | `Int` | Blue (`#AA4444FF`) | Background color (ARGB) |
| `textColor` | `Int` | `Color.WHITE` | Text color |
| `textSize` | `Float` | `16f` | Text size in SP |
| `position` | `Position` | `BOTTOM` | `TOP` or `BOTTOM` |
| `bottomMargin` | `Int` | `32` | Bottom margin in DP |
| `isDraggable` | `Boolean` | `true` | Enable drag & drop |

## 🖱️ Drag & Drop Features

The overlay supports intuitive drag & drop functionality:

- **Drag to Move**: Long press and drag to reposition
- **Click to Hide**: Quick tap to show/hide overlay
- **Smart Snapping**: Automatically snaps to nearest screen edge
- **Boundary Protection**: Cannot be dragged outside screen bounds

```kotlin
// Enable drag & drop
MBVersionOverlay.configure(isDraggable = true)

// Disable drag & drop (click to hide/show only)
MBVersionOverlay.configure(isDraggable = false)
```

## 🎮 Manual Control

### Show/Hide on Specific Activities

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manually show overlay
        MBVersionOverlay.show(this, true)
        
        // Hide overlay
        MBVersionOverlay.show(this, false)
    }
}
```

## 🐛 Troubleshooting

### Overlay Not Appearing

1. Ensure `init()` is called in `Application.onCreate()`
2. Check if `enabled` parameter is `true`
3. Verify Application class is registered in AndroidManifest.xml

### Drag & Drop Issues

1. Make sure `isDraggable = true` in configuration
2. Test on physical device (emulator touch might be different)
3. Check if overlay is clickable (not behind other views)

### Bottom Navigation Overlap

```kotlin
// Increase bottom margin to avoid navigation bars
MBVersionOverlay.configure(
    bottomMargin = 100 // Adjust as needed
)
```

## ⚠️ Known Issues

### Edge-to-Edge Displays

**Issue**: On devices with edge-to-edge displays (like those using `enableEdgeToEdge()`), the drag & drop functionality may allow the overlay to be dragged outside the visible screen area, particularly near system bars.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m '[DEV][Add amazing feature]'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

```
MIT License

Copyright (c) 2025 Mobven

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🌟 Show Your Support

If this library helped you, please ⭐ star this repository and share it with others!

---

<p align="center">
  Made with ❤️ by <a href="https://mobven.com/">Mobven</a>
</p>
