# Validierung am 26.10.2026

Fassung: `znpcs-5.4-SNAPSHOT-paper-26.3.jar`, Grundlage ZNPCs **5.3**.
Zielserver: Paper **26.3-223-main@e415f5e**, API **26.3.build.223-stable**.
Laufzeit: Eclipse Temurin **25.0.5+8**, separater localhost-Testserver.

Der Test-Build hatte SHA-256 `e5801ae24cbcfc5258ab6c4656e396d0ca79d7042fafebf530171fe5c5571de0`. Danach wurde ausschließlich der Herkunftshinweis `NOTICE` in der JAR aktualisiert. Alle `.class`-Dateien des ausgelieferten Builds werden bytegenau mit diesem getesteten Build verglichen. Die abschließende JAR-Prüfsumme steht in `tests/results/artifact.json`.

| Prüfung | Ergebnis |
| --- | --- |
| Kompilieren gegen das konkrete Paper-Binary; Plugin aktivieren und geordnet stoppen | Bestanden |
| 50 NPC-Typen erzeugen; native Spawn-, Metadaten-, Teleport-, Team- und Ausrüstungspakete kodieren | Bestanden |
| 5.0-JSON-Testdaten: ID, Position, Hologramme, signierter Skin, Aktionen und Einstellungen einlesen | Bestanden |
| Unbekannte NPC-Felder und Ausrüstungs-Metadaten erhalten | Bestanden |
| Alte Villager-Berufe und Katzenvarianten über aktuelle Registries | Bestanden |
| 5.3-Modell mit Attributen; `/znpcs scale` und natives Attributpaket | Bestanden |
| Unveränderte 5.3-Command-, NPCModel-, ConversationProcessor- und PlaceholderUtils-Klassen | Bytegenau identisch |
| Konversations-Farbformatierung und 5.3-Version-ABI | Bestanden |
| Skin-HTTP-Tests: Namen/UUIDs, signierte Properties, Routing, Fehler, Fallbacks, Callbacks und Queue-Ablehnung | Bestanden |
| MineSkin V2: 200/202, laufender Job, abgeschlossener Job mit Ergebnisabruf, Nicht-UUID-Job-ID, Autorisierungsfehler, Fehlschlag und Timeout | Bestanden mit lokalen Antworten |
| Echter `/znpcs skin 42 Notch`-Abruf über Mojang, neue Profil-UUID, signierte Textur und Empfang beim Testclient | Bestanden |
| Protokoll-777-Client: Anmeldung, Spieler-/Mob-NPC, Hologramme und Ausrüstung | Bestanden |
| `/znpcs create`, `/znpcs type`, Links-/Rechtsklick-Event und zugehörige CONSOLE-Aktion | Bestanden |
| Erststart-Sicherung | Bestanden |
| Defektes JSON und nicht lesbare Ausrüstung: deaktiviert, Originaldatei und Sicherung bytegenau erhalten | Bestanden |
| Archiv-Integrität, eindeutige Einträge, Lizenz/Herkunft und keine Testklassen im Plugin | Bestanden |

Der interne Servertest enthält **622 Prüfungen**, einschließlich der wiederholten Paketprüfungen für alle NPC-Typen und Ausrüstungsslots. Der separate HTTP-Test enthält **56 Prüfungen**. Resultate stehen unter `tests/results/`. Der Netzwerktest verwendet `@wp2508/minecraft-protocol` 1.69.5 mit Minecraft-Daten 3.111.7, keinen grafischen Client.

Die Testumgebung benötigt für ausgehende Java-HTTPS-Verbindungen ihren lokalen Netzwerk-Proxy und den vorhandenen System-Truststore. Diese JVM-Startoptionen betreffen ausschließlich den Testserver. Im Plugin wurde weder die TLS-Prüfung abgeschaltet noch eine Proxy-/Zertifikatskonfiguration eingebaut.

## Grenzen

- Der konkrete Datenordner des Nutzers lag nicht vor. Alle Konversationen, Pfade und Funktionen müssen zusätzlich in einer Serverkopie geprüft werden.
- Kein visueller Skin-Test im grafischen Client; überprüft wurden signierte Daten, Profilwechsel und Übertragung an den Protokollclient.
- MineSkin wurde mit lokalen V2-Antworten geprüft. Eine echte Generierung mit API-Schlüssel steht aus.
- Proxy-Serverwechsel, PlaceholderAPI und weitere externe Integrationen wurden nicht umfassend geprüft.
- Keine Freigabe für andere Paper-Builds, Minecraft-Versionen oder Serverfamilien.

Die Test-Plugins beenden ihren Server selbst und verändern Testdaten. Sie gehören ausschließlich in einen separaten Testserver; siehe `tests/README.md`.
