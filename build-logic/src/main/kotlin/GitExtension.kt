import org.gradle.api.provider.Property

abstract class GitExtension {
    companion object {
        val NAME = "GitExtension"
    }

    abstract val latestRelease : Property<String>
    abstract val currentVersion: Property<String>
    abstract val currentBranch: Property<String>
    abstract val commitHash: Property<String>
    abstract val commitHashShort: Property<String>
    abstract val cleanWorkingCopy: Property<Boolean>
    abstract val unpushedCommits :Property<Boolean>

    override fun toString(): String {
        return "VersionExtension(" +
            "latestRelease=${latestRelease.orNull}, " +
            "currentVersion=${currentVersion.orNull}, " +
            "currentBranch=${currentBranch.orNull}, " +
            "commitHash=${commitHash.orNull}, " +
            "commitHashShort=${commitHashShort.orNull}, " +
            "cleanWorkingCopy=${cleanWorkingCopy.orNull}), "+
            "unpushedCommits=${unpushedCommits.orNull})"
    }

}
