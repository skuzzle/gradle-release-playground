import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Not worth caching")
abstract class ReleaseHookTask : DefaultTask() {

    abstract val alsoDependsOn: Property<Task>

    override fun dependsOn(vararg paths: Any?): Task {
        super.dependsOn(*paths)
        alsoDependsOn.orNull?.dependsOn(*paths)
        return this
    }
}
