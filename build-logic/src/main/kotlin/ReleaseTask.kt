import de.skuzzle.semantic.Version
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class ReleaseTask : DefaultTask() {

    @get:Inject
    abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val gitExtension: Property<GitExtension>

    @TaskAction
    fun release() {
        val releaseVersion = providerFactory.gradleProperty("releaseVersion").orNull
            ?: throw IllegalStateException("Can not release: No -PreleaseVersion=x.y.z parameter specified")

        val parseError = tryParseVersion(releaseVersion)
        if (parseError != null) {
            throw IllegalStateException("Can not release: $releaseVersion is not a valid semantic version: ${parseError.message}")
        }

        val branch = currentBranch()
        println("Releasing $releaseVersion from branch ${branch}")
    }

    fun status(): String {
        return git("status", "--porcelain")
    }

    fun currentBranch(): String {
        return git("rev-parse", "--abbrev-ref", "HEAD")
    }

    fun tryParseVersion(v: String): Exception? {
        try {
            Version.parseVersion(v)
            return null
        } catch (e: Exception) {
            return e
        }
    }

    fun git(vararg args: String): String {
        val fullArgs = listOf("git") + listOf(*args)
        val exec = providerFactory.exec { this.commandLine(fullArgs) }
        val output = exec.standardOutput.asText.get().trim()

        //println("\$ ${fullArgs.joinToString(" ")}: $output")
        return output
    }
}
