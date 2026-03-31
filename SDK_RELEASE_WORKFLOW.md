# Circles Travel Pass SDK — Release & Verification Workflow

## Architecture Overview

```
┌──────────────────────────────────┐
│  circles-sg-rn-expo              │  React Native (Expo) source
│  (shared RN code)                │  Generates the JS bundle + AAR
└──────────────┬───────────────────┘
               │  produces AAR artifact
               ▼
┌──────────────────────────────────┐
│  sg-circles-android-sdk          │  SDK distribution repo (JitPack)
│  build.gradle + AAR + POM        │  Published as Maven artifact
└──────────────┬───────────────────┘
               │  consumed via Gradle dependency
               ▼
┌──────────────────────────────────┐
│  sg-circles-android-host         │  Consumer / Host app
│  (or any other Android app)      │  implementation("com.github...")
└──────────────────────────────────┘
```

---

## Branch Strategy

### SDK Repo (`sg-circles-android-sdk`)

| Branch | Purpose |
|--------|---------|
| `main` | Production-ready. Tagged commits trigger JitPack builds. |

### Host App (`sg-circles-android-host`)

| Branch | Purpose |
|--------|---------|
| `main` | **Production** — resolves SDK from **JitPack** (remote). `mavenLocal()` is commented out. |
| `local-sdk-testing` | **Local verification** — resolves SDK from **Maven Local** (`~/.m2`). `mavenLocal()` is uncommented. |

> [!CAUTION]
> Never merge `local-sdk-testing` into `main`. It exists only for local dev verification.

---

## Step-by-Step Release Process

### Phase 1: Build & Publish SDK Locally

```bash
cd sg-circles-android-sdk

# 1. Make your changes to build.gradle / AAR / POM
# 2. Publish to Maven Local for testing
./gradlew clean publishToMavenLocal \
  -Pgroup=com.github.iniyanmurugavel \
  -Pversion=<NEW_VERSION>
```

This writes the artifact to:
```
~/.m2/repository/com/github/iniyanmurugavel/sg-circles-android-sdk/<VERSION>/
```

### Phase 2: Verify with Host App (Local Branch)

```bash
cd sg-circles-android-host

# Switch to the local testing branch
git checkout local-sdk-testing

# Update the SDK version in app/build.gradle.kts
# implementation("com.github.iniyanmurugavel:sg-circles-android-sdk:<NEW_VERSION>")

# Build and run on device/emulator
./gradlew assembleDebug
# or run directly from Android Studio
```

**What to verify:**
- App compiles without dependency errors
- SDK activity launches correctly
- Full user flow works end-to-end (bundle loads, UI renders, navigation works)
- No crashes in logcat

### Phase 3: Deploy to JitPack (Remote)

Only after Phase 2 passes:

```bash
cd sg-circles-android-sdk

# Commit, tag, and push
git add .
git commit -m "SDK <VERSION> Release"
git tag <VERSION>
git push origin main
git push origin <VERSION>
```

Then trigger JitPack:
1. Go to `https://jitpack.io/#iniyanmurugavel/sg-circles-android-sdk/<VERSION>`
2. Click **"Get it"**
3. Wait for green ✅ build status

### Phase 4: Verify Remote Resolution

```bash
cd sg-circles-android-host

# Switch back to production branch
git checkout main

# Update the SDK version in app/build.gradle.kts
# implementation("com.github.iniyanmurugavel:sg-circles-android-sdk:<VERSION>")

# Clean build (forces remote fetch)
./gradlew clean assembleDebug --refresh-dependencies
```

---

## Lesson Learned: Always Verify Locally First

### The Mistake

We pushed a tagged release (`1.0.1`) to JitPack **without running a local build first**. The JitPack build failed because:

```
Execution failed for task ':extractDebugAnnotations'.
> Could not resolve all files for configuration ':detachedConfiguration1'.
  > Cannot resolve external dependency com.android.tools.lint:lint-gradle:31.3.2
    because no repositories are defined.
```

**Root cause:** `build.gradle` had repositories only inside `buildscript {}`, which scopes them to the build classpath — not to project dependency resolution. The Android Gradle Plugin's `extractDebugAnnotations` task needs `lint-gradle` at configuration time, and it couldn't find it.

**Fix:** Added an `allprojects { repositories { google(); mavenCentral() } }` block.

### The Right Way

```
NEVER:  Code change → git tag → push → hope JitPack works
ALWAYS: Code change → publishToMavenLocal → test with host app → git tag → push
```

---

## Quick Reference

### Publish locally
```bash
cd sg-circles-android-sdk
./gradlew clean publishToMavenLocal -Pgroup=com.github.iniyanmurugavel -Pversion=X.Y.Z
```

### Switch host app to local resolution
```bash
cd sg-circles-android-host
git checkout local-sdk-testing
```

### Switch host app back to remote (JitPack)
```bash
cd sg-circles-android-host
git checkout main
```

### Check what's in Maven Local
```bash
ls ~/.m2/repository/com/github/iniyanmurugavel/sg-circles-android-sdk/
```

### Clean Maven Local cache (if stale)
```bash
rm -rf ~/.m2/repository/com/github/iniyanmurugavel/sg-circles-android-sdk/
```
