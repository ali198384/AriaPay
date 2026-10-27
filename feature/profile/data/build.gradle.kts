plugins {
    alias(libs.plugins.ariapay.android.library)
    alias(libs.plugins.ariapay.hilt)
}

android {
    namespace = "ir.neobank.ariapay.feature.profile.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.session)
    implementation(projects.feature.profile.domain)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.room.runtime)
    androidTestImplementation(libs.truth)
}
