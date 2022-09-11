import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.charlotte04"
version = "1.0-SNAPSHOT"
java.sourceCompatibility=JavaVersion.VERSION_17
val mcVersion = "1.19.2"

repositories {
    mavenCentral()
    maven {
        url = URI("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven{
        url = URI("https://papermc.io/repo/repository/maven-public/")
    }
    maven{
        url = URI("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    // Align versions of all Kotlin components
    //implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Kotlin
    //implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // PaperMC API
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
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

}