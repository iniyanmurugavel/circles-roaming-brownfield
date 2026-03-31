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

    // ── SDK Resolution Toggle ──────────────────────────────────────────
    // LOCAL TESTING:  Uncomment mavenLocal() to test a locally-published
    //   SDK snapshot before pushing to JitPack.
    //   Publish first:  cd sg-circles-android-sdk && ./gradlew clean publishToMavenLocal
    // PRODUCTION:      Comment out mavenLocal() and use JitPack only.
    //   Never ship a build that resolves from mavenLocal().
    // mavenLocal()  // ← uncomment for local SDK verification only
    maven(url = "https://www.jitpack.io")  // ← production remote resolution
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
