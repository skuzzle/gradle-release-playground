import org.gradle.api.DefaultTask
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class FinalizeReleaseTask : DefaultTask() {

    @get:Inject
    abstract val providerFactory: ProviderFactory

    @TaskAction
    fun finalizeRelease() {
        val currentBranch = currentBranch()
        if (currentBranch != "main") {
            throw IllegalStateException("Can not finalize release: expected to be on release branch but was: $currentBranch")
        }

        println("Pushing release commit to $currentBranch")
        git("push")
        println("Pushing release tag")
        git("push", "--tags")
        println("Pushing dev branch")
        git("checkout", "dev")
        git("push")
    }

    fun currentBranch(): String {
        return git("rev-parse", "--abbrev-ref", "HEAD")
    }

    fun git(vararg args: String): String {
        val fullArgs = listOf("git") + listOf(*args)
        val exec = providerFactory.exec { this.commandLine(fullArgs) }
        val output = exec.standardOutput.asText.get().trim()

        //println("\$ ${fullArgs.joinToString(" ")}: $output")
        return output
    }
}
