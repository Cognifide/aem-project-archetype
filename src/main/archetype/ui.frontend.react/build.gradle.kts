import com.moowork.gradle.node.yarn.YarnTask

plugins {
    id("base")
    id("com.github.node-gradle.node")
}

description = "${appTitle} - UI Frontend"

tasks {
    register<YarnTask>("webpack") {
        dependsOn("yarn")
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