# PokeMed_Quest

## Team Members

* Joris Brugman
* Erwin Liem
* Dennis Dzoba
* Inge de Mars

## Project Context

This is a group project for the Software Engineering course (Block 3) at Zuyd Hogeschool.

## Introduction

Juvenile Dermatomyositis (JDM) is a rare condition causing muscle weakness in children. Monitoring its progression relies heavily on the Children's Myositis Assessment Scale (CMAS) test, which involves specific exercises. While effective for data gathering, the repetitive nature of CMAS testing can be perceived as mundane and discouraging for young patients, potentially limiting their willingness to perform the tests consistently.

## Problem Statement

The core challenge addressed by this project is the reluctance of children to consistently perform CMAS exercises, especially during at-home monitoring. This inconsistency can limit the frequency and volume of data available for doctors to effectively track disease progression and tailor treatments. There is a need for a solution that increases patient engagement with the testing process.

## Proposed Solution

PokeMed_Quest aims to transform the CMAS testing experience into a more enjoyable and motivating activity for children diagnosed with JDM. Our proposed solution involves a Java application designed to:

* **Increase Engagement:** Utilize gamification elements, such as progress tracking and rewards.
* **Foster Connection:** Implement a customizable virtual avatar or pet that the child can personalize.
* **Enable Monitoring:** Store user data, test progress, and avatar details securely using an SQLite database.
* **Support Clinicians (Future Goal):** The concept includes future potential for a dashboard allowing doctors to review patient progress and an anomaly detection feature to highlight unusual data patterns.

## Prototype Scope (Current MVP)

This repository contains the Minimum Viable Product (MVP) focused on the core backend logic and a **Command-Line Interface (CLI)**, developed according to the initial project phase requirements. Key features implemented in this prototype include:

* **Command-Line Interface (CLI):** All user interactions occur via text commands in the terminal.
* **User Authentication:** Basic login functionality for different user roles (e.g., child patient, administrator/doctor).
* **Text-Based Avatar Customization:** Logic allowing users to manage avatar attributes through CLI commands.
* **SQLite Database Integration:** Use of an SQLite database (`data/application.db`) for persisting user, avatar, and progress information.

## Extra

* **Basic Anomaly Finder Logic:** A rudimentary implementation demonstrating the concept of data anomaly detection, likely presenting findings via CLI output.
* **Progress Tracking:** Functionality to record and display CMAS test progress data via the CLI.

## Technology Stack

* **Primary Language:** Java (JDK 11 or later recommended)
* **Database:** SQLite

## Getting Started

### Prerequisites

* Java Development Kit (JDK) - Version 11 or later is recommended.
* [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc/releases) JAR file. Download the latest version.
* Git (for cloning the repository).

### Setup

1.  **Clone the repository:** Replace `[Your Repository URL]` with the actual URL.
    ```bash
    git clone [Your Repository URL]
    cd PokeMed_Quest
    ```
2.  **Add JDBC Driver:** Place the downloaded `sqlite-jdbc-XYZ.jar` (replace XYZ with the actual version number) file inside the `lib/` directory in the project root.
3.  **Initialize Database (If applicable):**
    * The application might attempt to create the necessary tables automatically if the `data/application.db` file doesn't exist.
    * Alternatively, you can manually create the database structure using a tool like [DB Browser for SQLite](https://sqlitebrowser.org/) and executing the SQL commands found in `src/main/resources/schema.sql`.

### Compilation

You need to compile the Java source files, including the SQLite JDBC driver in the classpath.

```bash
# Make sure you are in the project's root directory (PokeMed_Quest)
# Adjust the JAR filename to match the one you downloaded.
# This example assumes Windows path separators for classpath (-cp) might need adjustment for Git Bash/WSL slightly.
javac -cp "lib/sqlite-jdbc-poke.jar" -d "target/classes" src/main/java/com/pokemedquest/*.java src/main/java/com/pokemedquest/*/*.java

# Explanation:
# -cp "lib/sqlite-jdbc-poke.jar" : Sets the classpath to include the JDBC driver. Use ; on Windows CMD, : on Git Bash/Linux/Mac.
# -d "target/classes" : Specifies the output directory for compiled .class files.
# src/main/java/.../*.java : Specifies the source files to compile (adjust pattern if needed).