package network;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The answer to one request: named values, named tables of rows and at most one file.
 * A request id of zero marks a message the server sent on its own.
 */
public class Response {
    private int requestId;
    private String error;
    private final Map<String, String> values = new LinkedHashMap<>();
    private final Map<String, List<Map<String, String>>> tables = new LinkedHashMap<>();
    private byte[] file;

    public static Response failure(String error) {
        Response response = new Response();
        response.error = error;
        return response;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public boolean isOk() {
        return error == null;
    }

    public Response put(String key, Object value) {
        values.put(key, value == null ? "" : value.toString());
        return this;
    }

    public String get(String key) {
        return values.getOrDefault(key, "");
    }

    public List<Map<String, String>> table(String name) {
        return tables.computeIfAbsent(name, key -> new ArrayList<>());
    }

    public Row row(String table) {
        Row row = new Row();
        table(table).add(row.values);
        return row;
    }

    public byte[] getFile() {
        return file;
    }

    public Response setFile(byte[] file) {
        this.file = file;
        return this;
    }

    public static final class Row {
        private final Map<String, String> values = new LinkedHashMap<>();

        public Row put(String key, Object value) {
            values.put(key, value == null ? "" : value.toString());
            return this;
        }
    }
}
