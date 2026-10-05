plugins {
    alias(libs.plugins.ariapay.jvm.library)
}

dependencies {
    api(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)
}
