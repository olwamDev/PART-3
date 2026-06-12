# QuickChat

A Java console application for user registration, authentication, and message management with file persistence.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Validation Rules](#validation-rules)
- [Message Hashing](#message-hashing)
- [Data Storage](#data-storage)
- [Running Unit Tests](#running-unit-tests)
- [Class Reference](#class-reference)

---

## Overview

QuickChat is a command-line messaging app built in Java. Users register an account, log in, and can send, store, or discard messages. All sent and stored messages can be searched, filtered, and managed through a dedicated menu. Message data is persisted to a local JSON file between sessions.

---

## Features

- User registration with validated username, password, and phone number
- Secure login with personalised welcome message
- Send messages to phone number recipients (max 250 characters)
- Unique 10-digit message ID and hash generated per message
- Three message actions: **Send**, **Disregard**, or **Store**
- Stored messages saved to `stored_messages.json`
- Search messages by ID or recipient
- Find the longest message across all sessions
- Delete messages by hash
- Full stored message report
- 8 built-in unit tests

---

## Project Structure

```
QuickChat/
├── Main.java               # Entry point and main menu loop
├── Login.java              # Registration and authentication logic
├── Message.java            # Message creation, sending, storage, and search
├── MessageTests.java       # Unit tests for all core features
└── stored_messages.json    # Auto-generated file for stored messages
```

> All classes are contained in a single file (`Main.java`) in the current version.

---

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- A terminal or IDE (IntelliJ IDEA, VS Code, Eclipse, etc.)

### Compile

```bash
javac Main.java
```

### Run

```bash
java Main
```

---

## Usage

### 1. Register

```
Username:  must contain an underscore and be 5 characters or fewer  (e.g. us_er)
Password:  8+ characters, 1 uppercase letter, 1 number, 1 special character (!@#$%&*)
Phone:     South African international format: +27XXXXXXXXX
```

### 2. Login

Enter your registered username and password. On success you will see:

```
Welcome Jane Doe, it is great to see you again.
```

### 3. Main Menu

```
1. Send Messages
2. Show Recently Sent Messages
3. Run Unit Tests
4. Stored Messages
5. Quit
```

### 4. Sending a Message

- Enter the recipient's number in `+27XXXXXXXXX` format
- Enter a message of up to 250 characters
- Choose to **Send**, **Disregard**, or **Store** the message

### 5. Stored Messages Menu

```
a. Display recipient of all stored messages
b. Display the longest stored message
c. Search for a message by ID
d. Search messages by recipient
e. Delete a message using its hash
f. Display full report of all stored messages
0. Back to main menu
```

---

## Validation Rules

| Field      | Rule                                                                 |
|------------|----------------------------------------------------------------------|
| Username   | Must contain `_` and be 5 characters or fewer                       |
| Password   | 8+ characters, at least 1 uppercase, 1 digit, 1 special char        |
| Phone      | Must match `+27` followed by exactly 9 digits                       |
| Message    | Maximum 250 characters; excess is reported before sending           |
| Recipient  | Must start with `+27` and be exactly 12 characters long             |

---

## Message Hashing

Each message gets a unique hash in the format:

```
XX:N:FIRSTWORDLASTWORD
```

| Part          | Description                                      |
|---------------|--------------------------------------------------|
| `XX`          | First two digits of the message ID               |
| `N`           | Message sequence number in the current session   |
| `FIRSTWORD`   | First word of the message (uppercase)            |
| `LASTWORD`    | Last word of the message, punctuation stripped   |

**Example:**  
Message: `"Hi Mike, can you join us for dinner tonight?"`  
Hash: `47:1:HITONIGHT`

---

## Data Storage

Stored messages are written to `stored_messages.json` in the working directory. Each entry uses this format:

```json
{
  "messageID": "4731829056",
  "messageHash": "47:1:HITONIGHT",
  "recipient": "+27718693002",
  "message": "Hi Mike, can you join us for dinner tonight?"
}
```

- The file is appended to when new messages are stored
- Deleting a message rewrites the entire file from the in-memory list
- The file is loaded fresh each time the Stored Messages menu is opened

---

## Running Unit Tests

From the main menu, select **option 3**. The following tests will run automatically:

| Test                        | What it checks                                              |
|-----------------------------|-------------------------------------------------------------|
| Message length              | Correct pass/fail for messages within and over 250 chars    |
| Recipient cell number       | Valid `+27` format vs invalid local format                  |
| Message hash                | Hash ends with correct `FIRSTLAST` word combination         |
| Sent messages ArrayList     | ArrayList correctly holds expected message entries          |
| Longest message             | Correct message identified from a set of test entries       |
| Search by message ID        | Correct message returned for a given ID                     |
| Search by recipient         | All messages for a given recipient are returned             |
| Delete by hash              | Message is correctly removed from the list by hash          |

All test data is cleaned up after each test to avoid polluting live session data.

---

## Class Reference

### `Login`

| Method                                                        | Description                                      |
|---------------------------------------------------------------|--------------------------------------------------|
| `checkUserName(String)`                                       | Validates username format                        |
| `checkPasswordComplexity(String)`                             | Validates password strength                      |
| `checkCellPhoneNumber(String)`                                | Validates phone number format                    |
| `registerUser(String, String, String, String, String)`        | Registers a user if all fields are valid         |
| `loginUser(String, String)`                                   | Returns true if credentials match                |
| `returnLoginStatus(String, String)`                           | Returns a welcome or error message               |

### `Message`

| Method                          | Description                                              |
|---------------------------------|----------------------------------------------------------|
| `checkMessageID()`              | Returns true if the generated ID is 10 chars or fewer    |
| `checkRecipientCell()`          | Validates recipient phone number                         |
| `checkMessageLength()`          | Validates message is within 250 characters               |
| `createMessageHash()`           | Generates the message hash string                        |
| `sentMessage(Scanner)`          | Prompts send/disregard/store and handles the action      |
| `storeMessage()`                | Writes message to `stored_messages.json`                 |
| `loadStoredMessages()`          | Reads and parses `stored_messages.json`                  |
| `storedMessagesMenu(Scanner)`   | Displays the stored messages management menu             |
| `printMessages()`               | Returns a formatted string of all sent messages          |
| `returnTotalMessages()`         | Returns the count of sent messages this session          |

---

## License

This project was created for educational purposes.
