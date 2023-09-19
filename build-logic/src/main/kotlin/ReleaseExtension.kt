import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

abstract class ReleaseExtension {
    companion object {
        const val NAME = "ReleaseExtension"
    }

    abstract val releaseBranches : SetProperty<String>
    abstract val releaseRequested : Property<Boolean>

    abstract val incrementVersionPart : Property<VersionIncrement>
    override fun toString(): String {
        return "ReleaseExtension(" +
            "releaseBranches=${releaseBranches.orNull}, " +
            "releaseRequested=${releaseRequested.orNull}, " +
            "incrementVersionPart=${incrementVersionPart.orNull})"
    }


}
