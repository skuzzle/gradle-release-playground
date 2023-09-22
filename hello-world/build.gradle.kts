plugins {
    id("java-library")
    `maven-publish`
}

println("Version in ${project.name}: ${project.version}")

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
