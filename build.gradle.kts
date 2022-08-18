import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding

plugins {
  java
  `java-library`
  `maven-publish`
  signing
  id("com.diffplug.spotless") version "6.9.1"
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

repositories {
  mavenCentral()
  maven("https://papermc.io/repo/repository/maven-public/")
}

val spotlessApply = rootProject.property("spotless.apply").toString().toBoolean()

if (spotlessApply) {
  configure<SpotlessExtension> {
    lineEndings = LineEnding.UNIX
    isEnforceCheck = false

    format("encoding") {
      target("*.*")
      encoding("UTF-8")
    }

    java {
      target("**/src/**/java/**/*.java")
      importOrder()
      removeUnusedImports()
      endWithNewline()
      indentWithSpaces(2)
      trimTrailingWhitespace()
      prettier(
        mapOf(
          "prettier" to "2.7.1",
          "prettier-plugin-java" to "1.6.2"
        )
      ).config(
        mapOf(
          "parser" to "java",
          "tabWidth" to 2,
          "useTabs" to false
        )
      )
    }
  }
}

val signRequired = !rootProject.property("dev").toString().toBoolean()

allprojects {
  group = "tr.com.infumia"

  extra["qualifiedProjectName"] = if (parent == null) {
    "Task"
  } else {
    val parentName = parent!!.extra["qualifiedProjectName"].toString()
    parentName + name[0].toUpperCase() + name.substring(1)
  }
}

subprojects {
  apply<JavaPlugin>()
  apply<JavaLibraryPlugin>()
  apply<MavenPublishPlugin>()
  apply<SigningPlugin>()

  val qualifiedProjectName = project.extra["qualifiedProjectName"].toString()

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }

  tasks {
    compileJava {
      options.encoding = Charsets.UTF_8.name()
    }

    jar {
      archiveClassifier.set(null as String?)
      archiveBaseName.set(qualifiedProjectName)
      archiveVersion.set(project.version.toString())
    }

    javadoc {
      options.encoding = Charsets.UTF_8.name()
      (options as StandardJavadocDocletOptions).tags("todo")
    }

    val javadocJar by creating(Jar::class) {
      dependsOn("javadoc")
      archiveClassifier.set("javadoc")
      archiveBaseName.set(qualifiedProjectName)
      archiveVersion.set(project.version.toString())
      from(javadoc)
    }

    val sourcesJar by creating(Jar::class) {
      dependsOn("classes")
      archiveClassifier.set("sources")
      archiveBaseName.set(qualifiedProjectName)
      archiveVersion.set(project.version.toString())
      from(sourceSets["main"].allSource)
    }

    build {
      dependsOn(jar)
      dependsOn(sourcesJar)
      dependsOn(javadocJar)
    }
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
      val publication = create<MavenPublication>("mavenJava") {
        groupId = project.group.toString()
        artifactId = qualifiedProjectName
        version = project.version.toString()

        from(components["java"])
        artifact(tasks["sourcesJar"])
        artifact(tasks["javadocJar"])
        pom {
          name.set("Event")
          description.set("A simple builder-like task orgianizer library.")
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

nexusPublishing {
  repositories {
    sonatype()
  }
}
