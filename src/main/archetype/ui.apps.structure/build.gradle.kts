import com.cognifide.gradle.aem.common.tasks.SyncFileTask
import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("com.cognifide.aem.common")
    id("com.github.dkorotych.gradle-maven-exec")
}

description = "${appTitle} - UI apps structure"

aem {
    tasks {
        val packageBuild by registering(MavenExec::class) {
            goals("clean", "install")
            inputs.dir("src")
            inputs.file("pom.xml")
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