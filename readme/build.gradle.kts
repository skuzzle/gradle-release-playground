import org.apache.tools.ant.filters.ReplaceTokens

val generateReadmeAndReleaseNotes by tasks.creating(DefaultTask::class.java) {
    notCompatibleWithConfigurationCache("Not yet")
    group = "release-relevant"
    description = "Copies the readme and release notes file into the root directory, replacing all placeholders"

    doLast {
        copy {
            from(project.projectDir) {
                include("*.md")
            }
            into(project.rootDir)
            filter(
                ReplaceTokens::class, "tokens" to mapOf(
                    "project.version" to project.version as String,
                    "project.groupId" to project.group as String,
                    "github.user" to "skuzzle",
                )
            )
        }
    }
}


println("Version in ${project.name}: ${project.version}")
