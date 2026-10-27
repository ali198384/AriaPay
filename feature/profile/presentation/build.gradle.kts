plugins {
    alias(libs.plugins.ariapay.android.feature)
}

android {
    namespace = "ir.neobank.ariapay.feature.profile.presentation"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.session)
    implementation(projects.feature.profile.domain)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.compose)
}
