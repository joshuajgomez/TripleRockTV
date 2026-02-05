import org.gradle.kotlin.dsl.dependencies

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    id("androidx.room") version "2.8.4" apply false
}

buildscript {
    dependencies {
        classpath(libs.androidx.navigation.safeargs.plugin)
    }
}