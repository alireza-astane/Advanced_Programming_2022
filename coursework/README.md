# Original coursework, 2022

My coursework from the Advanced Programming course at Sharif University of Technology (winter and spring 2022), taught by Dr. Tefagh. Everything is in Java.

This folder is an archive. The code is kept as I submitted or left it in 2022, mostly inside the original zip files, and it has not been rebuilt since.

## Contents

| Folder | What it is | State |
|---|---|---|
| `exercise_3/` | Game of Generals, a 5×5 shogi-style game with console and Swing versions | Source and images in `src.zip` |
| `exercise_4/` | Simulator of the Coup card game: one human and three bots, in JavaFX | Gradle project in `code.zip` |
| `exercise_5/` | Four short programs on collections, matrices and stacks | Plain `.java` files |
| `exercise_6/` | Client and server for The Mind card game | **Unfinished.** The message classes exist, but there is no game logic and it does not compile |
| `Project/` | University portal modelled on the Sharif `edu` and `cw` sites: socket server, JavaFX client, Hibernate and MySQL | **Unfinished.** See below |

Each folder also has the assignment PDF, in Persian.

## Final project status

The project in `Project/ApProject.zip` has about 16,000 lines of Java and 64 screens. Known problems in this version:

- The login check does not reject wrong passwords.
- Some screens cannot open, and some buttons have no code behind them.
- Automatic refresh is turned off and offline mode is incomplete.
- No database dump is included, so a new install has no users.
- It depends on Log4j 2.14.1, which has a critical vulnerability (Log4Shell). Do not expose it to a network.

## Building the old code

These are the versions the code was written against. Newer ones will not work without changes.

- `exercise_4`: Gradle 7.2 through the included wrapper, which needs Java 16 or older.
- `Project`: Gradle 7.4 through the included wrappers (`MyCW` is the server, `MyCWClientApp` the client), which needs Java 17 or older. The server also needs a local MySQL database named `mycw`.
- `exercise_3`: unzip `src.zip`, compile with `javac`, and run `Main` or `GuiMain` from the folder that contains `resources/`.
- `exercise_5`: each file holds its own `Main` class. Copy one to `Main.java` to compile it.

## License

[MIT](../LICENSE.md)
