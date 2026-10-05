plugins {
    alias(libs.plugins.ariapay.android.library)
    alias(libs.plugins.ariapay.hilt)
}

android {
    namespace = "ir.neobank.ariapay.core.datastore"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
}
