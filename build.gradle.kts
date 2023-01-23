import java.net.URL

plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.izzel.taboolib") version "1.55"
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.dokka") version "1.7.20"
}
val api: String? by project
val order: String? by project

task("versionModify") {
    project.version = project.version.toString() + (order?.let { "-$it" } ?: "")
}

task("versionAddAPI") {
    if (api == null) return@task
    val origin = project.version.toString()
    project.version = "$origin-api"
}


task("releaseName") {
    println(project.name + "-" + project.version)
}

task("version") {
    println(project.version.toString())
}
taboolib {
    if (project.version.toString().contains("-api")) {
        options("skip-kotlin-relocate", "keep-kotlin-module")
    }
    description {
        contributors {
            name("Glom_")
        }
        dependencies {
            name("Pouvoir")
            name("GermPlugin").optional(true)
            name("SkillAPI").optional(true)
            name("DragonCore").optional(true)
            name("Magic").optional(true)
            name("MythicMobs").optional(true)


        }
    }

    install("common")
    install("common-5")
    install("module-chat")
    install("module-nms-util")
    install("module-nms")
    install("module-configuration")
    install("platform-bukkit")
    install("module-lang")

    install("module-metrics")
    classifier = null
    version = "6.0.10-71"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}


tasks.dokkaJavadoc.configure {
    outputDirectory.set(File("C:\\Users\\Administrator\\Desktop\\Doc\\attsystem"))
    dokkaSourceSets {
        configureEach {
            externalDocumentationLink {
                url.set(URL("https://doc.skillw.com/pouvoir/"))
            }
        }
    }

}
repositories {
    maven { url = uri("https://mvn.lumine.io/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }

    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.0.0")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.10")
    compileOnly("io.lumine:Mythic-Dist:5.0.3")
    compileOnly("ink.ptms.core:v11901:11901-minimize:mapped")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly(fileTree("libs"))
    compileOnly(kotlin("stdlib-jdk8"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.javadoc {
    this.options {
        encoding = "UTF-8"
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}



configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
            from(components["java"])
            version = project.version.toString()
            groupId = project.group.toString()
            pom {
                name.set(project.name)
                description.set("Bukkit Script Engine Plugin.")
                url.set("https://github.com/Glom-c/Pouvoir/")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/Glom-c/Asahi/blob/main/LICENSE")
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
                    connection.set("...")
                    developerConnection.set("...")
                    url.set("...")
                }
            }
        }
    }
}



signing {
    sign(publishing.publications.getAt("library"))
}