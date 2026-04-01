pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()

    // ── SDK Resolution: LOCAL TESTING BRANCH ─────────────────────────
    // This branch resolves the SDK from Maven Local (~/.m2).
    // Publish first:  cd sg-circles-android-sdk && ./gradlew clean publishToMavenLocal
    //
    // ⚠️  DO NOT merge this branch into main/production.
    //     Switch to main branch for JitPack (remote) resolution.
    mavenLocal()  // ← ACTIVE: resolves SDK from ~/.m2
    maven(url = "https://www.jitpack.io")
    flatDir {
      dirs("$rootDir/app/libs")
    }

    val expoNodeModules = file("../circles-sg-rn-expo/node_modules")
    listOf(
      "expo-asset/local-maven-repo",
      "expo-file-system/local-maven-repo",
      "expo-font/local-maven-repo",
      "expo-keep-awake/local-maven-repo",
    ).forEach { relativeRepo ->
      maven(url = uri(expoNodeModules.resolve(relativeRepo)))
    }
  }
}

rootProject.name = "SGCirclesAndroidHost"
include(":app")
