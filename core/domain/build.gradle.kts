plugins {
    alias(libs.plugins.ariapay.jvm.library)
}

dependencies {
    api(projects.core.model)
    implementation(projects.core.common)
    implementation(libs.kotlinx.coroutines.core)
}
