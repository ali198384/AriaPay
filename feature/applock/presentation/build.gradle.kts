plugins {
    alias(libs.plugins.ariapay.android.feature)
}

android {
    namespace = "ir.neobank.ariapay.feature.applock.presentation"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.session)
    implementation(projects.core.security)
    implementation(projects.feature.applock.domain)

    implementation(libs.androidx.biometric)
    implementation(libs.androidx.compose.material.icons.extended)
    //implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
