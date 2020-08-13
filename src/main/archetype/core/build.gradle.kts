import com.cognifide.gradle.aem.common.tasks.SyncFileTask
import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("com.cognifide.aem.common")
    id("com.github.dkorotych.gradle-maven-exec")
}

description = "${appTitle} - Core"

tasks {
    val bundleBuild by registering(MavenExec::class) {
        dependsOn(":pom")
        goals("clean", "install")
        inputs.dir("src")
        inputs.file("pom.xml")
        outputs.dir("target")
    }
    val bundleInstall by registering(SyncFileTask::class) {
        dependsOn(bundleBuild)
        files.from(common.recentFileProvider("target"))
        syncFile { awaitIf { osgi.installBundle(it); true } }
    }
    build { dependsOn(bundleBuild) }
    clean { delete(bundleBuild) }
}
