plugins {
    alias(libs.plugins.ariapay.jvm.library)
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.kotlinx.coroutines.core)
}
