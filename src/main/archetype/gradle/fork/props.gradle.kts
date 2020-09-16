import com.cognifide.gradle.aem.common.instance.local.Source
import com.cognifide.gradle.aem.common.instance.local.OpenMode
import com.neva.gradle.fork.ForkExtension

configure<ForkExtension> {
    properties {
        group("Instance Options") {
            define("instanceType") {
                label = "Type"
                select("local", "remote")
                description = "Local - instance will be created on local file system\nRemote - connecting to remote instance only"
                controller { toggle(value == "local", "instanceRunModes", "instanceJvmOpts", "localInstance*") }
            }
            define("instanceAuthorHttpUrl") {
                label = "Author HTTP URL"
                url("http://localhost:4502")
                optional()
            }
            define("instanceAuthorEnabled") {
                label = "Author Enabled"
                checkbox(true)
            }
            define("instancePublishHttpUrl") {
                label = "Publish HTTP URL"
                url("http://localhost:4503")
                optional()
            }
            define("instancePublishEnabled") {
                label = "Publish Enabled"
                checkbox(true)
            }
            define("instanceProvisionEnabled") {
                label = "Provision Enabled"
                description = "Turns on/off automated instance configuration."
                checkbox(true)
            }
            define("instanceProvisionDeployPackageStrict") {
                label = "Provision Deploy Package Strict"
                description = "Check if package is actually deployed on instance.\n" +
                        "By default faster heuristic is used which does not require downloading deployed packages eagerly."
                checkbox(false)
            }
        }

        group("Instance Checking") {
            define("instanceCheckHelpEnabled") {
                label = "Help"
                description = "Tries to start bundles automatically when instance is not stable longer time."
                checkbox(true)
            }
            define("instanceCheckBundlesEnabled") {
                label = "Bundles"
                description = "Awaits for all bundles in active state."
                checkbox(true)
            }
            define("instanceCheckInstallerEnabled") {
                label = "Installer"
                description = "Awaits for Sling OSGi Installer not processing any resources."
                checkbox(true)
            }
            define("instanceCheckEventsEnabled") {
                label = "Events"
                description = "Awaits period of time free of OSGi events incoming."
                checkbox(true)
            }
            define("instanceCheckComponentsEnabled") {
                label = "Components"
                description = "Awaits for active platform and application specific components."
                checkbox(true)
            }
        }

        group("Local instance") {
            define("localInstanceSource") {
                label = "Source"
                description = "Controls how instances will be created (from scratch, backup or any available source)"
                select(Source.values().map { it.name.toLowerCase() }, Source.AUTO.name.toLowerCase())
            }
            define("localInstanceQuickstartJarUri") {
                label = "Quickstart URI"
                description = "For file named 'cq-quickstart-x.x.x.jar'"
            }
            define("localInstanceQuickstartLicenseUri") {
                label = "Quickstart License URI"
                description = "For file named 'license.properties'"
            }
            define("localInstanceBackupDownloadUri") {
                label = "Backup Download URI"
                description = "For backup file. Protocols supported: SMB/SFTP/HTTP"
                optional()
            }
            define("localInstanceBackupUploadUri") {
                label = "Backup Upload URI"
                description = "For directory containing backup files. Protocols supported: SMB/SFTP"
                optional()
            }
            define("localInstanceRunModes") {
                label = "Run Modes"
                text("local")
            }
            define("localInstanceJvmOpts") {
                label = "JVM Options"
                text("-server -Xmx2048m -XX:MaxPermSize=512M -Djava.awt.headless=true")
            }
            define("localInstanceOpenMode") {
                label = "Open Automatically"
                description = "Open web browser when instances are up."
                select(OpenMode.values().map { it.name.toLowerCase() }, OpenMode.ALWAYS.name.toLowerCase())
            }
            define("localInstanceOpenAuthorPath") {
                label = "Open Author Path"
                text("/aem/start.html")
            }
            define("localInstanceOpenPublishPath") {
                label = "Open Publish Path"
                text("/crx/packmgr")
            }
        }

        group("Package") {
            define("packageDeployAvoidance") {
                label = "Deploy Avoidance"
                description = "Avoids uploading and installing package if identical is already deployed on instance."
                checkbox(true)
            }
            define("packageDamAssetToggle") {
                label = "Deploy Without DAM Worklows"
                description = "Turns on/off temporary disablement of assets processing for package deployment time.\n" +
                        "Useful to avoid redundant rendition generation when package contains renditions synchronized earlier."
                checkbox(true)
                dynamic("props")
            }
            define("packageValidatorEnabled") {
                label = "Validator Enabled"
                description = "Turns on/off package validation using OakPAL."
                checkbox(false)
            }
            define("packageNestedValidation") {
                label = "Nested Validation"
                description = "Turns on/off separate validation of built subpackages."
                checkbox(true)
            }
            define("packageBundleTest") {
                label = "Bundle Test"
                description = "Turns on/off running tests for built bundles put under install path."
                checkbox(true)
            }
        }

        group("Authorization") {
            define("companyUser") {
                label = "User"
                description = "Authorized to access AEM files"
                defaultValue = System.getProperty("user.name").orEmpty()
                optional()
            }
            define("companyPassword") {
                label = "Password"
                description = "For above user"
                optional()
            }
            define("companyDomain") {
                label = "Domain"
                description = "Needed only when accessing AEM files over SMB"
                defaultValue = System.getenv("USERDOMAIN").orEmpty()
                optional()
            }
        }

        group("Other") {
            define("webpackMode") {
                label = "Webpack Mode"
                description = "Controls optimization of front-end resources (CSS/JS/assets) "
                select("dev", "prod")
            }
            define("notifierEnabled") {
                label = "Notifications"
                description = "Controls displaying of GUI build notifications (baloons)"
                checkbox(true)
            }
        }
    }
}