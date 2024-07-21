plugins {
    id("java")
}

group = "sim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    testImplementation("org.testng:testng:7.4.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("io.github.cdimascio:dotenv-java:2.2.0")
}

tasks.test {
    useTestNG()
}