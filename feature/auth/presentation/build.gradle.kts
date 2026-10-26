plugins {
    alias(libs.plugins.ariapay.android.feature)
}

android {
    namespace = "ir.neobank.ariapay.feature.auth.presentation"
}

dependencies {
    testImplementation(projects.core.testing)
    implementation(projects.feature.auth.domain)
    implementation(projects.core.designsystem)
}
