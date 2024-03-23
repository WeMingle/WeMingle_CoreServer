import com.ewerk.gradle.plugins.tasks.QuerydslCompile
plugins {
	java
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
}

group = "com.wemingle"
version = "0.0.1-SNAPSHOT"

val queryDslVersion = "5.0.0"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.json:json:20231013")
	implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
	implementation("org.hibernate:hibernate-spatial:6.1.7.Final")
	implementation("software.amazon.awssdk:s3:2.13.0")


	// QueryDSL Implementation
	implementation ("com.querydsl:querydsl-jpa:${queryDslVersion}:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:${queryDslVersion}:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
//val querydslDir = "src/main/generated"
//sourceSets {
//	getByName("main").java.srcDirs(querydslDir)
//}
//
//tasks.withType<JavaCompile> {
//	options.generatedSourceOutputDirectory = file(querydslDir)
//
//	// 위의 설정이 안되면 아래 설정 사용
//	// options.generatedSourceOutputDirectory.set(file(querydslDir))
//}
//
//tasks.named("clean") {
//	doLast {
//		file(querydslDir).deleteRecursively()
//	}
//}

val querydslDir = "src/main/generated"

querydsl {
	jpa = true
	querydslSourcesDir = querydslDir
}
sourceSets.getByName("main") {
	java.srcDir(querydslDir)
}
configurations {
	named("querydsl") {
		extendsFrom(configurations.compileClasspath.get())
	}
}
tasks.withType<QuerydslCompile> {
	options.annotationProcessorPath = configurations.querydsl.get()
}
