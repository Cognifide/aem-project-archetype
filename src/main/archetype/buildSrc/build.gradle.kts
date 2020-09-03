repositories {
    mavenLocal()
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:14.2.12")
    implementation("org.apache.sling:org.apache.sling.caconfig.bnd-plugin:1.0.2")
    implementation("com.github.node-gradle:gradle-node-plugin:2.2.4")
    implementation("com.cognifide.gradle:environment-plugin:1.0.4")
    implementation("com.neva.gradle:fork-plugin:5.0.6")
}
