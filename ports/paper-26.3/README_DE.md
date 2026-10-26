# ZNPCs 5.4-SNAPSHOT für Paper 26.3

Inoffizieller Vorschlag für die nächste Version, auf Basis der veröffentlichten **ZNPCs 5.3**. Ziel: **Paper 26.3, Build 223 (`e415f5e`), Java 25**. Die Versionsnummer reserviert keine offizielle Veröffentlichung; darüber entscheidet der Entwickler. [English / build instructions](README.md).

## Installation und Datenübernahme

1. Server vollständig stoppen und die bisherige JAR sowie `plugins/ServersNPC/` sichern. Den ersten Start in einer Serverkopie durchführen.
2. Bisherige ZNPCs-/ServersNPC-JARs aus `plugins/` entfernen und `znpcs-5.4-SNAPSHOT-paper-26.3.jar` einsetzen.
3. Den bestehenden Ordner `plugins/ServersNPC/` beibehalten und den Server neu starten.
4. `/znpcs list`, Hologramme, Ausrüstung, Aktionen und `/znpcs skin <ID> Notch` prüfen. Kein `/reload` oder Hotloader verwenden.

Der Plugin-Name und Datenpfad bleiben `ServersNPC`. PlaceholderAPI bleibt optional. Vor dem ersten Einlesen erstellt der Port eine Sicherung unter `plugins/ServersNPC/backups/pre-paper-26.3-<Datum-Uhrzeit>/`. Bei einem Sicherungsfehler wird der Start abgebrochen. `.paper-26.3-migrated` markiert eine erfolgreiche Erstinitialisierung. Wenn bereits die vorherige Port-Fassung installiert war, bleibt deren Sicherung bestehen; vor diesem Update deshalb auch selbst den aktuellen Ordner sichern.

Die vorhandenen JSON-Dateien und `.path`-Dateien werden weiterverwendet. IDs, Positionen, gespeicherte Skin-Texturen und Signaturen, Hologramme, Aktionen, Funktionen, Ausrüstung sowie das Attributfeld aus 5.3 bleiben enthalten. Die alten Typnamen `MUSHROOM_COW` und `SNOWMAN` werden unterstützt. Unbekannte Konfigurations- und NPC-Felder bleiben beim Speichern erhalten. Beschädigtes JSON, unbekannte NPC-Typen oder nicht lesbare Ausrüstung deaktivieren das Plugin, ohne die Quelldaten durch Standardwerte zu ersetzen. Schreibvorgänge erfolgen über temporäre Dateien mit anschließendem Dateiaustausch.

Der echte Datenordner des Nutzers lag nicht vor. Die Übernahme wurde mit Beispieldaten geprüft. Der Nutzer bestätigte die grundsätzliche Funktion der vorherigen 5.0-Portierung auf seinem Server; die neue 5.3-basierte Fassung muss dort noch geprüft werden. Die gespeicherten Welten müssen geladen sein.

## Skin-Abrufe

- `/znpcs skin <ID> Spielername` und UUIDs verwenden Mojangs Profil- und Session-API. Dafür ist kein API-Schlüssel erforderlich. Ein echter Abruf von `Notch` wurde einschließlich signierter Textur und Übertragung an einen Protokollclient geprüft.
- Bild-URLs verwenden die MineSkin-V2-Warteschlange. Bei einer Autorisierungsanforderung den Schlüssel in `plugins/ServersNPC/skins.properties` unter `mineskin-api-key=` eintragen und neu starten. Alternativ `ZNPCS_MINESKIN_API_KEY` setzen; diese Umgebungsvariable hat Vorrang. MineSkin V2 wurde mit lokalen HTTP-Testantworten geprüft, nicht mit einem echten Schlüssel.
- HTTP-Fehler, ungültige Antworten und Ratenlimits erscheinen mit Anbieter und Ursache in der Konsole. Abrufe erfolgen außerhalb des Serverthreads; NPC-Änderungen auf dem Serverthread. Ein Skin-Wechsel erneuert die Profil-UUID und sendet das Profil erneut, damit der Client den alten Skin nicht weiter aus dem Cache verwendet.

## Änderungen aus 5.1 bis 5.3

Die Grundlage ist das vollständige 5.3-Binary. Alle nicht ersetzten Klassen werden daraus bytegenau übernommen. Damit sind die Änderungen seit 5.0 enthalten, statt nur die alte Portierung umzubenennen:

| Veröffentlichung | Übernahme in diesen Vorschlag |
| --- | --- |
| 5.1 | Skin-Anbieter-API und Erfolgsmeldung; Anpassungen an Customizations; Farbformatierung in Konversationen. Skin-Abrufe wurden zusätzlich repariert. |
| 5.2 | `/znpcs scale`, gespeicherte Bukkit-Attribute und Übertragung des Skalierungsattributs an den Client. |
| 5.3 | Vollständige unveränderte Klassen aus 5.3 und deren öffentliche Methoden/Modelle als Grundlage. |

Die historischen Serverziele aus 5.1–5.3 werden dadurch nicht zu unterstützten Zielen dieser JAR. Dieser Port verwendet native Paper-26.3-Klassen. Der konkrete Umfang, die Herkunft und Grenzen stehen in [README.md](README.md) und [VALIDIERUNG.md](VALIDIERUNG.md).
