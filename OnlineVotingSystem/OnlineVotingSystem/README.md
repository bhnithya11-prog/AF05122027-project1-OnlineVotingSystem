# Online Voting System — Java + JDBC + MySQL

## Project Structure

```
OnlineVotingSystem/
├── schema.sql                          ← Run this in MySQL first
├── README.md
└── src/
    ├── model/
    │   ├── User.java
    │   ├── Candidate.java
    │   └── Vote.java
    ├── dao/
    │   ├── UserDAO.java
    │   ├── CandidateDAO.java
    │   └── VoteDAO.java
    ├── util/
    │   └── DBConnection.java
    └── main/
        └── VotingApp.java
```

---

## Prerequisites

| Tool              | Version       |
|-------------------|---------------|
| JDK               | 17+           |
| MySQL Server      | 8.x           |
| MySQL Connector/J | 8.x (JAR)     |

Download the MySQL JDBC driver (connector) from:
https://dev.mysql.com/downloads/connector/j/

---

## Step 1 — Set Up the Database

Open MySQL Workbench or the MySQL CLI and run:

```sql
source /path/to/OnlineVotingSystem/schema.sql
```

This creates `online_voting_db` with three tables: `users`, `candidates`, `votes`.

---

## Step 2 — Configure DB Credentials

Open `src/util/DBConnection.java` and update:

```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/online_voting_db?useSSL=false&serverTimezone=UTC";
private static final String DB_USER     = "root";      // your MySQL username
private static final String DB_PASSWORD = "root";      // your MySQL password
```

---

## Step 3 — Running in IntelliJ IDEA

1. Open IntelliJ → **File > Open** → select the `OnlineVotingSystem` folder.
2. Right-click `src` folder → **Mark Directory as > Sources Root**.
3. Add the MySQL JDBC JAR:
   - **File > Project Structure > Modules > Dependencies > + > JARs**
   - Browse to `mysql-connector-java-X.X.X.jar` → OK.
4. Open `src/main/VotingApp.java`.
5. Click the green **Run** button (or press `Shift+F10`).

---

## Step 4 — Running in Eclipse

1. **File > New > Java Project** → Project name: `OnlineVotingSystem`.
2. Copy all source files into the `src` folder, preserving the package folders.
3. Right-click project → **Build Path > Add External Archives** → select the MySQL JAR.
4. Right-click `VotingApp.java` → **Run As > Java Application**.

---

## Step 5 — Running from Command Line

```bash
# From the OnlineVotingSystem directory
javac -cp ".:mysql-connector-java-X.X.X.jar" -d out \
  src/model/*.java src/dao/*.java src/util/*.java src/main/*.java

java -cp "out:mysql-connector-java-X.X.X.jar" main.VotingApp
```

On Windows, replace `:` with `;` in the classpath.

---

## Admin Credentials

| Username | Password  |
|----------|-----------|
| admin    | admin123  |

To change admin credentials, edit `DBConnection.java`:
```java
public static final String ADMIN_USERNAME = "admin";
public static final String ADMIN_PASSWORD = "admin123";
```

---

## Features Summary

- **User Registration** — unique username, stored password
- **User Login / Admin Login** — credential validation
- **Add / View / Delete Candidates** (admin only)
- **Vote Casting** — each user can vote exactly once (enforced in DB + app)
- **Transaction Management** — vote insert + count update + flag update are atomic (commit/rollback)
- **Real-time Results** — live vote counts, percentages, ASCII bar chart, winner declaration

---

## Generating the ZIP (manual)

You can zip the project from the command line:

```bash
# macOS / Linux
zip -r OnlineVotingSystem.zip OnlineVotingSystem/

# Windows PowerShell
Compress-Archive -Path OnlineVotingSystem -DestinationPath OnlineVotingSystem.zip
```

Or right-click the project folder in your file explorer → **Compress / Send to > Zip**.
