import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class PrepareReleaseTask : DefaultTask() {

    @get:Inject
    abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val gitExtension: Property<GitExtension>

    @TaskAction
    fun release() {
        val gitExtension = gitExtension.get()

        if (!gitExtension.cleanWorkingCopy.get()) {
            throw IllegalStateException("Can not release because working copy is not clean")
        } else if (gitExtension.unpushedCommits.get()) {
            throw IllegalStateException("Can not release because current branch is ahead of its upstream branch")
        }

        logger.info("Fetching latest changes")
        git("fetch", "--all")
        logger.info("Checking out main branch")
        git("checkout", "main")
        logger.info("Pulling latest changes")
        git("pull")
        git("checkout", "dev")
        git("pull")
        git("checkout", "main")

        val releaseVersion = gitExtension.currentVersion.get()
        val releaseBranchName = "release-$releaseVersion"
        logger.info("Creating release branch: $releaseBranchName")
        git("checkout", "-b", releaseBranchName)

        logger.info("Merging dev to release")
        git("merge", "dev")

    }

    fun git(vararg args: String): String {
        val fullArgs = listOf("git") + listOf(*args)
        val exec = providerFactory.exec { this.commandLine(fullArgs) }
        val output = exec.standardOutput.asText.get().trim()

        println("\$ ${fullArgs.joinToString(" ")}: $output")
        return output
    }
}
