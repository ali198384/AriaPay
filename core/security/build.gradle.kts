plugins {
    alias(libs.plugins.ariapay.android.library)
    alias(libs.plugins.ariapay.hilt)
}

android {
    namespace = "ir.neobank.ariapay.core.security"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.tink.android)

    androidTestImplementation(libs.androidx.junit)
    implementation(libs.kotlinx.coroutines.test)
}
