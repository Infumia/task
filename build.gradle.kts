import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding

plugins {
  java
  `java-library`
  `maven-publish`
  signing
  id("com.diffplug.spotless") version "6.16.0"
  id("io.github.gradle-nexus.publish-plugin") version "1.2.0"
}

val signRequired = !rootProject.property("dev").toString().toBoolean()
val spotlessApply = rootProject.property("spotless.apply").toString().toBoolean()

repositories { mavenCentral() }

if (spotlessApply) {
  configure<SpotlessExtension> {
    lineEndings = LineEnding.UNIX
    isEnforceCheck = false

    format("encoding") {
      target("*.*")
      encoding("UTF-8")
      endWithNewline()
      trimTrailingWhitespace()
    }

    kotlinGradle {
      target("**/*.gradle.kts")
      endWithNewline()
      indentWithSpaces(2)
      trimTrailingWhitespace()
      ktfmt()
    }

    java {
      target("**/src/**/java/**/*.java")
      importOrder()
      removeUnusedImports()
      endWithNewline()
      indentWithSpaces(2)
      trimTrailingWhitespace()
      prettier(mapOf("prettier" to "2.7.1", "prettier-plugin-java" to "1.6.2"))
          .config(
              mapOf("parser" to "java", "tabWidth" to 2, "useTabs" to false, "printWidth" to 100))
    }
  }
}

allprojects { group = "tr.com.infumia" }

subprojects {
  apply<JavaPlugin>()
  apply<JavaLibraryPlugin>()
  apply<MavenPublishPlugin>()
  apply<SigningPlugin>()

  val projectName = property("project.name").toString()

  java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

  tasks {
    compileJava { options.encoding = Charsets.UTF_8.name() }

    jar {
      archiveClassifier.set(null as String?)
      archiveBaseName.set(projectName)
      archiveVersion.set(project.version.toString())
    }

    javadoc {
      options.encoding = Charsets.UTF_8.name()
      (options as StandardJavadocDocletOptions).tags("todo")
    }

    val javadocJar by
        creating(Jar::class) {
          dependsOn("javadoc")
          archiveClassifier.set("javadoc")
          archiveBaseName.set(projectName)
          archiveVersion.set(project.version.toString())
          from(javadoc)
        }

    val sourcesJar by
        creating(Jar::class) {
          dependsOn("classes")
          archiveClassifier.set("sources")
          archiveBaseName.set(projectName)
          archiveVersion.set(project.version.toString())
          from(sourceSets["main"].allSource)
        }

    build {
      dependsOn(jar)
      dependsOn(sourcesJar)
      dependsOn(javadocJar)
    }
  }

  repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenLocal()
  }

  dependencies {
    compileOnly(rootProject.libs.terminable)
    compileOnly(rootProject.libs.lombok)
    compileOnly(rootProject.libs.annotations)

    annotationProcessor(rootProject.libs.lombok)
    annotationProcessor(rootProject.libs.annotations)

    testAnnotationProcessor(rootProject.libs.lombok)
    testAnnotationProcessor(rootProject.libs.annotations)
  }

  publishing {
    publications {
      val publication =
          create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = projectName
            version = project.version.toString()

            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
              name.set(projectName)
              description.set("A simple builder-like task organizer library for Paper.")
              url.set("https://infumia.com.tr/")
              licenses {
                license {
                  name.set("MIT License")
                  url.set("https://mit-license.org/license.txt")
                }
              }
              developers {
                developer {
                  id.set("portlek")
                  name.set("Hasan Demirtaş")
                  email.set("utsukushihito@outlook.com")
                }
              }
              scm {
                connection.set("scm:git:git://github.com/infumia/task.git")
                developerConnection.set("scm:git:ssh://github.com/infumia/task.git")
                url.set("https://github.com/infumia/task")
              }
            }
          }

      signing {
        isRequired = signRequired
        if (isRequired) {
          useGpgCmd()
          sign(publication)
        }
      }
    }
  }
}

nexusPublishing { repositories { sonatype() } }
