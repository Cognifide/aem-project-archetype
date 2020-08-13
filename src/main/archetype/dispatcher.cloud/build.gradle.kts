import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("base")
    id("com.github.dkorotych.gradle-maven-exec")
}

description = "${appTitle} - Dispatcher"

tasks {
    val zipBuild by registering(MavenExec::class) {
        dependsOn(":pom")
        goals("clean", "install")
        inputs.dir("src")
        inputs.files("pom.xml", "assembly.xml")
        outputs.dir("target")
    }
    build { dependsOn(zipBuild) }
    clean { delete(zipBuild) }
}