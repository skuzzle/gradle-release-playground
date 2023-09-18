import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

abstract class ReleaseExtension {
    companion object {
        val NAME = "ReleaseExtension"
    }

    /** Regex of branch names that are considered to be release branches */
    abstract val releaseBranches: SetProperty<String>

    /** Whether release should fail when working copy is not clean */
    abstract val requireCleanWorkingCopy: Property<Boolean>

    abstract val incrementVersionPart : Property<VersionIncrement>
}
