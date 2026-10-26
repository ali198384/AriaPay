plugins {
    alias(libs.plugins.ariapay.android.feature)
}

android {
    namespace = "ir.neobank.ariapay.feature.profile.presentation"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.feature.profile.domain)
}
