plugins {
    alias(libs.plugins.ariapay.android.library)
    alias(libs.plugins.ariapay.hilt)
}

android {
    namespace = "ir.neobank.ariapay.feature.applock.data"
}

dependencies {
    implementation(projects.core.datastore)
    implementation(projects.core.security)
    implementation(projects.feature.applock.domain)
    implementation(libs.androidx.datastore.preferences)
}
