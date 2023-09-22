import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import javax.inject.Inject


/*

abstract class CucumberCompanionExtension @Inject constructor(
    @Internal
    private val taskContainer: TaskContainer,
    @Internal
    private val projectLayout: ProjectLayout
) {
 */

abstract class AbstractReleaseStep() : DefaultTask() {

    @get:Inject
    abstract val providers: ProviderFactory

    @get:Input
    abstract val dryRun: Property<Boolean>
    @get:Input
    abstract val verbose: Property<Boolean>

    @get:Internal
    val git: Git

    init {
        dryRun.convention(false)
        verbose.convention(true)
        git = Git(providers, dryRun, verbose)
    }

}
