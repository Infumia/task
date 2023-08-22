# task
[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Update Snapshot](https://github.com/Infumia/task/actions/workflows/snapshot.yml/badge.svg)](https://github.com/Infumia/task/actions/workflows/snapshot.yml)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/tr.com.infumia/task?label=maven-central&server=https%3A%2F%2Foss.sonatype.org%2F)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/tr.com.infumia/task?label=maven-central&server=https%3A%2F%2Foss.sonatype.org)
## How to Use (Developers)
### Initiate the Library
```java
final class Plugin extends JavaPlugin {
  @Override
  public void onLoad() {
    BukkitTasks.init(this);
  }
}
```
### Maven
```xml
<dependencies>
  <!-- Do NOT forget to relocate -->
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>task-common</artifactId>
    <version>VERSION</version>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>task-bukkit</artifactId>
    <version>VERSION</version>
  </dependency>
</dependencies>
```
### Gradle
```groovy
plugins {
  id "java"
}

dependencies {
  // Do NOT forget to relocate.
  implementation "tr.com.infumia:task-common:VERSION"
  implementation "tr.com.infumia:task-bukkit:VERSION"
}
```
