import com.cognifide.gradle.aem.common.tasks.SyncFileTask
import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("com.cognifide.aem.common")
    id("com.github.dkorotych.gradle-maven-exec")
}

description = "${appTitle} - All"

aem {
    tasks {
        val packageBuild by registering() {

        }
        val packageDeploy by registering(SyncFileTask::class) {

        }
    }
}
