plugins {
    alias(libs.plugins.ariapay.android.library)
    alias(libs.plugins.ariapay.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ir.neobank.ariapay.core.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
