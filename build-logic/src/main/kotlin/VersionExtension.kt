import de.skuzzle.semantic.Version
import org.gradle.api.provider.Property

abstract class VersionExtension {
    companion object {
        const val NAME = "VersionExtension"
    }

    abstract val latestReleaseVersion: Property<Version>
    abstract val developmentVersion: Property<Version>
    abstract val nextReleaseVersion: Property<Version>

    override fun toString(): String {
        return "$NAME(" +
            "latestReleaseVersion=${latestReleaseVersion.orNull}, " +
            "developmentVersion=${developmentVersion.orNull}, " +
            "nextReleaseVersion=${nextReleaseVersion.orNull})"
    }

}
