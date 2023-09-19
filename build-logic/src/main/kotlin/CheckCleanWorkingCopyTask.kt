import org.gradle.api.DefaultTask
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class CheckCleanWorkingCopyTask : AbstractReleaseStep() {

    @TaskAction
    fun finalizeRelease() {
        val status = status()
        if (status.isNotEmpty()) {
            throw IllegalStateException("Can not release: Working copy is not clean\n$status")
        }
    }
}
