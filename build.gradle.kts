import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.charlotte04"
version = ""
java.sourceCompatibility=JavaVersion.VERSION_17
val mcVersion = "1.19.3"

repositories {
    mavenCentral()
    maven {
        //spigot
        url = URI("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven{
        //papermc
        url = URI("https://papermc.io/repo/repository/maven-public/")
    }
    maven{
        //jecon
        url = URI("https://himajyun.github.io/mvn-repo/")
    }
    maven {
        url = URI("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        //Placeholder
        url = URI("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        //Vault
        url = URI("https://jitpack.io")
    }

}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
    // Align versions of all Kotlin components
    // implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    // Kotlin
    // implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    // PaperMC API
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    // MiniMessage API
    implementation("net.kyori:adventure-text-minimessage:4.12.0")
    // Triumph GUI API
    implementation("dev.triumphteam:triumph-gui:3.1.2")
    // Vault API
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    // jecon API
    compileOnly("jp.jyn:Jecon:2.2.1")
    // PlaceHolder API
    compileOnly("me.clip:placeholderapi:2.11.2")
    // https://mvnrepository.com/artifact/mysql/mysql-connector-java
    implementation("mysql:mysql-connector-java:8.0.30")
    // ORM hikariCP
    //implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("club.minnced:discord-webhooks:0.8.2")
}

application{
    mainClass.set("com.charlotte04.vsccore.VSCCore.kt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("VSCCore.jar")
}