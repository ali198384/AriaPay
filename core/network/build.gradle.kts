plugins {
    alias(libs.plugins.ariapay.android.library)
}

android {
    namespace = "ir.neobank.ariapay.core.network"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.kotlinx.coroutines.core)
}
