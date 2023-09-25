val generateReadmeAndReleaseNotes by tasks.creating(CopyAndFilterReadme::class.java) {
    group = "release-relevant"
    description = "Copies the readme and release notes file into the root directory, replacing all placeholders"
    replaceTokens = mapOf(
        "project.version" to project.version.toString(),
        "project.groupId" to project.group.toString(),
        "github.user" to providers.gradleProperty("githubUser"),
    )
    sourceDir.set(project.projectDir)
    targetDir.set(project.rootDir)
}

tasks.beforeReleaseHook.configure { dependsOn(generateReadmeAndReleaseNotes) }


println("Version in ${project.name}: ${project.version}")
