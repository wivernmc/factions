/*
 * Wivern © 2024 - Tous droits réservés
 * Ce code source est la propriété de Antoine Pigeard.
 */

plugins {

    id("com.gradleup.shadow") version "8.3.3"
    id("maven-publish")
    id("java")
}

group = "fr.wivern"
version = "1.0"

repositories {

    mavenCentral()

    maven {
        url = uri("https://repo.wivern.fr/releases")
        credentials {
            username = project.properties["wivernUsername"].toString()
            password = project.properties["wivernPassword"].toString()
        }
    }

    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://jcenter.bintray.com")
    maven("https://libraries.minecraft.net/")
    maven("https://maven.playpro.com/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.bg-software.com/repository/api/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.mikeprimm.com/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.rosewooddev.io/repository/public/")

}

dependencies {

    // Wivern
    compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")

    implementation("org.reflections:reflections:0.9.12")
    implementation("com.github.Driftay:WorldGuard-Bridge:6c80848837")
    implementation("de.tr7zw:item-nbt-api:2.13.2")
    implementation("me.lucko:commodore:2.2")
    implementation("com.mojang:brigadier:1.0.14")
    implementation("com.github.cryptomorin:XSeries:11.0.0-beta")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7") { exclude(group = "org.bukkit") }
    compileOnly(files("./dependencies/MVdWPlaceholderAPI.jar"))
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.1-SNAPSHOT") {
        exclude(group = "org.bukkit")
        exclude(group = "com.google.code.findbugs", module = "jsr305")
        exclude(group = "org.sk89q.bukkit", module = "bukkit-classloader-check")
        exclude(group = "com.sk89q", module = "dummypermscompat")
        exclude(group = "com.sk89q", module = "jchronic")
        exclude(group = "rhino", module = "js")
        exclude(group = "de.schlichtherle", module = "truezip")
        exclude(group = "com.sk89q.lib", module = "jlibnoise")
        exclude(group = "com.thoughtworks.paranamer", module = "paranamer")
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    compileOnly("dev.rosewood:rosestacker:1.2.6")
    compileOnly("com.bgsoftware:WildStackerAPI:2022.6")
    compileOnly("net.ess3:EssentialsX:2.18.2")
    compileOnly("net.ess3:EssentialsXChat:2.0.1") {
        exclude(group = "org.bukkit")
        exclude(group = "org.projectlombok")
        exclude(group = "net.ess3", module = "Essentials")
    }
    implementation("net.kyori:adventure-api:4.18.0-SNAPSHOT")
    implementation("net.kyori:adventure-text-serializer-legacy:4.18.0-SNAPSHOT")
    implementation("net.kyori:adventure-text-serializer-gson:4.18.0-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    compileOnly("org.dynmap:dynmap:2.0") {
        exclude(group = "org.bukkit")
        exclude(group = "com.nijikokun.bukkit", module = "Permissions")
        exclude(group = "de.bananaco", module = "bPermissions")
        exclude(group = "org.anjocaido", module = "EssentialsGroupManager")
        exclude(group = "org.getspout", module = "spoutpluginapi")
        exclude(group = "com.platymuus.bukkit.permissions", module = "PermissionsBukkit")
        exclude(group = "ru.tehkode", module = "PermissionsEx")
    }
    implementation("com.google.guava:guava:21.0") {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
    }
    implementation("org.apache.commons:commons-lang3:3.12.0")
    compileOnly("me.clip:placeholderapi:2.11.6") {
        exclude(group = "me.clip.placeholderapi.libs.kyori", module = "adventure-platform-bukkit")
        exclude(group = "me.clip.placeholderapi.libs.kyori", module = "adventure-api")
        exclude(group = "me.clip.placeholderapi.libs.kyori", module = "adventure-text-serializer-gson")
        exclude(group = "me.clip.placeholderapi.libs.kyori", module = "adventure-text-serializer-legacy")
    }
    compileOnly("net.coreprotect:coreprotect:2.15.0")
    implementation("com.mojang:authlib:1.5.21")
    implementation("org.bstats:bstats-bukkit:3.0.1")
    compileOnly("org.codehaus.mojo:findbugs-maven-plugin:3.0.5")


}

val pluginName = "Wivern" + project.name.split("-").joinToString("") { it.capitalize() }
tasks.processResources {

    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(project.properties
            .mapNotNull { (k, v) -> v?.toString()?.let { k.replace(".", "_") to it } }.toMutableList().apply {
                add("pluginName" to pluginName)
            }.toMap())

    }

    outputs.upToDateWhen { false }

}

tasks.shadowJar {
    archiveBaseName.set(pluginName)
    archiveClassifier.set("")
}

tasks.build {
    dependsOn("shadowJar")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.shadowJar {
    relocate("org.apache.commons.lang3", "com.massivecraft.factions.shade.apache")
    relocate("com.cryptomorin", "com.massivecraft.factions.shade")
    relocate("org.bstats", "com.massivecraft.factions.shade.org.bstats")
    relocate("net.dv8tion", "com.massivecraft.factions.shade.net.dv8tion")
    relocate("xyz.xenondevs", "com.massivecraft.factions.shade.particlelib")
    relocate("net.kyori", "com.massivecraft.factions.shade.net.kyori")
    relocate("com.darkblade12", "com.massivecraft.factions.shade.com.darkblade12")
    relocate("de.tr7zw.changeme.nbtapi", "com.massivecraft.factions.shade.nbtapi")
}

publishing {

    publications {

        publications {
            register(name, MavenPublication::class) {
                from(components["shadow"])
            }
        }

    }

    repositories {

        maven {
            url = uri("https://repo.wivern.fr/releases")
            credentials {
                username = project.properties["wivernUsername"].toString()
                password = project.properties["wivernPassword"].toString()
            }
        }

    }

}