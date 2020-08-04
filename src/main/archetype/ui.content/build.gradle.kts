plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
}

description = "${appTitle} - UI content"

aem {
    tasks {
        packageCompose {
            vault {
                property("cloudManagerTarget", "none")
            }
        }
    }
}