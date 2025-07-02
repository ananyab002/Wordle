package com.wordle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class Wordle {
  private final int wordLength;
  private final int maxGuesses;
  private final List<String> words;
  private final String answer;
  private final Scanner scanner = new Scanner(System.in);
  private int guesses = 0;

  public Wordle(String wordFile, int wordLength, int maxGuesses) {
    this.wordLength = wordLength;
    this.maxGuesses = maxGuesses;
    this.words = loadWords(wordFile);
    this.answer = words.get(new Random().nextInt(words.size()));
  }

  private List<String> loadWords(String filename) {
    List<String> loadedWords = new ArrayList<>();
    try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(filename)) {
      if (resourceStream != null) {
        loadedWords = new BufferedReader(new InputStreamReader(resourceStream)).lines().toList();
      } else {
        loadedWords = List.of("WATER", "OTTER", "HOUND", "PIZZA", "EAGLE", "FRUIT", "PAPER");
      }
    } catch (IOException e) {
      System.err.println("An error occurred while loading words: " + e.getMessage());
    }

    loadedWords.stream()
        .map(String::trim)
        .map(String::toUpperCase)
        .filter(w -> w.length() == wordLength)
        .toList();
    if (loadedWords.isEmpty()) {
      throw new RuntimeException("No words found with length " + wordLength + " in file: " + filename);
    }
    return loadedWords;
  }

  public void play() {

    displayInstructions();

    while (guesses < maxGuesses) {
      System.out.printf("Attempt %d/%d: ", guesses + 1, maxGuesses);

      String input = scanner.nextLine();
      Optional<String> validatedGuess = validateInput(input);

      if (validatedGuess.isEmpty()) {
        continue;
      }

      String guess = validatedGuess.get();

      if (guess.equals(answer)) {
        System.out.println("\nCongratulations! Your guess " + Color.GREEN.getCode() + guess + Color.GRAY.getCode() +
            " was guessed in " + ++guesses + " guesses!\n");
        return;
      }

      Color[] guessHints = evaluate(guess, answer);
      displayguessHints(guess, guessHints);

      guesses++;
    }

    System.out.println("\n Ah! so close, you are out of guesses! The word was: " + answer);
  }

  Optional<String> validateInput(String input) {
    if (input == null) {
      return Optional.empty();
    }

    String guess = input.toUpperCase().trim();

    if (guess.length() != wordLength) {
      System.out.println("Please enter a " + wordLength + " - letter word.");
      return Optional.empty();
    }

    boolean isValidWord = guess.chars().allMatch(Character::isLetter);
    if (!isValidWord) {
      System.out.println("Please enter only letters.");
      return Optional.empty();
    }

    return Optional.of(guess);
  }

  public Color[] evaluate(String guess, String answer) {
    int len = guess.length();
    Color[] guessHints = new Color[len];
    Map<Character, Integer> mismatchedCharMap = new HashMap<>();

    for (int i = 0; i < len; i++) {
      char currentAnswerChar = answer.charAt(i);
      if (guess.charAt(i) == currentAnswerChar) {
        guessHints[i] = Color.GREEN;
      } else {
        int mismatchedAnswerCount = mismatchedCharMap.getOrDefault(currentAnswerChar, 0);
        mismatchedCharMap.put(currentAnswerChar, mismatchedAnswerCount + 1);
      }
    }

    for (int i = 0; i < len; i++) {

      if (guessHints[i] != null)  continue;

      char currentGuessChar = guess.charAt(i);
      int mismatchedGuessCount = mismatchedCharMap.getOrDefault(currentGuessChar, 0);
      if (mismatchedGuessCount > 0) {
        guessHints[i] = Color.YELLOW;
        mismatchedCharMap.put(currentGuessChar, mismatchedGuessCount - 1);
      } else {
        guessHints[i] = Color.GRAY;
      }

    }
    return guessHints;
  }

  private void displayInstructions() {
    System.out.println("\n         === Welcome to WORDLE! ===");
    System.out.println("Rules:");
    System.out.printf("* You have %s guesses to find the secret word\n", maxGuesses);
    System.out.println("* After each guess, you'll get one of the below feedback:");
    System.out.println(Color.GREEN.getCode() + "  GREEN: Correct letter in correct position");
    System.out.println(Color.YELLOW.getCode() + "  YELLOW: Correct letter in wrong position" + Color.GRAY.getCode());
    System.out.println("* Letters won't show yellow if you have more");
    System.out.println("  of them than in the answer word");
    System.out.println("=".repeat(50));
    System.out.println("\nLet's begin!");
    System.out.println("\nGuess the " + wordLength + " - letter word!");
  }

  private static void displayguessHints(String guess, Color[] colors) {
    for (int i = 0; i < guess.length(); i++) {
      System.out.print(colors[i].getCode() + guess.charAt(i) + " " + Color.GRAY.getCode());
    }
    System.out.println();
  }
}