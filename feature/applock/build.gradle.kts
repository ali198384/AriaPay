plugins {
    alias(libs.plugins.ariapay.android.feature)
}

android {
    namespace = "ir.neobank.ariapay.feature.applock"
}

dependencies {
    implementation(projects.core.datastore)
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.security)

    implementation(libs.androidx.biometric)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
