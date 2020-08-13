import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("base")
    id("com.github.dkorotych.gradle-maven-exec")
}

description = "${appTitle} - UI Frontend"

tasks {
    val clientlibRoot = "../ui.apps/src/main/content/jcr_root/apps/${appId}/clientlibs"
    val clientlibBuild by registering(MavenExec::class) {
        dependsOn(":pom")
        goals("clean", "install")
        inputs.dir("src")
        inputs.files(fileTree(projectDir) {
            include("*.xml", "*.js", "*.json")
            exclude("package-lock.json")
        })
        outputs.dirs("dist", clientlibRoot)
    }
    build { dependsOn(clientlibBuild) }
    clean {
        delete(
                "dist",
                "$clientlibRoot/clientlib-site",
                "$clientlibRoot/clientlib-dependencies"
        )
    }
}