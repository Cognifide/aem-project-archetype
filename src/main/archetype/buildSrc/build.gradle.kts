repositories {
    mavenLocal()
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:14.1.3")
    implementation("com.cognifide.gradle:environment-plugin:1.0.2")
    implementation("com.neva.gradle:fork-plugin:5.0.4")
    implementation("gradle.plugin.com.github.dkorotych.gradle.maven.exec:gradle-maven-exec-plugin:2.2.1")
}