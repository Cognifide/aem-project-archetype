plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
}

description = "${appTitle} - UI apps"

aem {
    tasks {
        packageCompose {
            dependsOn(":ui.frontend:webpack")
            installBundleProject(":core")
            vault {
                property("cloudManagerTarget", "none")
            }
        }
    }
}