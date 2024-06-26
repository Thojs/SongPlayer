plugins {
	kotlin("jvm") version "2.0.0"
	id("fabric-loom") version "1.6-SNAPSHOT"
	id("maven-publish")
}

val mod_version: String by project
version = mod_version

group = "com.github.hhhzzzsss"

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project
val archives_base_name: String by project

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:$minecraft_version")
	mappings("net.fabricmc:yarn:$yarn_mappings:v2")
	modImplementation("net.fabricmc:fabric-loader:$loader_version")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

	modImplementation("net.fabricmc:fabric-language-kotlin:1.11.0+kotlin.2.0.0")
}

tasks {
	processResources {
		inputs.property("version", version)

		filesMatching("fabric.mod.json") {
			expand("version" to rootProject.version)
		}
	}

	compileJava {
		options.release = 21
	}

	jar {
		from("LICENSE") {
			rename { "${it}_$archives_base_name" }
		}
	}
}

java {
	withSourcesJar()
}