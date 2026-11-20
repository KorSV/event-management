plugins {
    id("java")
    java
    checkstyle
    jacoco
}

group = "com.ksv"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

checkstyle {
    toolVersion = "10.24.0" // актуальная стабильная версия для Gradle
    isIgnoreFailures = false // если есть ошибки — сборка падает (как в NFR)
    maxWarnings = 0 // не допускаем предупреждений
}

jacoco {
    toolVersion= "0.8.15"
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Тесты должны быть выполнены до отчёта
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}