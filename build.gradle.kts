plugins {
    java
    // Добавляем плагин для удобного запуска тестового сервера
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "rto.plug"
version = "1.0.0"

repositories {
    mavenCentral()
    // Репозиторий PaperMC для ядра
    maven("https://repo.papermc.io/repository/maven-public/")
    // Официальный репозиторий ProtocolLib
    maven("https://repo.dmulloy2.net/repository/public/")
    // Репозиторий для AuthMe (через JitPack)
    maven("https://jitpack.io")
}

dependencies {
    // Твоя рабочая версия ядра
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")

    // Обновленный ProtocolLib, который точно должен скачаться
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    // AuthMe API
    compileOnly("com.github.AuthMe:AuthMeReloaded:5.6.0-beta2")
}

java {
    // Строго фиксируем версию Java
    toolchain.languageVersion.set(JavaLanguageVersion.of(26))
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