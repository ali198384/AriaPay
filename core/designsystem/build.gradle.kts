plugins {
    alias(libs.plugins.ariapay.android.library)
    alias(libs.plugins.ariapay.android.library.compose)
}

android {
    namespace = "ir.neobank.ariapay.core.designsystem"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.window.core)
}
