plugins {
    alias(libs.plugins.ariapay.android.library)
}

android {
    namespace = "ir.neobank.ariapay.core.domain"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}