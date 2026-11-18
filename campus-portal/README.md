# Campus Portal

A university portal with a socket server and a JavaFX desktop client. Students, professors, department staff and the registrar each get their own pages for courses, grades, requests, coursework and messages.

I started it in 2022 as the final project of the Advanced Programming course at Sharif University of Technology. The version I submitted is kept at the [`coursework-2022`](../../tree/coursework-2022) tag.

**This is a work in progress.** The server has been rebuilt and tested. The desktop screens are still the 2022 ones, and some of them do not open yet.

## Running it

You need JDK 21. Start the server, then the client in a second terminal:

```sh
./gradlew :server:run
./gradlew :client:run
```

The first start creates a local database in `server/data` with made-up people. Every demo account uses the password `demo1234`:

| Role | Username |
|---|---|
| Student | `student.sara`, `student.omid`, `student.nika`, `student.reza`, `student.lena` |
| Professor | `prof.karimi`, `prof.moradi` |
| Head of department | `head.cs`, `head.math` |
| Deputy | `deputy.cs`, `deputy.math` |
| Registrar | `registrar` |
| Administrator | `admin` |

## Settings

| Environment variable | Default |
|---|---|
| `PORTAL_HOST` (client) | `localhost` |
| `PORTAL_PORT` | `8080` |
| `PORTAL_DB_URL`, `PORTAL_DB_USER`, `PORTAL_DB_PASSWORD` | Local H2 file |
| `PORTAL_DEMO_DATA` | `true` |

## Tests

```sh
./gradlew test
```

The server tests talk to a running server over real sockets. The client test starts the server in its own process and logs in through the login screen.

## License

[MIT](LICENSE)
