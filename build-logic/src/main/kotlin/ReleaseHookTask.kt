import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Not worth caching")
abstract class ReleaseHookTask : DefaultTask() {

    @get:Input
    abstract val alsoDependsOn: Property<Task>

    fun releaseDependsOn(task: Task) {
        dependsOn(task)
        task.dependsOn(alsoDependsOn)
    }
}
