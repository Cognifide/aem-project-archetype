import com.cognifide.gradle.aem.common.tasks.SyncFileTask
import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("com.cognifide.aem.common")
    id("com.github.dkorotych.gradle-maven-exec")
}

description = "${appTitle} - All"

aem {
    tasks {
        val packageBuild by registering(MavenExec::class) {
            dependsOn(":core:bundleBuild", ":ui.apps:packageBuild", ":ui.content:packageBuild")
            goals("clean", "install")
            inputs.dir("src")
            inputs.file("pom.xml")
            inputs.file(common.recentFileProvider("../ui.apps/target"))
            inputs.file(common.recentFileProvider("../ui.content/target"))
            outputs.dir("target")
        }
        val packageDeploy by registering(SyncFileTask::class) {
            dependsOn(packageBuild)
            files.from(common.recentFileProvider("target"))
            syncFile { awaitIf { packageManager.deploy(it) } }
        }
        build {
            dependsOn(packageBuild)
        }
        clean {
            delete("target")
        }
    }
}
