# Backend API contract (for the BE AI)

This is what the Android client expects from the server. Implement exactly
these shapes; the client code links each endpoint to its `TODO(BE)` marker.

## Conventions

- Base URL is configurable; dev default `http://10.0.2.2:8080` (see
  `AppConstants`, emulator loopback). Versioned under `/v1`.
- Auth: `Authorization: Bearer <userId>:<secret>` on every call except
  guest creation. Guest secret is issued once and never logged.
- Errors: HTTP status + JSON `{"code": "<ERROR_CODE>", "message": "..."}`.
  Codes match `protocol/errors/ErrorCode` (NOT_FOUND, NOT_MEMBER,
  RATE_LIMITED, BAD_REQUEST, …).
- All timestamps are epoch millis. Deadlines the server sends are absolute
  server times; clients also read `serverTime` to correct skew.

## Profile (implemented client-side, mocked — build these first)

### `GET /v1/profile`
Returns the signed-in user.

```json
{
  "user_id": "user-keshav",
  "display_name": "Keshav",
  "level": 12,
  "xp": 2450,
  "xp_max": 3000,
  "online": true,
  "coins": 4250,
  "gems": 120
}
```

### `GET /v1/friends`
Returns the friend list, online first (server sorts; client keeps order).

```json
[
  { "user_id": "user-neo-kid", "display_name": "NEO_KID", "online": true },
  { "user_id": "user-neo-min", "display_name": "NEO_MIN", "online": false }
]
```

## Rooms (client types exist in `protocol/`, server later)

- `POST /v1/rooms` `{ "name": "…" }` → `RoomSummary`
- `POST /v1/rooms/join` `{ "code": "ABC123" }` → `RoomSnapshot`
- `GET /v1/rooms/mine` → `MyRooms`
- WebSocket `/ws` (bearer `userId:secret`; dev-only query param for
  browser-console testing): room channel and match channel envelopes,
  snapshots with versions, `MatchStarting`, `MatchEvent`, `MatchEnded`.
  Full shapes: `protocol/room/`, `protocol/match/`.

## Voice (later)

- `GET /v1/rooms/{id}/voice-token` → `VoiceToken { url, token, expiresAt }`.
  LiveKit first, behind the client's voice interface.

## Rules for BE changes

- Never rename a `snake_case` field without bumping `ContractVersion`
  and announcing it — Android DTOs use `@SerialName` explicitly.
- Adding an optional field is a patch; a new required field is minor;
  changing/removing anything is major.
