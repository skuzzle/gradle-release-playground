import de.skuzzle.semantic.Version
import org.gradle.api.provider.Property

abstract class GitExtension {
    companion object {
        const val NAME = "GitExtension"
    }

    abstract val latestReleaseTag : Property<String>
    abstract val currentBranch: Property<String>
    abstract val commitHash: Property<String>
    abstract val commitHashShort: Property<String>
    abstract val cleanWorkingCopy: Property<Boolean>
    abstract val unpushedCommits :Property<Boolean>

    override fun toString(): String {
        return "$NAME(" +
            "latestReleaseTag=${latestReleaseTag.orNull}, " +
            "currentBranch=${currentBranch.orNull}, " +
            "commitHash=${commitHash.orNull}, " +
            "commitHashShort=${commitHashShort.orNull}, " +
            "cleanWorkingCopy=${cleanWorkingCopy.orNull}, "+
            "unpushedCommits=${unpushedCommits.orNull})"
    }

}
