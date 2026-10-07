plugins {
    alias(libs.plugins.ariapay.android.feature)
}

android {
    namespace = "ir.neobank.ariapay.feature.auth"
}

dependencies {
    testImplementation(projects.core.testing)
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

}
