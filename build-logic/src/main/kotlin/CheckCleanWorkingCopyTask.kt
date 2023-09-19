import org.gradle.api.DefaultTask
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class CheckCleanWorkingCopyTask : DefaultTask() {

    @get:Inject
    abstract val providerFactory: ProviderFactory

    @TaskAction
    fun finalizeRelease() {
        val status = git("status", "--porcelain")
        if (status.isNotEmpty()) {
            throw IllegalStateException("Can not release: Working copy is not clean\n$status")
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
