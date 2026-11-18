package generals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Replays games recorded from the original 2022 program, which was graded against the assignment. */
class TranscriptTest {

    static Stream<String> games() {
        return IntStream.range(0, 40).mapToObj(i -> String.format("game%02d", i));
    }

    @ParameterizedTest
    @MethodSource("games")
    void matchesOriginalProgram(String game) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Main.run(new Scanner(read(game + ".in")), out);
        assertEquals(read(game + ".out").replace("\r\n", "\n"),
                bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"));
    }

    private static String read(String name) throws IOException {
        try (InputStream in = TranscriptTest.class.getResourceAsStream("/transcripts/" + name)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
