plugins {
    id("release-conventions")
}

println("Version in ${project.name}: ${project.version}")

tasks.beforeReleaseHook.configure {
    dependsOn(":readme:generateReadmeAndReleaseNotes", ":hello-world:publishToMavenLocal")
}

release {
    releaseNotesContent.set(providers.fileContents(layout.projectDirectory.file("RELEASE_NOTES.md")).asText)
}
