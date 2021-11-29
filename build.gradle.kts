import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//Применяется к студии
plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
}

//какие то константы
group = "me.ekate"
version = "1.0-SNAPSHOT"

//откуда брать код
repositories {
    mavenCentral()
}

//подключение библиотек
dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    //implementation("")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}