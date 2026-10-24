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
    implementation(projects.feature.auth)
    implementation(projects.feature.applock)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.datastore)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
}
