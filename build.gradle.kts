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
    draft.set(true)
    token(providers.gradleProperty("ghToken"))
    owner.set(providers.gradleProperty("githubUser"))
    repo.set(providers.gradleProperty("githubRepo"))
    dryRun.set(providers.environmentVariable("RELEASE_DRY_RUN").map { it == "true" }.orElse(false))
    body.set(providers.fileContents(layout.projectDirectory.file("RELEASE_NOTES.md")).asText)
}
