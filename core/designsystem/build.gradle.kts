plugins {
    alias(libs.plugins.ariapay.android.library)
    alias(libs.plugins.ariapay.android.library.compose)
}

android {
    namespace = "ir.neobank.ariapay.core.designsystem"
}

dependencies {
    implementation(project(":core:common"))
}