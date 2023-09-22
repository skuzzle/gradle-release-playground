plugins {
    id("com.github.breadmoirai.github-release") version "2.4.1"
    id("release-conventions")
}

println("Version in ${project.name}: ${project.version}")

tasks.named("beforeReleaseHook").configure {
    dependsOn(":readme:generateReadmeAndReleaseNotes", ":hello-world:publishToMavenLocal")
}

tasks.named("afterReleaseHook").configure {
    dependsOn(":githubRelease")
}

release {
    releaseNotesContent.set(providers.fileContents(layout.projectDirectory.file("RELEASE_NOTES.md")).asText)
}
