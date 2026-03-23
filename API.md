# Dual Job Dating — API Spezifikation

Base URL: `https://<host>/api`
Alle geschützten Endpunkte benötigen den Header: `Authorization: Bearer <token>`

## Auth

### POST `/auth/login`
Authentifiziert einen Studierenden und gibt ein Bearer Token zurück.

**Request Body:**
- `email` — `String` — E-Mail-Adresse des Studierenden
- `password` — `String` — Passwort im Klartext

```json
{
  "email": "student@fh.at",
  "password": "secret123"
}
```

**Response `200`:**
- `token` — `String` — Bearer Token für alle weiteren Requests
- `studentId` — `String` — UUID des Studierenden

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "studentId": "abc-123"
}
```

**Fehler:**
- `401` — E-Mail oder Passwort falsch

### POST `/auth/logout`
Invalidiert das aktuelle Token serverseitig.

**Headers:** `Authorization: Bearer <token>`
**Response `204`:** Kein Inhalt

### PATCH `/auth/password`
Ändert das Passwort des aktuell eingeloggten Studierenden.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
- `oldPassword` — `String` — Aktuelles Passwort im Klartext
- `newPassword` — `String` — Neues Passwort im Klartext

```json
{
  "oldPassword": "secret123",
  "newPassword": "newSecret456"
}
```

**Response `204`:** Kein Inhalt

**Fehler:**
- `401` — altes Passwort ist falsch

## Companies

### GET `/companies`
Gibt alle **aktiven** Unternehmen zurück. Das ist die Liste die im Swipe-Card-Stack angezeigt wird.

**Headers:** `Authorization: Bearer <token>`

**Response `200`:**
- `id` — `String` — UUID des Unternehmens
- `name` — `String` — Name des Unternehmens
- `description` — `String` — Beschreibungstext
- `industry` — `String` — Branche
- `logoUrl` — `String` — Vollständige URL zum Logo-Bild

```json
[
  {
    "id": "company-1",
    "name": "Acme GmbH",
    "description": "Wir bauen coole Sachen.",
    "industry": "Software",
    "logoUrl": "https://cdn.example.com/logos/acme.png"
  }
]
```

### GET `/companies/{id}`
Gibt das vollständige Profil eines einzelnen Unternehmens zurück. Wird für die Detailansicht vor dem Swipen verwendet.

**Headers:** `Authorization: Bearer <token>`

**Response `200`:**
- `id` — `String` — UUID des Unternehmens
- `name` — `String` — Name des Unternehmens
- `description` — `String` — Beschreibungstext
- `industry` — `String` — Branche
- `logoUrl` — `String` — Vollständige URL zum Logo-Bild

```json
{
  "id": "company-1",
  "name": "Acme GmbH",
  "description": "Wir bauen coole Sachen.",
  "industry": "Software",
  "logoUrl": "https://cdn.example.com/logos/acme.png"
}
```

**Fehler:**
- `404` — Unternehmen nicht gefunden oder inaktiv

### POST `/companies/{id}/vote`
Speichert die Bewertung des Studierenden für ein Unternehmen. Der Studierende wird über das Token identifiziert, keine `studentId` im Body nötig.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
- `vote` — `String (Enum)` — Bewertung: `LIKE`, `DISLIKE` oder `NEUTRAL`

```json
{
  "vote": "LIKE"
}
```

**Response `204`:** Kein Inhalt

**Fehler:**
- `400` — ungültiger Vote-Wert
- `404` — Unternehmen nicht gefunden oder inaktiv
- `409` — Studierender hat dieses Unternehmen bereits bewertet

## Appointments

### GET `/appointments/me`
Gibt den zugeteilten Terminplan des aktuell eingeloggten Studierenden zurück. Nur verfügbar nachdem der Admin das Matching durchgeführt hat.

**Headers:** `Authorization: Bearer <token>`

**Response `200`:**
- `id` — `String` — UUID des Termins
- `companyId` — `String` — UUID des zugeteilten Unternehmens
- `companyName` — `String` — Name des Unternehmens zur direkten Anzeige
- `timeSlot` — `String (ISO 8601)` — Datum und Uhrzeit des Termins, z.B. `2025-11-14T09:00:00Z`

```json
[
  {
    "id": "apt-1",
    "companyId": "company-1",
    "companyName": "Acme GmbH",
    "timeSlot": "2025-11-14T09:00:00Z"
  }
]
```

Gibt ein leeres Array `[]` zurück wenn das Matching noch nicht durchgeführt wurde.

**Fehler:**
- `401` — nicht authentifiziert