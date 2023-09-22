import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class FinalizeReleaseTask: AbstractReleaseStep() {

    @get:[Input Optional]
    abstract val releaseDryRun: Property<Boolean>

    @TaskAction
    fun finalizeRelease() {
        if (releaseDryRun.getOrElse(false)) {
            println("Skipping finalize because releaseDryRun is true")
            return
        }
        val currentBranch = git.currentBranch()
        if (currentBranch != "main") {
            throw IllegalStateException("Can not finalize release: expected to be on release branch but was: $currentBranch")
        }

        println("Pushing release commit to $currentBranch")
        git.git("push", "origin", "main")
        println("Pushing release tag")
        git.git("push", "--tags")
        println("Switching to dev branch")
        git.git("checkout", "dev")
        println("Pushing dev branch")
        git.git("push", "origin", "dev")
    }
}
