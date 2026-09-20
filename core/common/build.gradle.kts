plugins {
    alias(libs.plugins.ariapay.android.library)
}

android {
    namespace = "ir.neobank.ariapay.core.common"
}

dependencies {
    implementation(projects.core.model)
}
