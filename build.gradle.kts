
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.8.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("com.google.gms:google-services:4.4.1")
    }
}


allprojects {
    repositories {
    }
}