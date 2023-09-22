plugins {
    id("java-library")
    `maven-publish`
    id("before-release-conventions")
}

println("Version in ${project.name}: ${project.version}")

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
