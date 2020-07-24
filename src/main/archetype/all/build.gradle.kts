plugins {
    id("com.cognifide.aem.package")
    id("maven-publish")
}

description = "${appTitle} - All"

aem {
    tasks {
        packageCompose {
            nestPackageProject(":ui.apps") { dirPath.set("/apps/${appId}-packages/application/install") }
            nestPackageProject(":ui.content") { dirPath.set("/apps/${appId}-packages/content/install") }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(common.publicationArtifact(tasks.packageCompose))
        }
    }
}