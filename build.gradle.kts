// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.kapt) apply false

    // Linha corrigida para o KSP
    id("com.google.devtools.ksp") version "2.2.21-1.0.20" apply false

    id("com.google.dagger.hilt.android") version "2.57.2" apply false // ATENÇÃO AQUI, veja nota abaixo
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("io.sentry.android.gradle") version "5.12.2" apply false
}
