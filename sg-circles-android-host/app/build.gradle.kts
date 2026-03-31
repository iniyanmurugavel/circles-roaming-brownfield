import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("plugin.compose")
}

android {
  namespace = "com.circles.travelpass.host"
  compileSdk = 36
  ndkVersion = "29.0.13599879"

  defaultConfig {
    applicationId = "com.circles.travelpass.host"
    minSdk = 27
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    compose = true
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}

dependencies {
  // The remote JitPack SDK brings its Maven metadata (POM) with it,
  // so Gradle will automatically resolve Expo and React Native transitively.
  implementation("com.github.iniyanmurugavel:sg-circles-android-sdk:1.0.2")

  implementation("host.exp.exponent:expo.core:55.0.9")
  implementation("com.facebook.react:react-android:0.83.4")
  implementation("com.facebook.hermes:hermes-android:0.14.1")

  implementation("androidx.activity:activity:1.13.0")
  implementation("androidx.activity:activity-compose:1.13.0")
  implementation("com.google.android.material:material:1.13.0")

  implementation(platform("androidx.compose:compose-bom:2026.03.01"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material:material-icons-core")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
}
