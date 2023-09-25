
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

    /*copySpec.set{
        from(project.projectDir) {
            include("*.md")
        }
        into(project.rootDir)
        filter(
            ReplaceTokens::class, "tokens" to mapOf(
                "project.version" to providers.gradleProperty("version"),
                "project.groupId" to providers.gradleProperty("group"),
                "github.user" to "skuzzle",
            )
        )
    }*/
}

println("Version in ${project.name}: ${project.version}")
