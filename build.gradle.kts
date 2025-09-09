
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)

}

group = "com.advanced_baseball_stats"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation("io.ktor:ktor-client-core:3.1.1")
    implementation("io.ktor:ktor-client-cio:3.1.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.1.1")
    implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.41.2.2")
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-crypt:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")

    implementation("org.jetbrains.exposed:exposed-jodatime:0.61.0")
    // or
    implementation("org.jetbrains.exposed:exposed-java-time:0.61.0")
    // or
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.61.0")

    implementation("org.jetbrains.exposed:exposed-json:0.61.0")
    implementation("org.jetbrains.exposed:exposed-money:0.61.0")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.61.0")

    implementation("org.ktorm:ktorm-core:4.1.1")
    implementation("org.ktorm:ktorm-jackson:4.1.1")
    implementation("org.ktorm:ktorm-support-sqlite:4.1.1")
    implementation("org.ktorm:ktorm-ksp-annotations:4.1.1")

    implementation("redis.clients:jedis:6.0.0")

    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation("io.mockk:mockk:1.13.10")
}
