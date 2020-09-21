import com.cognifide.gradle.aem.bundle.tasks.bundle
import com.moowork.gradle.node.NodeExtension
import com.cognifide.gradle.aem.pkg.tasks.PackageDeploy

plugins {
    id("com.cognifide.aem.instance.local")
    id("com.cognifide.environment")
    id("com.neva.fork")
}

allprojects {

    group = "${groupId}"

    repositories {
        mavenLocal()
        jcenter()
        maven("https://repo.adobe.com/nexus/content/groups/public")
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        archiveBaseName.set(provider { "${rootProject.name}-${project.name}" })
        destinationDirectory.set(layout.projectDirectory.dir("target"))
    }

    plugins.withId("java") {
        tasks.withType<JavaCompile>().configureEach {
            with(options) {
                sourceCompatibility = "1.8"
                targetCompatibility = "1.8"
                encoding = "UTF-8"
            }
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging.showStandardStreams = true
        }

        dependencies {
            "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.6.0")
            "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.6.0")
        }
    }

    plugins.withId("com.cognifide.aem.package") {
        tasks.withType<PackageDeploy>().configureEach {
            dependsOn(":requireProps")
        }
    }

    plugins.withId("com.cognifide.aem.bundle") {
        tasks {
            withType<Jar> {
                bundle {
                    bnd("-plugin org.apache.sling.caconfig.bndplugin.ConfigurationClassScannerPlugin")
                    bnd("-plugin org.apache.sling.bnd.models.ModelsScannerPlugin")
                }
            }
        }

        dependencies {
#if ( $aemVersion != "cloud" )
            "compileOnly"("org.osgi:org.osgi.annotation.versioning:1.1.0")
            "compileOnly"("org.osgi:org.osgi.annotation.bundle:1.0.0")
            "compileOnly"("org.osgi:org.osgi.service.metatype.annotations:1.4.0")
            "compileOnly"("org.osgi:org.osgi.service.component.annotations:1.4.0")
            "compileOnly"("org.osgi:org.osgi.service.component:1.4.0")
            "compileOnly"("org.osgi:org.osgi.service.cm:1.6.0")
            "compileOnly"("org.osgi:org.osgi.service.event:1.3.1")
            "compileOnly"("org.osgi:org.osgi.service.log:1.4.0")
            "compileOnly"("org.osgi:org.osgi.resource:1.0.0")
            "compileOnly"("org.osgi:org.osgi.framework:1.9.0")
            "compileOnly"("org.osgi:org.osgi.util.tracker:1.5.1")
            "compileOnly"("org.apache.sling:org.apache.sling.servlets.annotations:1.2.4")
            "compileOnly"("org.slf4j:slf4j-api:1.7.21")
            "compileOnly"("org.apache.sling:org.apache.sling.models.api:1.3.6")
            "compileOnly"("javax.servlet:javax.servlet-api:3.1.0")
            "compileOnly"("javax.servlet.jsp:jsp-api:2.1")
            "compileOnly"("javax.annotation:javax.annotation-api:1.3.2")
            "compileOnly"("javax.jcr:jcr:2.0")
            "compileOnly"("com.day.cq.wcm:cq-wcm-taglib:5.7.4")
            "compileOnly"("com.adobe.cq:core.wcm.components.core:${core.wcm.components.version}")
            "compileOnly"("com.adobe.cq:core.wcm.components.content:${core.wcm.components.version}")
            "compileOnly"("com.adobe.cq:core.wcm.components.config:${core.wcm.components.version}")
            "compileOnly"("com.adobe.aem:uber-jar:$aemVersion:apis")
            #else
            "compileOnly"("com.adobe.aem:aem-sdk-api:SDK_VERSION")
#end
        }
    }

    plugins.withId("com.github.node-gradle.node") {
        configure<NodeExtension> {
            version = "12.16.2"
            yarnVersion = "1.22.4"
            download = true
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
    `package` {
        validator { // https://github.com/Cognifide/gradle-aem-plugin/blob/master/docs/package-plugin.md#crx-package-validation
            base("com.adobe.acs:acs-aem-commons-oakpal-checks:4.4.0")
        }
    }
    instance {
        provisioner { // https://github.com/Cognifide/gradle-aem-plugin/blob/master/docs/instance-plugin.md#task-instanceprovision
            configureReplicationAgentAuthor("publish") { enable(publishInstance) }
            configureReplicationAgentPublish("flush") { enable("http://localhost:80/dispatcher/invalidate.cache") }
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
}

apply(from = "gradle/fork/props.gradle.kts")