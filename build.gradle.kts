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

githubRelease {
    token(provider { property("ghToken") as String? })
    owner.set(property("githubUser").toString())
    repo.set(property("githubRepo").toString())
    draft.set(true)
    dryRun.set(providers.environmentVariable("RELEASE_DRY_RUN").map { it == "true" })
    body(providers.fileContents(layout.projectDirectory.file("RELEASE_NOTES.md")).asText)
}
