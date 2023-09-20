import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class FinalizeReleaseTask : AbstractReleaseStep() {

    @get:[Input Optional]
    abstract val releaseDryRun: Property<Boolean>

    @TaskAction
    fun finalizeRelease() {
        if (releaseDryRun.getOrElse(false)) {
            println("Skipping finalize because releaseDryRun is true")
            return
        }
        val currentBranch = currentBranch()
        if (currentBranch != "main") {
            throw IllegalStateException("Can not finalize release: expected to be on release branch but was: $currentBranch")
        }

        println("Pushing release commit to $currentBranch")
        git("push")
        println("Pushing release tag")
        git("push", "--tags")
        println("Switching to dev branch")
        git("checkout", "dev")
        println("Pushing dev branch")
        git("push")
    }
}
