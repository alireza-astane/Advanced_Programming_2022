#!/usr/bin/env bash
# Starts the server, opens the client, and stops the server when the client closes.
set -euo pipefail
cd "$(dirname "$0")"

port="${PORTAL_PORT:-8080}"
port_open() { (exec 3<>"/dev/tcp/127.0.0.1/$port") 2>/dev/null; }

if port_open; then
    echo "Port $port is already in use. If the portal server is running, start only the client:" >&2
    echo "  ./gradlew :client:run" >&2
    exit 1
fi

./gradlew --quiet --console=plain :server:installDist :client:installDist

server/build/install/server/bin/server &
server_pid=$!
trap 'kill "$server_pid" 2>/dev/null || true' EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

for _ in $(seq 1 120); do
    port_open && break
    kill -0 "$server_pid" 2>/dev/null || { echo "The server stopped while starting." >&2; exit 1; }
    sleep 0.5
done
port_open || { echo "The server did not start in time." >&2; exit 1; }

client/build/install/client/bin/client
