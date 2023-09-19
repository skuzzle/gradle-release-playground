import org.gradle.api.DefaultTask
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class AbstractReleaseStep : DefaultTask() {

    @get:Inject
    abstract val providerFactory: ProviderFactory

    fun status(): String {
        return git("status", "--porcelain")
    }

    fun currentBranch(): String {
        return git("rev-parse", "--abbrev-ref", "HEAD")
    }

    fun git(vararg args: String): String {
        val fullArgs = listOf("git") + listOf(*args)
        val command = "\$ ${fullArgs.joinToString(" ")}"

        val exec = providerFactory.exec {
            this.commandLine(fullArgs)
            this.isIgnoreExitValue = true
        }
        val result = exec.result
            .orNull ?: throw IllegalStateException("No result while running: $command")

        if (result.exitValue != 0) {
            throw IllegalStateException("Exit value ${result.exitValue} while running $command: ${exec.standardError.asText.get().trim()}")
        }
        val output = exec.standardOutput.asText.get().trim()

        println("$command: $output")
        return output
    }
}
