plugins {
    alias(libs.plugins.ariapay.jvm.library)
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)
    api(libs.kotlinx.coroutines.core)
}
