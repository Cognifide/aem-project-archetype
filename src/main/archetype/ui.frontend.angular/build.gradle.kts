import com.moowork.gradle.node.yarn.YarnTask

plugins {
    id("base")
    id("com.github.node-gradle.node")
}

description = "${appTitle} - UI Frontend"

tasks {
    register<YarnTask>("webpack") {
        dependsOn("yarn")

        val mode = findProperty("webpack.mode")?.toString() ?: "prod"
        setYarnCommand(when (mode) {
            "prod" -> "build:production"
            else -> "build"
        })
        setYarnCommand(mode)

        inputs.property("mode", mode)
        inputs.file("package.json")
        inputs.dir("src")
        outputs.dir("dist")
    }

    register<Zip>("zip") {
        dependsOn("webpack")
        from("dist")
    }

    clean {
        delete("dist")
    }

    build {
        dependsOn("zip")
    }
}