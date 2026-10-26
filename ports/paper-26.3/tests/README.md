# Test reproduction

Build the plugin first using the module README. Run commands below from the module directory. JDK 25, the prepared exact Paper server and the compile-only annotations JAR are required. Captured results from 2026-10-26 are in `results/`.

## HTTP/provider tests (no Minecraft server)

```sh
python3 tests/run_skin.py \
  --java-home /path/to/jdk25 \
  --paper-home /path/to/prepared-paper \
  --annotations /path/to/annotations-26.0.2.jar
```

Expected: `PASS 56 skin-fetch integration checks`. The test starts an ephemeral loopback HTTP server and includes deliberate invalid responses and errors, so provider warnings are expected. It makes no real MineSkin/Mojang requests and needs no API key.

## Integration-test plugins

Use a separate disposable Paper 26.3 build 223 server. These plugins change data, make `PortTester` an operator and stop the server. Never install them on a production server or run multiple test plugins together. Build one test plugin using:

```sh
python3 tests/build_plugin.py smoke \
  --java-home /path/to/jdk25 \
  --paper-home /path/to/prepared-paper \
  --annotations /path/to/annotations-26.0.2.jar
```

Replace `smoke` with `live` or `failure` as needed. Outputs are `build/PortSmokeTest.jar`, `build/PortLiveTest.jar` or `build/PortFailureTest.jar`. Each includes the correct plugin descriptor and Mojang namespace manifest.

Prepare a fresh local server with only the snapshot and the selected test plugin in `plugins/`. Accept the Minecraft EULA, use a flat world named `world`, and bind the test server to localhost. Relevant `server.properties` values used in the recorded run:

```properties
server-ip=127.0.0.1
server-port=25575
online-mode=false
white-list=false
enforce-secure-profile=false
spawn-protection=0
level-name=world
level-type=minecraft:flat
```

Copy `fixtures/data.json` into `plugins/ServersNPC/data.json` before starting; it contains NPC 42. Let the plugin create the other default JSON files. Start Paper with Java 25, `--nogui --noconsole` and stdout/stderr redirected to a log file. Use a process timeout and inspect the result files; a process exiting alone is not proof of success.

The smoke plugin finishes after about 100 ticks and writes `smoke-result.txt`. Expect `PASS` followed by 622 successful checks. For a repeat run, restore the fixture and reset the migration marker if testing first-start backups.

For the live test, start with the same fixture, no NPC 43, and only `PortLiveTest.jar` as the test plugin. After Paper is ready, run:

```sh
cd tests/client
npm ci
node live_client.js /absolute/test-server/live-client-result.json /absolute/test-server/live-target.json
```

The client uses protocol 777 and the localhost port above. The server invokes `/znpcs skin 42 Notch` against real Mojang endpoints. Network access to the Mojang profile/session APIs is required. Expected: `live-server-result.txt` starts with `PASS` and `live-client-result.json` has `pass: true` and `skinReceived: true`. This verifies received packet data, not graphical rendering. The test includes scale, colour formatting and right/left CONSOLE actions.

For failure tests, save the exact invalid `data.json` bytes also as `failure-expected.bin` in the server root and remove `.paper-26.3-migrated`. Start with only `PortFailureTest.jar` as the test plugin. Cases used: malformed JSON (`[{ invalid broken data`) and a valid fixture with `npcEquip` set to `{"HELMET":"bm90LXNlcmlhbGl6ZWQ="}`. Expect `failure-result.txt` starting with `PASS`, disabled ServersNPC, and byte-identical input and backup. Restore the original fixture between cases.

No production TLS bypass is used. The hosted test environment required JVM proxy flags and its existing system truststore for outgoing Java HTTPS; configure your own environment normally if it needs a proxy. A normal network should not need those test-environment flags.
