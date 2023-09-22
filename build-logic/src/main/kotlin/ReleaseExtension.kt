import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class ReleaseExtension {
    companion object {
        const val NAME = "release"
    }

    @get:Input
    abstract val dryRun: Property<Boolean>

    @get:Input
    abstract val verbose: Property<Boolean>

    @get:Input
    abstract val mainBranch: Property<String>

    @get:Input
    abstract val devBranch: Property<String>

    fun wireUp(releaseStep: AbstractReleaseStep) {
        val extension = this
        releaseStep.apply {
            this.dryRun.set(extension.dryRun)
            this.verbose.set(extension.verbose)
            this.mainBranch.set(extension.mainBranch)
            this.devBranch.set(extension.devBranch)
        }
    }
}
