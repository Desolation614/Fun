Below is a README template that is tailored to this Ferox client project and is structured so it’s easy to understand how to build, run, and extend it.

MyFeroxClient
A local, fully client-side Ferox RuneLite-based client built from decompiled .jar files.
The goal is to build, launch, and log in to Ferox using a custom Gradle project, and then extend it with safe client-side plugins, overlays, and scripts.

Project goals
Rebuild a working Ferox client around the existing Ferox jars on disk.

Launch the client via a custom Java entrypoint (MyClientLauncher) instead of the original launcher.

Keep all work strictly client-side (no server modification, spoofing, or exploitation).

Provide a clean base to add custom plugins, overlays, and models later.

Prerequisites
OS: Windows

JDK: 17 (required by the Gradle version used here)

Build tool: Gradle wrapper included in this repo (gradlew / gradlew.bat)

Ferox jars: copied into libs/ (for example):

client-57b3fe11d3.jar

injected-client-1.11.12.jar

runelite-api-1.11.12.jar

runescape-api-1.11.12.jar

protobuf-javalite-3.21.12.jar

plus support jars: LWJGL, Guava, Gson, OkHttp, logging, etc.

These jars are loaded as dependencies and contain the original, working client bytecode.

Project layout
Key files and folders:

build.gradle.kts

Configures Java 17 toolchain.

Uses the application plugin.

Adds all jars in libs/ as dependencies via fileTree.

Sets the main class to MyClientLauncher.

libs/

Contains all Ferox and supporting .jar files.

src/main/java/

MyClientLauncher.java

Small wrapper with public static void main(String[] args) that calls RuneLite.main(args).

RuneLite.java

Decompiled Ferox/RuneLite bootstrap class (entry point used by the original client).

Other decompiled packages (config, plugins, ui, etc.).

Many of these mirror upstream RuneLite/OpenOSRS code and Ferox-specific plugins.

Only the minimum set of decompiled classes needed for startup is kept; broken or unnecessary decompiled classes are removed or renamed so the project falls back to the original implementations inside the jars.

Building and running
From the project root:

bash
# Windows PowerShell or CMD
./gradlew clean run
This will:

Compile the Java sources that remain in src/main/java.

Put all jars from libs/ on the classpath.

Launch MyClientLauncher.main(...), which in turn calls RuneLite.main(...).

Open the Ferox client window and allow login to the Ferox servers (assuming the jars and configuration are correct).

If the build fails, the error log points to specific decompiled classes that may need to be removed (so the jar versions are used instead) or manually patched to match upstream RuneLite/OpenOSRS code.

Editing and adding plugins
Existing plugins live under src/main/java/plugins/... and include both standard RuneLite plugins and Ferox-specific ones (for example, plugins/ferox/FeroxPlugin.java).

New client-side plugins can be added in a custom package such as src/main/java/plugins/myplugins/ and wired into the plugin system following RuneLite’s plugin patterns.

Constraints:

All custom logic must remain on the client.

Do not add code that attempts to modify, emulate, or attack the Ferox server or other players.

Focus on overlays, UI helpers, visualizations, and other local QoL features.
