plugins {
    alias(libs.plugins.ariapay.android.library)
    alias(libs.plugins.ariapay.hilt)
}

android {
    namespace = "ir.neobank.ariapay.core.data"
}

dependencies {
    implementation(projects.core.datastore)
    implementation(projects.core.network)
    implementation(projects.core.domain)
}
