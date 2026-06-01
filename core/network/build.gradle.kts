import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

// Read local.properties (gitignored) so real backend URLs stay out of VCS.
// Safe fallbacks are used when the file is absent (e.g. CI, fresh clone).
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
fun localProp(key: String, default: String): String = localProperties.getProperty(key) ?: default

android {
    namespace = "dev.jskrzypczak.photovault.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 31
        consumerProguardFiles("consumer-rules.pro")
        // BASE_URL must be set in local.properties (gitignored). The file is never committed.
        // Fallback to empty string — a missing local.properties means no real backend; build still compiles.
        buildConfigField("String", "BASE_URL",
            "\"${localProp("BASE_URL_DEBUG", "")}\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL",
                "\"${localProp("BASE_URL_DEBUG", "")}\"")
        }
        release {
            buildConfigField("String", "BASE_URL",
                "\"${localProp("BASE_URL_RELEASE", "")}\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.koin.core)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.turbine)
}