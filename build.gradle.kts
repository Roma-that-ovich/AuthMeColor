plugins {
    java
    // Возвращаем плагин для работы задачи runServer
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "rto.plug"
version = "1.2.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    // Фиксируем версию API под 1.20.1 для совместимости с сервером
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")

    // AuthMe API (используем стабильный репозиторий)
    compileOnly("fr.xephi:authme:5.6.0-beta2")

    // PacketEvents (версия 2.12.1 совместима с 1.20)
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")
}

java {
    // Строгая привязка к Java 25 для рабочего сервера
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}

// Настройка тестового сервера
tasks.withType<xyz.jpenilla.runpaper.task.RunServer> {
    minecraftVersion("26.1.2")
}

tasks.build {
    dependsOn("assemble")
}
