plugins {
    alias(libs.plugins.ariapay.android.library)
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
