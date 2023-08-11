import java.net.URL

plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.dokka") version "1.7.20"
    id("io.codearte.nexus-staging") version "0.30.0"
}

val order: String? by project

task("info") {
    println(project.name + "-" + project.version)
    println(project.version.toString())
}
taboolib {
    project.version = project.version.toString() + (order?.let { "-$it" } ?: "")
    if (project.version.toString().contains("-api")) {
        options("skip-kotlin-relocate", "keep-kotlin-module")
    }
//    if (project.version.toString().contains("-no-ktmod")) {
//        options.remove("keep-kotlin-module")
//    }
    description {
        contributors {
            name("Glom_")
        }
        dependencies {
            name("Pouvoir")
            name("GermPlugin").optional(true)
            name("DragonCore").optional(true)
            name("MythicMobs").optional(true)


        }
    }

    install("common")
    install("common-5")
    install("module-chat")
    install("module-nms-util")
    install("module-nms")
    install("module-configuration")
    install("module-lang")
    install("platform-bukkit")

    install("module-metrics")
    classifier = null
    version = "6.0.11-31"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}


tasks.dokkaJavadoc.configure {
    dokkaSourceSets {
        configureEach {
            externalDocumentationLink {
                url.set(URL("https://doc.skillw.com/pouvoir/"))
            }
            externalDocumentationLink {
                url.set(URL("https://docs.oracle.com/javase/8/docs/api/"))
            }
            externalDocumentationLink {
                url.set(URL("https://docs.oracle.com/javase/8/docs/api/"))
            }
            externalDocumentationLink {
                url.set(URL("https://doc.skillw.com/bukkit/"))
            }
        }
    }

}
repositories {
    maven { url = uri("https://mvn.lumine.io/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }

    mavenCentral()
}
tasks.register<Jar>("buildAPIJar") {
    dependsOn(tasks.compileJava, tasks.compileKotlin)
    from(tasks.compileJava, tasks.compileKotlin)
    includeEmptyDirs = false
    include { it.isDirectory or it.name.endsWith(".class") or it.name.endsWith(".kotlin_module") }
    archiveClassifier.set("api")
}

tasks.register<Jar>("buildJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.register<Jar>("buildSourcesJar") {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}
dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.10")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.5.0b")
    compileOnly("io.lumine:Mythic-Dist:5.0.3")
    compileOnly("ink.ptms.core:v11901:11901-minimize:mapped")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("com.skillw.pouvoir:Pouvoir:1.6.4-8")
    compileOnly(fileTree("libs"))
    compileOnly(kotlin("stdlib-jdk8"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.javadoc {
    this.options {
        encoding = "UTF-8"
    }
}


configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}


publishing {
    repositories {
        maven {
            url = if (project.version.toString().contains("-SNAPSHOT")) {
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            } else {
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
            credentials {
                username = project.findProperty("username").toString()
                password = project.findProperty("password").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")

            }
        }
        mavenLocal()
    }
    publications {
        create<MavenPublication>("library") {
            artifact(tasks["buildAPIJar"]) { classifier = classifier?.replace("-api", "") }
            artifact(tasks["buildJavadocJar"])
            artifact(tasks["buildSourcesJar"])
            version = project.version.toString()
            groupId = project.group.toString()
            pom {
                name.set(project.name)
                description.set("Bukkit Attribute Engine Plugin.")
                url.set("https://github.com/Glom-c/AttributeSystem/")

                dependencies {
                    compileOnly("com.skillw.pouvoir:Pouvoir:1.6.4-8")
                }

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/Glom-c/AttributeSystem/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("Skillw")
                        name.set("Glom_")
                        email.set("glom@skillw.com")
                    }
                }
                scm {
                    connection.set("scm:git:git:https://github.com/Glom-c/AttributeSystem.git")
                    developerConnection.set("scm:git:ssh:https://github.com/Glom-c/AttributeSystem.git")
                    url.set("https://github.com/Glom-c/AttributeSystem.git")
                }
            }
        }
    }
}

nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    username = project.findProperty("username").toString()
    password = project.findProperty("password").toString()
    packageGroup = "com.skillw"
}

signing {
    sign(publishing.publications.getAt("library"))
}