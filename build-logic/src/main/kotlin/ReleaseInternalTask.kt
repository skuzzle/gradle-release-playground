import de.skuzzle.semantic.Version
import org.gradle.api.tasks.TaskAction

abstract class ReleaseInternalTask : AbstractReleaseStep() {

    @TaskAction
    fun release() {
        val releaseVersion = providerFactory.gradleProperty("releaseVersion").orNull
            ?: throw IllegalStateException("Can not release: No -PreleaseVersion=x.y.z parameter specified")

        val parseError = tryParseVersion(releaseVersion)
        if (parseError != null) {
            throw IllegalStateException("Can not release: $releaseVersion is not a valid semantic version: ${parseError.message}")
        }

        val branch = currentBranch()
        println("Releasing $releaseVersion from branch $branch")

        println("Adding files to git:\n${status()}")
        git("add", ".")

        println("Creating release commit & tag")
        git("commit", "-m", "Release $releaseVersion")
        git("tag", "-a", "v${releaseVersion}", "-m", "Release $releaseVersion")

        println("Merging release into main branch")
        git("checkout", "main")
        git("merge", "v${releaseVersion}", "--strategy-option", "theirs")

        println("Status after merge")
        println(git("status"))
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
