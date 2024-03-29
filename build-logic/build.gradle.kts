import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(libs.semanticVersion)
    implementation(libs.gradle.spotless)
    implementation(libs.gradle.commonCustomUserData)
    implementation(libs.gradle.enterprise)
    implementation(libs.githubRelease)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release.set(11)
    }
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
            allWarningsAsErrors = true
        }
    }
}
