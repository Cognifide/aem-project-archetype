repositories {
    mavenLocal()
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:14.4.5")
    implementation("com.cognifide.gradle:environment-plugin:1.1.1")
    implementation("com.neva.gradle:fork-plugin:5.0.6")
    implementation("gradle.plugin.com.github.dkorotych.gradle.maven.exec:gradle-maven-exec-plugin:2.2.1")
}