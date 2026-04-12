plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    id("androidx.room")
}

android {
    namespace = "com.joshgm3z.triplerocktv.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 34
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val versionOverride = project.findProperty("versionNameOverride") as? String
        // Add a custom field to the core module's BuildConfig
        buildConfigField("String", "VERSION_NAME", "\"${versionOverride ?: "1.0-core-default"}\"")
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
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("demo") {
            dimension = "environment"
        }
        create("online") {
            dimension = "environment"
        }
        create("dev") {
            dimension = "environment"
            isDefault = true
        }
    }

    sourceSets {
        getByName("dev") {
            java.srcDirs("src/online/kotlin")
        }
    }

    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.buildType == "release" &&
                listOf("dev", "demo").contains(variantBuilder.flavorName)
            ) variantBuilder.enable = false
        }
    }
}

dependencies {
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.hilt.android)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.media3.exoplayer)

    kapt(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.zxing.android.embedded)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    testImplementation(kotlin("test"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}