plugins {
	java
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	id("jacoco")
}

group = "com.teste"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.flywaydb:flyway-database-postgresql:10.15.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2:2.2.224")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("io.rest-assured:rest-assured:5.3.0")
//	testImplementation("org.testcontainers:testcontainers:1.18.0")
	// https://mvnrepository.com/artifact/org.testcontainers/postgresql
	testImplementation("org.testcontainers:postgresql:1.19.0")



	// Doc
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

}

jacoco {
	toolVersion = "0.8.8"
}

val excludedClasses = listOf(
	"br/com/estudo/testes/exceptions/**",
	"br/com/estudo/testes/model/**"
	// Adicione outros pacotes ou classes a serem excluídos
)

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport) // executa a tarefa de relatório do JaCoCo após os testes
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // garante que os testes sejam executados antes do relatório
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude(excludedClasses)
				// Adicione outros pacotes ou classes a serem excluídos
			}
		})
	)
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.8".toBigDecimal() // 80% de cobertura mínima exigida
			}
		}
	}
	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude(excludedClasses)
				// Adicione outros pacotes ou classes a serem excluídos
			}
		})
	)
}
