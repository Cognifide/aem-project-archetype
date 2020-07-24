plugins {
    id("com.cognifide.aem.package")
}

description = "${appTitle} - UI apps structure"

aem {
    tasks {
        packageCompose {
            vaultDefinition {
                property("cloudManagerTarget", "none")
                filters(
                    "/apps",
                    "/apps/sling",
                    "/apps/cq",
                    "/apps/dam",
                    "/apps/wcm",
                    "/apps/msm",
                    "/apps/settings"
                )
            }
        }
    }
}