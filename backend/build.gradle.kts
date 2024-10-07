import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("org.springframework.boot") version "3.3.4"
  id("io.spring.dependency-management") version "1.1.6"
  id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
  id("jacoco")
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  kotlin("plugin.jpa") version "1.9.25"
}

group = "cz.tul"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(20)
  }
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
    jvmTarget.set(JvmTarget.JVM_20)
    verbose = true
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
  testImplementation {
    exclude(group = "org.mockito") // it is shipped with spring and there is no need, since we use mockk
  }
}

repositories {
  mavenCentral()
  maven {
    url = uri("https://jitpack.io") // for jackson-module-kogera
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-webflux")

  // Jackson and logging
  implementation("com.github.ProjectMapK:jackson-module-kogera:2.17.1-beta13")

  // Logging
  implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
  implementation("ch.qos.logback:logback-classic:1.5.6")
  implementation("ch.qos.logback:logback-core:1.5.6")

  // used for Loki/Prometheus logging maybe we will use it in the future
  /*val logbackJsonVersion = "0.1.5"
  implementation("ch.qos.logback.contrib:logback-json-classic:$logbackJsonVersion")
  implementation("ch.qos.logback.contrib:logback-jackson:$logbackJsonVersion")*/

  // Springdoc OpenAPI
  val openapiVersion = "2.6.0"
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openapiVersion")
  implementation("org.springdoc:springdoc-openapi-starter-common:$openapiVersion")

  // Kotlin extra libraries
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  // Database
  runtimeOnly("org.postgresql:postgresql")
  implementation("org.liquibase:liquibase-core:4.26.0")

  // Json Web Token
  val jjwtVersion = "0.12.5"
  implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

  // testing
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("com.h2database:h2:2.2.224")

  val kotestVersion = "5.8.0"
  testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
  testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
  testImplementation("io.kotest:kotest-property:$kotestVersion")
  testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
  testImplementation("com.ninja-squad:springmockk:4.0.2")
/*  val logCaptureVersion = "1.3.3" // can listen and assert on log messages
  testImplementation("org.logcapture:logcapture-core:$logCaptureVersion")
  testImplementation("org.logcapture:logcapture-kotest:$logCaptureVersion")*/
}

tasks.test {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  reports {
    xml.required = true
  }
  dependsOn(tasks.test)
}

tasks.jacocoTestCoverageVerification {
  violationRules {
    rule {
      limit {
        minimum = BigDecimal("0.8")
      }
    }
  }
}

tasks.register("deleteTemp") {
  delete {
    delete("$projectDir/.tmp")
  }
}

tasks["clean"].dependsOn("deleteTemp")
