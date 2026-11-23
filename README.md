# MultiLoader Template

A template for Minecraft mods that ship for **NeoForge** and **Fabric** from one codebase, with **one branch per Minecraft version**. Most of the code lives in `common/`, compiled against plain Minecraft; each loader module adds only its entry point and the glue to that loader.

Based on [jaredlll08/MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template), with the build of a published multi-version mod on top: the configuration cache, minimum Mixin versions enforced at compile time, mod metadata generated as an IDE-friendly source folder, jar names that never clash across branches, and GitHub Actions that build every push and publish to CurseForge, Modrinth, GitHub Releases and Maven.

## Branches

Every branch has the same layout and the same build logic; they differ in `gradle.properties`, the Loom plugin, and whatever Minecraft itself changed.

| Branch | Minecraft | Java | NeoForge | Fabric Loader | Loom | Parchment |
|---|---|---|---|---|---|---|
| `NeoFabric/1.21.1` | 1.21.1 | 21 | 21.1.235 | 0.19.5 | `fabric-loom` (remaps) | yes |
| `NeoFabric/1.21.11` | 1.21.11 | 21 | 21.11.48 | 0.20.1 | `fabric-loom` (remaps) | yes |
| `NeoFabric/26.1.2` | 26.1.2 | 25 | 26.1.2.126 | 0.20.1 | `net.fabricmc.fabric-loom` | no |
| `NeoFabric/26.2` | 26.2 | 25 | 26.2.0.93 | 0.20.1 | `net.fabricmc.fabric-loom` | no |
| `NeoFabric/26.3` | 26.3 | 25 | 26.3.0.113 | 0.20.1 | `net.fabricmc.fabric-loom` | no |

Since 26.1 Minecraft ships unobfuscated: Fabric runs on Mojang's names, so the 26.x branches have no mappings, no `mod*` configurations (`modImplementation` becomes `implementation`) and no Parchment (parameter names are included).

## Getting started

1. Rename the folder, and `rootProject.name` in `settings.gradle` to the same name (IntelliJ shows the name in brackets when they differ).
2. Fill in the `mod_*` keys in `gradle.properties`: id, name, version, group, authors, license, description and links.
3. Rename the package `com.example.examplemod` in the three modules, and update everything that names it:
   - `Constants.MOD_ID` and `MOD_NAME`;
   - the `package` in the three `*.mixins.json`;
   - the entry point in `fabric/src/main/templates/fabric.mod.json`;
   - both files in `META-INF/services/` (their name and their content).
