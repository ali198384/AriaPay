plugins {
    alias(libs.plugins.ariapay.android.library)
}

android {
    namespace = "ir.neobank.ariapay.core.testing"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.junit.jupiter.api)
}

