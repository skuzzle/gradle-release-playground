import org.gradle.api.tasks.TaskAction

abstract class FinalizeReleaseTask : AbstractReleaseStep() {

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
        println("Switching to dev branch")
        git("checkout", "dev")
        println("Pushing dev branch")
        git("push")
    }
}