4. Rename the files and folders named after the mod id: `examplemod.mixins.json`, `examplemod.fabric.mixins.json`, `examplemod.neoforge.mixins.json` and `common/src/main/resources/assets/examplemod/`.
5. Replace `LICENSE` (the template's own, CC0) with your mod's, and `assets/<mod_id>/icon.png` with your icon.
6. Do the same on every branch you keep. Delete the branches you don't need.

## Layout

```
build-logic/   convention plugins, shared by every module
  multiloader-common.gradle   Java toolchain, repositories, jar naming,
                              manifest, sources/javadoc jars, publishing
  multiloader-loader.gradle   compiles common/ into each loader's jar and
                              generates the mod metadata
common/        the shared code: compiled against plain Minecraft (NeoForm)
fabric/        Fabric entry point, Fabric-only mixins, fabric.mod.json
neoforge/      NeoForge entry point, NeoForge-only mixins, neoforge.mods.toml
changelogs/    one changelog-<version>.md per release (read by the workflows)
run/           game directories of the dev runs (not in git)
```

`common/` has no loader API on its classpath, so using one by mistake fails to compile instead of crashing on the other loader. What each loader does differently goes through an interface in `common/` with an implementation per loader, found by Java's `ServiceLoader` (see `Services` and `IPlatformHelper`).

## Building and running

- `./gradlew build` builds `fabric/build/libs/<mod_id>-fabric-<version>+mc<minecraft>.jar` and the NeoForge one, plus sources and javadoc jars.
- The IDE gets run configurations for both loaders: **Fabric Client/Server** and **NeoForge Client/Server/Data**. Each runs in `run/<minecraft>/<loader>/<side>/` at the root. `run/` is not in git, so all branches share it, but versions and loaders never mix mods or worlds.
- The client runs always play as `Dev`, so worlds and inventories carry over between runs.
- To test against other mods, drop their jars into the run's `mods/` folder. On the 1.21.x branches, Fabric Loader remaps them on the fly.
- `./gradlew :neoforge:runData` runs the data generators into `neoforge/src/generated/resources/`.

The build uses the configuration cache, parallel execution and the build cache (`gradle.properties`), and the Gradle daemon runs on the branch's Java version (`gradle/gradle-daemon-jvm.properties`).

## Mod metadata

`fabric.mod.json` and `neoforge.mods.toml` live in `src/main/templates/` and are generated into a source folder by the `generateModMetadata` task. A filter on `processResources` would work too, but IntelliJ cannot model that and warns about it. Placeholders are:

- every `mod_*` key in `gradle.properties` (add your own, such as `mod_discord_url`, and use them right away);
- `minecraft_version`, `minecraft_version_range`, `java_version`, `neo_version`, `neo_version_range`, `loader_version_range`, `fabric_loader_requirement` and `fabric_api_version`.

Anything else fails the build, so a typo never expands to an empty string. Values are escaped for a double-quoted string, which JSON and TOML escape the same way: a quote, a backslash or a `\n` in the description cannot produce a jar that won't load.

## Versions: built against vs. accepted

Each loader has two versions on purpose:

- `neo_version` and `fabric_loader_version` are what the jar is built against and the dev run uses. Keep them at the newest.
- `neo_version_range` and `fabric_loader_requirement` are what the jar accepts. Raising the build version then never shuts out players on an older loader.

`mixin_version` and `mixin_extras_version` are the **oldest** versions that the accepted loaders bundle, not the newest. `common/` compiles against them, so a Mixin feature too new for some players fails to compile instead of crashing their game. When you raise a loader minimum, raise these to match.

## Dependencies

Repositories are declared once, in `multiloader-common.gradle`. Each is exclusive to its groups, so Gradle never asks the wrong one, and Loom's locally generated artifacts are kept off Maven Central (its rate limit used to fail builds in Actions). Modrinth's Maven is already there, so any mod on Modrinth is `maven.modrinth:<slug>:<version>`.

- **In a loader module:** compile against the mod with `modCompileOnly` / `modImplementation` on 1.21.x, or with `compileOnly` / `implementation` on 26.x. Examples are in `fabric/build.gradle`.
- **In `common/`:** use the NeoForge variant of the mod's API, which has the same Mojang names, with `transitive = false` so NeoForge stays off `common/`'s classpath. An example is in `common/build.gradle`.
- **Access transformers:** `common/src/main/resources/META-INF/accesstransformer.cfg` is picked up automatically by `common/` and `neoforge/`.
- **Access wideners:** `common/src/main/resources/<mod_id>.accesswidener` is picked up by `fabric/`. Also list it in `fabric.mod.json`.

Every variant carries the `io.github.mcgradleconventions.loader` attribute (`common`, `fabric` or `neoforge`). That way Gradle never resolves another loader's variant, and a mod that depends on this one gets the right jar.

## Publishing to Maven

`maven-publish` publishes every module (`<mod_id>-common`, `<mod_id>-fabric`, `<mod_id>-neoforge`) with its sources and javadoc jars:

- `./gradlew publishToMavenLocal` puts them in `~/.m2`.
- `./gradlew publish` sends them to the repository in the `modMavenUrl` Gradle property. Add `modMavenUsername` and `modMavenPassword` if that repository needs them. Set these in `~/.gradle/gradle.properties` or as `ORG_GRADLE_PROJECT_*` environment variables, never in the repository's `gradle.properties`.

## GitHub Actions

All actions are pinned by commit SHA. Dependabot keeps them and the Gradle wrapper current on every branch listed in `.github/dependabot.yml`.

**`build.yml`** builds every push and pull request, and uploads the jars as an artifact. It reads the Java version, the jar names and the loaders from the branch itself, so it is the same file on every branch. It also publishes:

| To | When | Needs |
|---|---|---|
| CurseForge | `[curseforge]`, `[curseforge beta]` or `[curseforge alpha]` in the commit message, or a manual run | secret `CURSEFORGE_TOKEN`, variable `CURSEFORGE_PROJECT_ID` |
| Modrinth | `[modrinth]`, `[modrinth beta]` or `[modrinth alpha]` in the commit message, or a manual run | secret `MODRINTH_TOKEN`, variable `MODRINTH_PROJECT_ID` |

- Each loader is uploaded separately, with its own dependencies. Fabric API is required on Fabric; edit `RELATIONS` / `DEPENDENCIES` in the workflow for more.
- The changelog is the part of `changelogs/changelog-<version>.md` above the first `---`. Without that file, it is the body of the commit message.
- A `curseforge-<loader>-<mc>-<version>` or `modrinth-...` tag marks each upload, so a re-run never uploads the same file twice.
- CurseForge files are tagged Client and Server by default (`CURSEFORGE_SIDES`).

**`release.yml`** runs on a tag such as `v1.0.0+mc1.21.1`:

1. It checks that the tag matches the branch's `mod_version` and `minecraft_version`, and that `changelogs/changelog-1.0.0.md` exists.
2. It creates a GitHub Release with the jars, using that file as its text. A hyphen in the version (`1.0.0-beta.1`) marks a prerelease.
3. If the `MAVEN_URL` variable is set, it also publishes to Maven, with the `MAVEN_USERNAME` and `MAVEN_PASSWORD` secrets if the repository needs them.

The issue templates and `dependabot.yml` only take effect on the repository's default branch.

## Porting to a new Minecraft version

1. Branch off the newest branch: `NeoFabric/<new version>`.
2. In `gradle.properties`, update `minecraft_version`, `minecraft_version_range`, `neo_form_version`, `neo_version`, `neo_version_range`, `loader_version_range` and `fabric_api_version`, plus Parchment where it applies. Then update the minimum loader and Mixin versions as described above.
3. If the Java version changes, update `java_version`, the `compatibilityLevel` in the three `*.mixins.json`, and `gradle/gradle-daemon-jvm.properties` (`./gradlew updateDaemonJvm --jvm-version=<n>`).
4. Bump Loom and ModDevGradle in the root `build.gradle` if the new version needs them.
5. Add the branch to `.github/dependabot.yml` and to the version dropdown in `.github/ISSUE_TEMPLATE/bug_report.yml` on the default branch.
