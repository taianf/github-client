import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardExtension
import com.google.javascript.jscomp.CompilationLevel
import org.padler.gradle.minify.*

val appengine_plugin_version: String by project
val appengine_version: String by project
val assertj_version: String by project
val firebase_version: String by project
val gce_logback_version: String by project
val google_cloud_datastore_version: String by project
val junit_version: String by project
val kotlin_serialization: String by project
val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.google.cloud.tools:appengine-gradle-plugin:2.3.0")
    }
}

plugins {
    application
    id("org.padler.gradle.minify") version "1.6.0"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    war
}

// keep appengine plugin in the old way because of compatibility issues
apply(plugin = "com.google.cloud.tools.appengine")

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin")
        resources.srcDirs("src/main/resources")
    }
    test {
        java.srcDirs("src/test/kotlin")
        resources.srcDirs("src/test/resources")
    }
}

minification {
    val staticDir = "${rootDir}/src/main/resources/static"
    val distDir = "$staticDir/dist"
    cssDstDir = distDir
    cssSrcDir = "$staticDir/css"
    jsDstDir = distDir
    jsSrcDir = "$staticDir/js"

    css.eliminateDeadStyles = true
    css.cssRenamingPrefix = ""

    js.compilationLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS
    js.strictModeInput = true
}

repositories {
    google()
    gradlePluginPortal()
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation("com.google.cloud:google-cloud-datastore:$google_cloud_datastore_version")
    implementation("com.google.cloud:google-cloud-logging-logback:$gce_logback_version")
    implementation("com.google.firebase:firebase-admin:$firebase_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-servlet:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("org.padler.gradle.minify:gradle-minify-plugin:1.6.0")

    providedCompile("com.google.appengine:appengine:$appengine_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.assertj:assertj-core:$assertj_version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
}

//appengineDeploy {
//    dependsOn = test
//}
//appengineStage {
//    dependsOn = test
//}

configure<AppEngineStandardExtension> {
    deploy {
        projectId = "GCLOUD_CONFIG"
        version = "GCLOUD_CONFIG"
//        dependsOn("test")
    }
    stage {
//        dependsOn("test")
    }
    run {
        jvmFlags = listOf("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
        automaticRestart = true
    }
}

tasks {
    val javaVersionCode = JavaVersion.VERSION_1_8.toString()
    compileKotlin {
        kotlinOptions.jvmTarget = javaVersionCode
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = javaVersionCode
    }
    test {
        useJUnit()
        testLogging.showStandardStreams = true
        fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
            logger.lifecycle("" + testDescriptor + ": " + result)
        }
    }
    build {
        dependsOn("minify")
    }
}
