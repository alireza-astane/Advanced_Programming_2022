package network;

import database.models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** One request being handled, with the client that sent it and its logged in user. */
public record Call(ClientHandler client, User user, List<String> args, byte[] file) {

    public String text(int index) {
        return index < args.size() && args.get(index) != null ? args.get(index).trim() : "";
    }

    public String required(int index, String field) {
        String value = text(index);
        if (value.isEmpty()) {
            throw new Problem(field + " is required.");
        }
        return value;
    }

    public int number(int index, String field) {
        try {
            return Integer.parseInt(required(index, field));
        } catch (NumberFormatException e) {
            throw new Problem(field + " must be a whole number.");
        }
    }

    public int id(int index) {
        return number(index, "Identifier");
    }

    public double decimal(int index, String field) {
        try {
            return Double.parseDouble(required(index, field));
        } catch (NumberFormatException e) {
            throw new Problem(field + " must be a number.");
        }
    }

    public LocalDateTime time(int index, String field) {
        try {
            return LocalDateTime.parse(required(index, field));
        } catch (DateTimeParseException e) {
            throw new Problem(field + " must look like 2026-12-20T09:00.");
        }
    }

    public List<String> list(int index, String separator) {
        List<String> items = new ArrayList<>();
        for (String item : text(index).split(separator)) {
            if (!item.isBlank()) {
                items.add(item.trim());
            }
        }
        return items;
    }
}
