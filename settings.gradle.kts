pluginManagement {
    includeBuild("build-logic")
    repositories {
        /*google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()*/

        maven { url = uri("https://maven.myket.ir/") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        /*google()
        mavenCentral()*/

        maven { url = uri("https://maven.myket.ir/") }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "AriaPay"
include(":app")
include(":core:common")
include(":core:model")
include(":core:session")
include(":core:testing")
include(":core:designsystem")
include(":core:database")
include(":core:datastore")
include(":core:security")
include(":feature:home")
include(":feature:auth:presentation")
include(":feature:auth:domain")
include(":feature:auth:data")
include(":feature:applock:presentation")
include(":feature:applock:domain")
include(":feature:applock:data")
include(":feature:profile:presentation")
include(":feature:profile:domain")
include(":feature:profile:data")
