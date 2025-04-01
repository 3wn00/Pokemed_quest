# PokeMed_Quest

## Team Members

* Joris Brugman
* Erwin Liem
* Dennis Dzoba
* Inge de Mars

## Project Context

This is a group project for the Software Engineering course (Block 3) at Zuyd Hogeschool. (Current Phase: MVP Implementation as of April 1, 2025).

## Introduction

Juvenile Dermatomyositis (JDM) is a rare condition causing muscle weakness in children. Monitoring its progression relies heavily on the Children's Myositis Assessment Scale (CMAS) test, which involves specific exercises. While effective for data gathering, the repetitive nature of CMAS testing can be perceived as mundane and discouraging for young patients, potentially limiting their willingness to perform the tests consistently.

## Problem Statement

The core challenge addressed by this project is the potential reluctance of children to consistently perform CMAS exercises, especially during at-home monitoring. This inconsistency can limit the frequency and volume of data available for doctors to effectively track disease progression and adjust treatment. There is a need for a solution that makes the testing process more engaging.

## Proposed Solution

PokeMed_Quest aims to transform the CMAS testing experience into a more enjoyable and motivating activity for children diagnosed with JDM. Our proposed solution involves a Java application incorporating:

* **Gamification:** Elements like progress tracking and rewards to encourage participation.
* **Customizable Avatar:** A virtual pet or avatar that the child can personalize, fostering engagement.
* **Data Management:** Secure storage of user data, progress, and avatar details using an SQLite database.
* **Doctor's Interface (Future Goal):** Concepts include a dashboard for doctors to review progress and potentially an anomaly detection feature to flag unusual data points.

## Prototype Scope (Current MVP)

This repository contains the Minimum Viable Product (MVP) focused on the core backend logic and a **Command-Line Interface (CLI)**, as per the initial project phase requirements. Key features implemented in this prototype include:

* **Command-Line Interface (CLI):** All interactions occur via text commands.
* **User Authentication:** Basic login for different user roles (e.g., child, admin).
* **Text-Based Avatar Customization:** Logic for managing avatar attributes via the CLI.
* **Progress Tracking:** Recording and displaying CMAS test progress via the CLI.
* **SQLite Database Integration:** Persistence of user, avatar, and progress data.
* **Basic Anomaly Finder Logic:** (Concept exists, basic implementation pending).

## Project Status / To-Do (As of April 1, 2025)

This section tracks the progress of the MVP development.

**Completed (`[x]`):**

* [x] Project Setup (Folders, Git Repository, `.gitignore`).
* [x] Database Schema Definition (`schema.sql` created, tables generated in `application.db`).
* [x] Core Data Models Defined (`User.java`, `Avatar.java`, `TestProgress.java`).
* [x] Data Access Object (DAO) Layer Implemented (`UserDao`, `AvatarDao`, `TestProgressDao` with basic methods).
* [x] Service Layer Implemented (`AuthService`, `AvatarService`, `ProgressService` with core logic).
* [x] Basic Command-Line Interface (CLI) Structure (`Main.java`, `CliHandler.java`).
* [x] MVP Feature: User Registration (via CLI).
* [x] MVP Feature: User Login (via CLI).
* [x] MVP Feature: Default Avatar Creation upon registration (for child role).
* [x] MVP Feature: View Avatar details (via CLI).
* [x] MVP Feature: Basic Avatar Customization (name, color, accessory via CLI).
* [x] MVP Feature: Record Test Progress (CMAS Score via CLI).
* [x] MVP Feature: View Test Progress History (via CLI).

**Current / To-Do (`[ ]`):**

* [ ] **CRITICAL SECURITY:** Implement proper **Password Hashing** (e.g., BCrypt) in `AuthService` for registration and login verification.
* [ ] Code Review & Merge: Review the Pull Request containing the initial working code and merge it into the `develop` (or `main`) branch.
* [ ] Expand CLI Features:
    * [ ] Implement Admin/Doctor menu options (e.g., view patient lists, view specific patient data).
    * [ ] Add more robust input validation and error handling in `CliHandler`.
    * [ ] Improve output formatting for better readability.
* [ ] Implement Anomaly Finder Logic (basic version) in `ProgressService` and display results via CLI.
* [ ] Refine DAO Layer: Implement `update`/`delete` methods in DAOs where necessary.
* [ ] Testing:
    * [ ] Write Unit Tests (JUnit) for critical Service and DAO methods.
    * [ ] Perform thorough Manual Testing of all features and edge cases.
    * [ ] Bug Fixing based on testing results.
* [ ] Documentation:
    * [ ] Complete the main Project Report document (`docs/Project_Report.docx`) including design diagrams (UML), implementation details, and testing results.
    * [ ] Finalize `README.md`, ensuring "Getting Started" instructions are accurate and complete.
* [ ] Prepare Final Presentation / Demo of the working MVP.

## Technology Stack

* **Primary Language:** Java (JDK 11 or later recommended)
* **Database:** SQLite

## Getting Started

### Prerequisites

* Java Development Kit (JDK) (Specify version, e.g., JDK 11 or later)
* [SQLite JDBC Driver JAR file](https://github.com/xerial/sqlite-jdbc/releases).
* Git (for cloning the repository).
* An SQLite Database Tool (like DB Browser for SQLite - recommended for setup).

### Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/3wn00/Pokemed_quest
    cd PokeMed_Quest
    ```
2.  **Add JDBC Driver:** Download the [SQLite JDBC driver](https://github.com/xerial/sqlite-jdbc/releases) JAR file and place it inside the `lib/` directory in the project root. **Make sure the filename matches the one specified in compile/run commands.**
3.  **Initialize Database:**
    * Use an SQLite tool (like DB Browser for SQLite) to open/create the `data/application.db` file.
    * Copy the SQL commands from `src/main/resources/schema.sql` and execute them using the tool to create the necessary tables (`users`, `avatars`, `test_progress`).
    * Save the changes to the database file.

### Compilation

*(Note: Using an IDE like IntelliJ IDEA or Eclipse with Maven/Gradle support is strongly recommended for easier dependency management and building.)*

If compiling manually from the project root directory:
```bash
# Adjust JAR filename and classpath separator (';' for Win CMD/PS, ':' for Bash/WSL/Mac)
javac -cp "lib/sqlite-jdbc-XYZ.jar" -d "target/classes" src/main/java/com/pokemedquest/*.java src/main/java/com/pokemedquest/*/*.java
# OR (if wildcards fail, list explicitly):
# javac -cp "lib/sqlite-jdbc-XYZ.jar" -d "target/classes" src/main/java/com/pokemedquest/Main.java src/main/java/com/pokemedquest/model/*.java src/main/java/com/pokemedquest/dao/*.java src/main/java/com/pokemedquest/service/*.java src/main/java/com/pokemedquest/cli/*.java src/main/java/com/pokemedquest/util/*.java
