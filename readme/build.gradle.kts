import org.jetbrains.kotlin.gradle.plugin.extraProperties

val generateReadmeAndReleaseNotes by tasks.creating(CopyAndFilterReadme::class.java) {
    group = "release-relevant"
    description = "Copies the readme and release notes file into the root directory, replacing all placeholders"
    replaceTokens = mapOf(
        "project.version" to project.version.toString(),
        "project.groupId" to project.group.toString(),
        "github.user" to providers.gradleProperty("githubUser"),
    )
    extra.set("releaseRelevant", true)
    sourceDir.set(project.projectDir)
    targetDir.set(project.rootDir)
}


println("Version in ${project.name}: ${project.version}")
