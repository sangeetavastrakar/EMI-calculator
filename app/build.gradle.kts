plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
}

// true only when building an AAB (Android App Bundle for the Play Store).
// The Pro "test unlock" in ProConfig is forced OFF for such builds.
val isBundleBuild = gradle.startParameter.taskNames.any { it.contains("bundle", ignoreCase = true) }

android {
  namespace = "com.aksh.emicalculator"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aksh.emicalculator"
    // Android 7.0 (API 24) ... Android 16 (API 36)
    minSdk = 24
    targetSdk = 36
    versionCode = 8
    versionName = "6"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables.useSupportLibrary = true
    buildConfigField("boolean", "IS_BUNDLE_BUILD", isBundleBuild.toString())
  }

  signingConfigs {
    // Release signing is used only when a keystore is provided (env vars / my-upload-key.jks).
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
  }

  buildTypes {
    release {
      // R8 shrinking + obfuscation ON: app ka code/classes ab obfuscate hote hain
      // (hack/reverse-engineer karna mushkil), aur unused code/resources hat jaate
      // hain (chhota APK/AAB). Crash se bachne ke liye saari zaroori keep rules
      // proguard-rules.pro me di gayi hain (ViewModel reflection, Billing, AdMob,
      // Compose, enums waghera) — isliye isko bina samjhe hata mat dena.
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      val ks = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      signingConfig =
        if (file(ks).exists()) signingConfigs.getByName("release")
        else signingConfigs.getByName("debug") // lets `assembleRelease` work without a keystore
    }
    // debug uses the standard Android debug keystore automatically
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Only what the app really uses -> smaller APK and faster cold start
// (Firebase, Room, Retrofit, OkHttp, Moshi were unused and slowed startup; removed).
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.play.services.ads)
  implementation(libs.billing)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)

  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
