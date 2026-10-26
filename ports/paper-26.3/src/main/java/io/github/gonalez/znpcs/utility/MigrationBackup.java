/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.utility;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Snapshot before initializing Configuration, including files not understood by this build. */
public final class MigrationBackup {
  private MigrationBackup() {}

  public static Path backup(Path folder) throws IOException {
    Files.createDirectories(folder);
    Path marker = folder.resolve(".paper-26.3-migrated");
    if (Files.exists(marker)) return null;
    List<Path> files;
    try (var walk = Files.walk(folder)) {
      files =
          walk.filter(Files::isRegularFile)
              .filter(p -> !folder.relativize(p).startsWith("backups"))
              .filter(p -> !p.equals(marker))
              .toList();
    }
    if (files.isEmpty()) return null;
    Path backup =
        folder.resolve(
            "backups/pre-paper-26.3-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS")));
    for (Path file : files) {
      Path target = backup.resolve(folder.relativize(file));
      Files.createDirectories(target.getParent());
      Files.copy(file, target, StandardCopyOption.COPY_ATTRIBUTES);
    }
    return backup;
  }

  public static void markSuccess(Path folder) throws IOException {
    Files.writeString(
        folder.resolve(".paper-26.3-migrated"),
        "ZNPCs 5.0 Paper 26.3 compatibility build initialized successfully.\n");
  }
}
