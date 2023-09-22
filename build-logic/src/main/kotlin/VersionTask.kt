import org.gradle.api.DefaultTask
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class VersionTask : DefaultTask() {
    @get:Inject
    abstract val providers: ProviderFactory

    @TaskAction
    fun printVersion() {
        println(providers.gradleProperty("version").orElse("unknown").get())
    }
}
