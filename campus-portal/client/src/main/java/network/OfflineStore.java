package network;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Where pages saved for offline use are kept: a folder in the user's home directory. */
final class OfflineStore {
    private static final Path ROOT = Path.of(System.getProperty("user.home"), ".campus-portal", "offline");

    private OfflineStore() {
    }

    static String path(String name) {
        try {
            Files.createDirectories(ROOT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return ROOT.resolve(name).toString();
    }
}
