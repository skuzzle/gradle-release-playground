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

    @get:Input
    abstract val versionExtension: Property<VersionExtension>

    @TaskAction
    fun prepareRelease() {
        val gitExtension = gitExtension.get()
        val releaseVersion = versionExtension.flatMap { it.nextReleaseVersion }.get()
        val branch = gitExtension.currentBranch.get()
        //val releaseBranchName = "release-$releaseVersion"

        logger.info("Preparing release $releaseVersion from branch $branch")

        if (!gitExtension.cleanWorkingCopy.get()) {
            throw IllegalStateException("Can not release because working copy is not clean")
        }
    }

    fun git(vararg args: String): String {
        val fullArgs = listOf("git") + listOf(*args)
        val exec = providerFactory.exec { this.commandLine(fullArgs) }
        val output = exec.standardOutput.asText.get().trim()

        println("\$ ${fullArgs.joinToString(" ")}: $output")
        return output
    }
}
