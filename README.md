# Wordle Game

A Java implementation of the popular word-guessing game Wordle.

## Game Rules

- Guess the secret 5-letter word within 5 attempts
- After each guess, you receive color-coded feedback:
- **GREEN**: Correct letter in correct position
- **YELLOW**: Correct letter in wrong position
- **GRAY**: Letter not in the word
- Letters won’t show yellow if you’ve guessed more instances than exist in the answer

## Requirements

- Java 21
- Maven 3.6+

## How to Run

1. **Build the project:**

```bash
mvn clean compile
```
1. **Run the game:**

```bash
mvn exec:java
```
1. **Run tests:**

```bash
mvn test
```

## Project Structure

```
├── src/main/java/com/wordle/
│ ├── Main.java # Entry point
│ ├── Wordle.java # Game logic
│ └── Color.java # Color enum for feedback
├── src/main/resources/
│ └── words.txt # Word list
├── src/test/java/com/wordle/
│ └── WordleTest.java # Unit tests
└── pom.xml # Maven configuration
```

## Gameplay

Simply type your 5-letter guess and press Enter. The game will provide feedback using colors and prompt for your next guess until you win or run out of attempts.
