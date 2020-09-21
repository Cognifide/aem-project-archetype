import com.cognifide.gradle.aem.common.tasks.SyncFileTask
import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("com.cognifide.aem.common")
    id("com.cognifide.aem.package.sync")
    id("com.github.dkorotych.gradle-maven-exec")
}

description = "${appTitle} - UI apps"

tasks {
    val packageBuild by registering(MavenExec::class) {
        dependsOn(":pom", ":ui.apps.structure:packageBuild", ":core:bundleBuild", ":ui.frontend:clientlibBuild")
        goals("clean", "install")
        inputs.dir("src")
        inputs.file("pom.xml")
        inputs.dir(project(":ui.apps").file("src/main/content/jcr_root/apps/${appId}/clientlibs"))
        inputs.file(common.recentFileProvider(project(":core").file("target")))
        outputs.dir("target")
    }
    val packageDeploy by registering(SyncFileTask::class) {
        dependsOn(packageBuild)
        files.from(common.recentFileProvider("target"))
        syncFile { awaitIf { packageManager.deploy(it) } }
    }
    build { dependsOn(packageBuild) }
    clean { delete(packageBuild) }
}