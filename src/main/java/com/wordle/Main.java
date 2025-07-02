
package com.wordle;

public class Main {
  public static void main(String[] args) {
    try {
      Wordle game = new Wordle("words.txt", 5, 5);
      game.play();

    } catch (Exception e) {
      System.err.println("An error occurred during game play: " + e.getMessage());
    }

  }
}