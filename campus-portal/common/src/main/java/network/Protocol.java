package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Reader;
import java.util.Base64;

/** Messages are single lines of JSON. Files travel inside them as Base64. */
public final class Protocol {
    public static final int DEFAULT_PORT = 8080;
    public static final int MAX_FILE_BYTES = 20 * 1024 * 1024;
    private static final int MAX_LINE_CHARS = MAX_FILE_BYTES / 3 * 4 + 1024 * 1024;

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(byte[].class, new BytesAdapter().nullSafe())
            .create();

    private Protocol() {
    }

    /** Reads one message, or returns null when the other side has closed the connection. */
    public static String readLine(Reader reader) throws IOException {
        StringBuilder line = new StringBuilder();
        int next;
        while ((next = reader.read()) != -1) {
            if (next == '\n') {
                return line.toString();
            }
            if (line.length() == MAX_LINE_CHARS) {
                throw new IOException("Message is too long");
            }
            line.append((char) next);
        }
        return line.isEmpty() ? null : line.toString();
    }

    private static final class BytesAdapter extends TypeAdapter<byte[]> {
        @Override
        public void write(JsonWriter out, byte[] value) throws IOException {
            out.value(Base64.getEncoder().encodeToString(value));
        }

        @Override
        public byte[] read(JsonReader in) throws IOException {
            return Base64.getDecoder().decode(in.nextString());
        }
    }
}
