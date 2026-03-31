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
            isDefault = true
        }
        create("dev") {
            dimension = "environment"
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
    kapt(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.androidx.media3.exoplayer)
    testImplementation(kotlin("test"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}