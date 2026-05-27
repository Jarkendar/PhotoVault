plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.collections.immutable)
}
