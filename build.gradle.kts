plugins {
    id("release-conventions")
}

println("Version in ${project.name}: ${project.version}")

release {
    releaseNotesContent.set(providers.fileContents(layout.projectDirectory.file("RELEASE_NOTES.md")).asText)
}
