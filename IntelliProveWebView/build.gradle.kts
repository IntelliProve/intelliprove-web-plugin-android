plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val sdkVersion = "1.1.1" // 👈 SDK version

android {
    namespace = "com.intelliprove.webview"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        version = sdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Change output .aar filename
    libraryVariants.all {
        outputs.all {
            val variantOutput = this as com.android.build.gradle.internal.api.LibraryVariantOutputImpl
            variantOutput.outputFileName = "intelliprove-webview-sdk-$sdkVersion-${name}.aar"
        }
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.10.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
