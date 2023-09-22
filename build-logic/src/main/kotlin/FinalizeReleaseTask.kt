import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class FinalizeReleaseTask: AbstractReleaseStep() {

    @TaskAction
    fun finalizeRelease() {
        if (dryRun.get()) {
            print("Skipping finalize because releaseDryRun is true")
            return
        }
        val currentBranch = git.currentBranch()
        val mainBranch = mainBranch.get()
        val devBranch = devBranch.get()
        if (currentBranch != mainBranch) {
            throw IllegalStateException("Can not finalize release: expected to be on release branch but was: $currentBranch")
        }

        println("Pushing release commit to $currentBranch")
        git.git("push", "origin", mainBranch)
        println("Pushing release tag")
        git.git("push", "--tags")
        println("Switching to dev branch")
        git.git("checkout", devBranch)
        println("Pushing dev branch")
        git.git("push", "origin", devBranch)
    }
}
