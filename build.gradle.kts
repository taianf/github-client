import com.google.cloud.tools.gradle.appengine.core.*
import com.google.cloud.tools.gradle.appengine.standard.*
import com.google.javascript.jscomp.*

val appengineVersion: String by project
val assertjVersion: String by project
val firebaseVersion: String by project
val gceLogbackVersion: String by project
val googleCloudDatastoreVersion: String by project
val junitVersion: String by project
val kotlinSerialization: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project
val minifyVersion: String by project

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
    implementation("com.google.cloud:google-cloud-datastore:$googleCloudDatastoreVersion")
    implementation("com.google.cloud:google-cloud-logging-logback:$gceLogbackVersion")
    implementation("com.google.firebase:firebase-admin:$firebaseVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-servlet:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.padler.gradle.minify:gradle-minify-plugin:$minifyVersion")

    providedCompile("com.google.appengine:appengine:$appengineVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

configure<AppEngineStandardExtension> {
    deploy {
        projectId = "GCLOUD_CONFIG"
        version = "GCLOUD_CONFIG"
    }
    stage {
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
        addTestListener(object : TestListener {
            override fun beforeTest(p0: TestDescriptor?) = Unit
            override fun beforeSuite(p0: TestDescriptor?) = Unit
            override fun afterTest(desc: TestDescriptor, result: TestResult) {
                logger.lifecycle("$desc: $result")
            }

            override fun afterSuite(desc: TestDescriptor, result: TestResult) = Unit
        })
    }
    named<DeployTask>("appengineDeploy") {
        dependsOn("test")
    }
    named<StageStandardTask>("appengineStage") {
        dependsOn("test")
    }
    build {
        dependsOn("minify")
    }
}
