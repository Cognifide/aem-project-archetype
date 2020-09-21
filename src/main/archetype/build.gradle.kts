import com.cognifide.gradle.aem.bundle.tasks.bundle
import com.cognifide.gradle.aem.common.tasks.SyncFileTask
import com.github.dkorotych.gradle.maven.exec.MavenExec

plugins {
    id("com.cognifide.aem.instance.local")
    id("com.cognifide.environment")
    id("com.neva.fork")
    id("com.github.dkorotych.gradle-maven-exec")
}

allprojects {
    group = "${groupId}"

    repositories {
        mavenLocal()
        jcenter()
        maven("https://repo.adobe.com/nexus/content/groups/public")
    }

    plugins.withId("com.cognifide.aem.common") {
        tasks.withType<SyncFileTask>().configureEach {
            dependsOn(":requireProps")
        }
    }
}

defaultTasks("develop")

common {
    tasks {
        registerSequence("develop", {
            description = "Builds and deploys AEM application to instances (optionally cleans environment)"
            dependsOn(":requireProps")
        }) {
            when (prop.string("instance.type")) {
                "local" -> dependsOn(
                        ":instanceSetup",
                        ":environmentUp",
                        ":all:packageDeploy",
                        ":environmentReload",
                        ":environmentAwait"
                )
                else -> dependsOn(
                        ":instanceProvision",
                        ":all:packageDeploy"
                )
            }
        }
    }
}

aem {
    instance {
        provisioner { // https://github.com/Cognifide/gradle-aem-plugin/blob/master/docs/instance-plugin.md#task-instanceprovision
            configureReplicationAgentAuthor("publish") { enable(publishInstance) }
            configureReplicationAgentPublish("flush") { enable("http://localhost:80/dispatcher/invalidate.cache") }
            deployPackage("com.adobe.cq:core.wcm.components.all:2.11.1@zip")
            deployPackage("com.neva.felix:search-webconsole-plugin:1.3.0")
        }
    }
    localInstance {
        install {
            files {
                // https://github.com/Cognifide/gradle-aem-plugin/blob/master/docs/local-instance-plugin.md#pre-installed-osgi-bundles-and-crx-packages
            }
        }
    }
}

environment { // https://github.com/Cognifide/gradle-environment-plugin
    docker {
        containers {
            "httpd" {
                resolve {
                    resolveFiles {
                        download("http://download.macromedia.com/dispatcher/download/dispatcher-apache2.4-linux-x86_64-4.3.3.tar.gz").use {
                            copyArchiveFile(it, "**/dispatcher-apache*.so", file("modules/mod_dispatcher.so"))
                        }
                    }
                    rootProject.file("src/environment/httpd/conf.d/variables/default.vars")
                            .copyTo(rootProject.file("dispatcher/src/conf.d/variables/default.vars"), true)
                    ensureDir("htdocs", "cache", "logs")
                }
                up {
                    ensureDir("/usr/local/apache2/logs", "/var/www/localhost/htdocs", "/var/www/localhost/cache")
                    execShell("Starting HTTPD server", "/usr/sbin/httpd -k start")
                }
                reload {
                    cleanDir("/var/www/localhost/cache")
                    execShell("Restarting HTTPD server", "/usr/sbin/httpd -k restart")
                }
                dev {
                    watchRootDir(
                            "dispatcher/src/conf.d",
                            "dispatcher/src/conf.dispatcher.d",
                            "src/environment/httpd"
                    )
                }
            }
        }
    }
    hosts {
        "http://publish" { tag("publish") }
        "http://flush" { tag("flush") }
    }
    healthChecks {
        http("Site '${appTitle}'", "http://publish/us/en.html", "${appTitle}")
        http("Author Sites Editor", "http://localhost:4502/sites.html") {
            containsText("Sites")
            options { basicCredentials = aem.authorInstance.credentials }
        }
    }
}

tasks {
    instanceResolve { dependsOn(requireProps) }
    instanceCreate { dependsOn(requireProps) }
    environmentUp { mustRunAfter(instanceUp, instanceProvision, instanceSetup) }
    environmentAwait { mustRunAfter(instanceAwait) }

    register<MavenExec>("pom") {
        goals("clean", "install", "--non-recursive")
        inputs.file("pom.xml")
        outputs.dir(file(System.getProperty("user.home")).resolve(".m2/repository/${project.group.toString().replace(".", "/")}/${project.name}"))
    }
}

apply(from = "gradle/fork/props.gradle.kts")