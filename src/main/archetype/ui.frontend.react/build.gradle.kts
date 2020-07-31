import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("base")
    id("com.github.dkorotych.gradle-maven-exec")
}

description = "${appTitle} - UI Frontend"

tasks {
    val clientlibsPath = "../ui.apps/src/main/content/jcr_root/apps/${appId}/clientlibs"
    val runNode by registering(MavenExec::class) {
        goals("clean", "package")
        inputs.dir("src")
        inputs.files(fileTree(projectDir) { include("pom.xml", "*.js", "*.json") })
        outputs.dirs("dist", clientlibsPath)
    }
    build {
        dependsOn(runNode)
    }
    clean {
        delete(
                "dist",
                "$clientlibsPath/clientlib-site",
                "$clientlibsPath/clientlib-dependencies"
        )
    }
}