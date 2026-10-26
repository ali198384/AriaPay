plugins {
    alias(libs.plugins.ariapay.android.application)
    alias(libs.plugins.ariapay.hilt)
}

android {
    namespace = "ir.neobank.ariapay"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "ir.neobank.ariapay"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    /*buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }*/
}

dependencies {
    implementation(libs.androidx.fragment)
    implementation(projects.feature.home)
    implementation(projects.feature.auth.presentation)
    implementation(projects.feature.auth.data)
    implementation(projects.feature.applock.presentation)
    implementation(projects.feature.applock.data)
    implementation(projects.feature.profile.presentation)
    implementation(projects.feature.profile.data)
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.session)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
}
