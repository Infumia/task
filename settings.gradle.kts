rootProject.name = "task"

include0(
    mapOf(
        ":common" to "task-common",
        ":bukkit" to "task-bukkit",
    ),
)

fun include0(modules: Map<String, String?>) {
    modules.forEach { (module, projectName) ->
        include(module)
        if (projectName != null) {
            project(module).name = projectName
        }
    }
}
