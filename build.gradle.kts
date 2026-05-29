plugins {
    java
    // Добавляем плагин для удобного запуска тестового сервера
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "rto.plug"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    // Репозиторий для PacketEvents
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    // Твоя рабочая версия ядра
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")

    // AuthMe API
    compileOnly("com.github.AuthMe:AuthMeReloaded:5.6.0-beta2")

// Правильные координаты и актуальная версия PacketEvents
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")
}

java {
    // Строго фиксируем версию Java
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Настраиваем тот самый runServer
tasks.withType<xyz.jpenilla.runpaper.task.RunServer> {
    // Указываем версию ядра, которую плагин должен скачать для запуска
    minecraftVersion("26.1.2")
}

tasks.build {
    dependsOn("assemble")
}
