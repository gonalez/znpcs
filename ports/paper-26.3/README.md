# ZNPCs 5.4-SNAPSHOT — Paper 26.3 proposal

This is an independent compatibility proposal based on the published **ZNPCs 5.3** binary. It targets **Paper 26.3 build 223 (`e415f5e`), API `26.3.build.223-stable`, Java 25**. `5.4-SNAPSHOT` identifies the proposed next development version; it is not an official upstream release. [German installation instructions](README_DE.md).

## Why a separate module?

The public `master` branch is an incomplete rewrite and does not contain all code shipped in the 5.3 release (see upstream issue #150). This module reconstructs only the classes requiring changes from the released binary and overlays their compiled replacements onto that exact binary. Unchanged classes are copied byte-for-byte. It does not replace or complete the rewrite at the repository root, and the root Gradle build does not build this module.

ZNPCs is by Gaston Gonzalez (Gonalez / ZNetwork), under Apache-2.0. See `LICENSE` and `NOTICE`. Changed Java classes are marked as a compatibility port. The work and tests were AI-assisted. Maintainer review and testing on real server data remain necessary before any upstream release.

## Baseline and retained release changes

Source release: [Spigot resource 80940](https://www.spigotmc.org/resources/znpcs.80940/), version **5.3**, published 2026-02-01, Spigot version ID **624359**. See the [official update notes](https://www.spigotmc.org/resources/znpcs.80940/updates). The build input was obtained through the Spiget mirror because the direct Spigot download required access unavailable in the build environment. Metadata identifies it as 5.3; its exact bytes are pinned below. A matching maintainer-supplied binary is preferable. No upstream or server binary is committed here.

| Release | Relevant behavior retained |
| --- | --- |
| 5.1 | Provider-based skin API and provider success message; customization changes; conversation chat colour formatting. |
| 5.2 | `/znpcs scale` and serialized Bukkit attributes. Updated spawn code sends the native attributes packet on Paper 26.3. |
| 5.3 | Complete released baseline, including unchanged commands, models, conversations, placeholders and integrations. Version helper and modified NPC methods retain the 5.3 calling interfaces. |

`commands/list/DefaultCommand.class`, `npc/NPCModel.class`, `npc/conversation/ConversationProcessor.class` and `utility/PlaceholderUtils.class` were explicitly checked byte-for-byte against 5.3. Earlier release notes also describe support for earlier Minecraft versions; **this native port supports only the stated Paper 26.3 target**.

## Compatibility and skin fixes

- Native spawn, remove, movement, metadata, equipment, team, player-info and attribute packets for Paper 26.3; immutable Authlib profiles; separate attack/interact packet handling.
- Player NPCs remain packet entities. Their profiles are available for skin rendering while hidden from the player list. Skin replacement changes the profile UUID, removes the old profile/entity and respawns for current viewers.
- Player names and UUIDs use Mojang's profile/session APIs with signed textures. HTTP image URLs use the MineSkin V2 queue, including pending/completed jobs. The old request/response assumptions are replaced. Scheduled skin refresh is active again.
- Bounded background skin work, HTTP timeouts, bounded responses and provider-specific failures; successful NPC updates return to the server thread. Normal TLS certificate verification remains enabled.
- Before-read migration backup; fail-closed JSON/item loading; original JSON model, unknown NPC/configuration fields and unloaded-world models retained; file replacement after serialization.
- Legacy entity names and registry-backed customizations, main-thread NPC/save tasks and click-handler lifecycle adapted to current Paper.

No API key is needed for names/UUIDs. For MineSkin image URLs, an optional key can be set in `plugins/ServersNPC/skins.properties` (`mineskin-api-key=`) or `ZNPCS_MINESKIN_API_KEY` (takes precedence), then restart. Some MineSkin access levels require a key. No key or trust-store setting is shipped in the plugin.

## Build

Inputs:

| Input | Exact version / SHA-256 |
| --- | --- |
| ZNPCs baseline | 5.3 — `e26b8d912fee7177e2ec5342c28157cf588b96a4c5052f9402b0755fee9426b1` |
| Paper bootstrap | 26.3 build 223 — `f8e684a4fef2a8b46d03aac049b49d59275e9ce5367dba7c6075cb5780f38470` |
| Prepared Paper server | `versions/26.3/paper-26.3.jar` — `6552d037cb5ead1634498cf28d5ac2d63ffee9125867b47db5de8b2e48c4756d` |
| Compile-only dependencies | PlaceholderAPI 2.11.5; JetBrains annotations 26.0.2 |

Obtain Paper build 223 from the [Paper download API](https://fill.papermc.io/v3/projects/paper/versions/26.3/builds/223). Prepare it in a separate folder with JDK 25 using `java -Dpaperclip.patchonly=true -jar paper-26.3-223.jar`. The folder must then contain `versions/26.3/paper-26.3.jar` and `libraries/**/*.jar`.

From this module directory:

```sh
python3 build.py \
  --java-home /path/to/jdk25 \
  --base-jar /path/to/znpcs-5.3.jar \
  --paper-home /path/to/prepared-paper-folder \
  --placeholder-api /path/to/placeholderapi-2.11.5.jar \
  --annotations /path/to/annotations-26.0.2.jar
```

Output: `build/znpcs-5.4-SNAPSHOT-paper-26.3.jar`. The builder verifies the baseline and prepared Paper hashes, compiles with `--release 25`, preserves unmodified classes and creates a deterministic archive with the Mojang namespace manifest. Tests and compile-only dependencies are not bundled. There is currently a compile warning for Paper's deprecated `Attribute.valueOf`; the target API still provides it.

## Validation and remaining work

See [VALIDIERUNG.md](VALIDIERUNG.md) and [tests/README.md](tests/README.md). Completed: 622 server checks, 56 HTTP/provider checks, a protocol-777 client session, a real signed Mojang skin lookup via the original `/znpcs skin` command, scale/colour checks, click actions, and failure tests preserving damaged input files.

No graphical Minecraft client was used. The actual user's data folder was not supplied. Real MineSkin generation with an API key, proxy switching, external integrations and every possible NPC function/conversation/path require further testing. Earlier in-game confirmation applied to the previous 5.0-based port, not this rebased snapshot. This module is offered as a draft for maintainer review, not as a claim of broad server compatibility.
