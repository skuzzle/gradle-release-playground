import de.skuzzle.semantic.Version
import org.gradle.api.tasks.TaskAction

abstract class ReleaseInternalTask : AbstractReleaseStep() {

    @Throws(IllegalStateException::class)
    @TaskAction
    fun release() {
        val releaseVersion = providers.gradleProperty("releaseVersion").orNull
            ?: throw IllegalStateException("Can not release: No -PreleaseVersion=x.y.z parameter specified")

        val parseError = tryParseVersion(releaseVersion)
        if (parseError != null) {
            throw IllegalStateException("Can not release: $releaseVersion is not a valid semantic version: ${parseError.message}")
        }

        val branch = git.currentBranch()
        println("Releasing $releaseVersion from branch $branch")

        println("Adding files to git:\n${git.status()}")
        git.git("add", ".")

        println("Creating release commit & tag")
        git.git("commit", "-m", "Release $releaseVersion")
        git.git("tag", "-a", "v${releaseVersion}", "-m", "Release $releaseVersion")

        println("Merging release into main branch")
        git.git("checkout", "main")
        git.git("merge", "v${releaseVersion}", "--strategy-option", "theirs")

        // TODO revisit output formatting if debug mode
        println("Status after merge")
        println(git.git("status"))
    }

    fun tryParseVersion(v: String): Exception? {
        try {
            Version.parseVersion(v)
            return null
        } catch (e: Exception) {
            return e
        }
    }
}
