package com.wordle;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WordleTest {

    private Wordle wordle;
    private static final int WORD_LENGTH = 5;
    private static final int MAX_GUESSES = 5;

    @BeforeEach
    void setUp() {
        wordle = new Wordle("words.txt", WORD_LENGTH, MAX_GUESSES);
    }

    @Test
    @DisplayName("Test exact match - all letters correct (GREEN)")
    void testExactMatch() {
        String guess = "WATER";
        String answer = "WATER";
        Color[] result = wordle.evaluate(guess, answer);
        Color[] expected = { Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN };

        assertArrayEquals(expected, result, "All letters should be GREEN for exact match");
    }

    @Test
    @DisplayName("Test no match - no letters in common (GRAY)")
    void testNoMatch() {
        String guess = "BRAIN";
        String answer = "HOUSE";
        Color[] result = wordle.evaluate(guess, answer);
        Color[] expected = { Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY };

        assertArrayEquals(expected, result, "All letters should be GRAY when no letters match");
    }

    @Test
    @DisplayName("Test partial match - some letters in wrong positions (YELLOW)")
    void testPartialMatch() {
        String guess = "STEAM";
        String answer = "MEATS";
        Color[] result = wordle.evaluate(guess, answer);
        Color[] expected = { Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.YELLOW };

        assertArrayEquals(expected, result, "All letters should be YELLOW when all exist but in wrong positions");
    }

    @Test
    @DisplayName("Test mixed match - combination of GREEN, YELLOW, and GRAY")
    void testMixedMatch() {
        String guess = "WATER";
        String answer = "WANDS";
        Color[] result = wordle.evaluate(guess, answer);
        Color[] expected = { Color.GREEN, Color.GREEN, Color.GRAY, Color.GRAY, Color.GRAY };

        assertArrayEquals(expected, result, "Should handle mixed GREEN, YELLOW, and GRAY correctly");
    }

    @Test
    @DisplayName("Test duplicate letters - more guesses than answer contains")
    void testDuplicateLettersExcess() {
        String guess = "SPEED";
        String answer = "ERASE";
        Color[] result = wordle.evaluate(guess, answer);
        Color[] expected = { Color.YELLOW, Color.GRAY, Color.YELLOW, Color.YELLOW, Color.GRAY };

        assertArrayEquals(expected, result,
                "Should handle duplicate letters correctly - only count actual occurrences");
    }

    @Test
    @DisplayName("Test duplicate letters - exact count match")
    void testDuplicateLettersExactCount() {
        String guess = "APPLE";
        String answer = "PUPPY";
        Color[] result = wordle.evaluate(guess, answer);
        Color[] expected = { Color.GRAY, Color.YELLOW, Color.GREEN, Color.GRAY, Color.GRAY };

        assertArrayEquals(expected, result, "Should match available letter count in answer");
    }

    @Test
    @DisplayName("Test duplicate letters - green takes priority")
    void testDuplicateLettersGreenPriority() {
        String guess = "ALLEY";
        String answer = "LLAMA";
        Color[] result = wordle.evaluate(guess, answer);
        Color[] expected = { Color.YELLOW, Color.GREEN, Color.YELLOW, Color.GRAY, Color.GRAY };

        assertArrayEquals(expected, result, "GREEN matches should be processed before YELLOW");
    }

    @Test
    @DisplayName("Test input validation - correct input")
    void testValidInput() {
        String input = "water";

        Optional<String> result = wordle.validateInput(input);

        assertTrue(result.isPresent());
        assertEquals("WATER", result.get());
    }

    @Test
    @DisplayName("Test input validation - null input")
    void testNullInput() {
        String input = null;

        Optional<String> result = wordle.validateInput(input);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test input validation - wrong length short")
    void testWrongLengthShortInput() {
        String input = "cat";

        Optional<String> result = wordle.validateInput(input);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test input validation - wrong length long")
    void testWrongLengthLongInput() {
        String input = "elephant";

        Optional<String> result = wordle.validateInput(input);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test input validation - input with numbers")
    void testInputWithNumbers() {
        String input = "wat3r";

        Optional<String> result = wordle.validateInput(input);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test input validation - input with special characters")
    void testInputWithSpecialCharacters() {
        String input = "wat-r";

        Optional<String> result = wordle.validateInput(input);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test input validation - whitespace handling")
    void testWhitespaceInput() {
        String input = " water ";

        Optional<String> result = wordle.validateInput(input);

        assertTrue(result.isPresent());
        assertEquals("WATER", result.get());
    }

    @Test
    @DisplayName("Rule Test: Letters won't show yellow if you have more than in answer")
    void testExcessLettersRule() {
        String guess = "AAABC";
        String answer = "AXYZA";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.GREEN, result[0]);
        assertEquals(Color.YELLOW, result[1]);
        assertEquals(Color.GRAY, result[2]);
        assertEquals(Color.GRAY, result[3]);
        assertEquals(Color.GRAY, result[4]);
    }

    @Test
    @DisplayName("Rule Test: Complex duplicate letter scenario")
    void testComplexDuplicateScenario() {
        String guess = "GEESE";
        String answer = "THESE";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.GRAY, result[0]);
        assertEquals(Color.GRAY, result[1]);
        assertEquals(Color.GREEN, result[2]);
        assertEquals(Color.GREEN, result[3]);
        assertEquals(Color.GREEN, result[4]);
    }

    @Test
    @DisplayName("Rule Test: All same letter guess")
    void testAllSameLetterGuess() {
        String guess = "AAAAA";
        String answer = "ABOUT";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.GREEN, result[0]);
        assertEquals(Color.GRAY, result[1]);
        assertEquals(Color.GRAY, result[2]);
        assertEquals(Color.GRAY, result[3]);
        assertEquals(Color.GRAY, result[4]);
    }

    @Test
    @DisplayName("Rule Test: Multiple yellows for same letter")
    void testMultipleYellowsForSameLetter() {
        String guess = "BOOKS";
        String answer = "ROBOT";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.YELLOW, result[0]);
        assertEquals(Color.GREEN, result[1]);
        assertEquals(Color.YELLOW, result[2]);
        assertEquals(Color.GRAY, result[3]);
        assertEquals(Color.GRAY, result[4]);
    }

    @Test
    @DisplayName("Rule Test: Yellow priority over excess letters")
    void testYellowPriorityOverExcess() {
        String guess = "LLAMA";
        String answer = "GRILL";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.YELLOW, result[0]);
        assertEquals(Color.YELLOW, result[1]);
        assertEquals(Color.GRAY, result[2]);
        assertEquals(Color.GRAY, result[3]);
        assertEquals(Color.GRAY, result[4]);
    }

    @Test
    @DisplayName("Rule Test: Green takes precedence over yellow")
    void testGreenTakesPrecedenceOverYellow() {
        String guess = "LEVEL";
        String answer = "LLAMA";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.GREEN, result[0]);
        assertEquals(Color.GRAY, result[1]);
        assertEquals(Color.GRAY, result[2]);
        assertEquals(Color.GRAY, result[3]);
        assertEquals(Color.YELLOW, result[4]);
    }

    @Test
    @DisplayName("Rule Test: No yellows when all instances accounted for")
    void testNoYellowsWhenAllInstancesAccountedFor() {
        String guess = "EAGLE";
        String answer = "AGREE";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.YELLOW, result[0]);
        assertEquals(Color.YELLOW, result[1]);
        assertEquals(Color.YELLOW, result[2]);
        assertEquals(Color.GRAY, result[3]);
        assertEquals(Color.GREEN, result[4]);
    }

    @Test
    @DisplayName("Rule Test: Triple letter complexity")
    void testTripleLetterComplexity() {
        String guess = "EERIE";
        String answer = "BREED";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.YELLOW, result[0]);
        assertEquals(Color.YELLOW, result[1]);
        assertEquals(Color.YELLOW, result[2]);
        assertEquals(Color.GRAY, result[3]);
        assertEquals(Color.GRAY, result[4]);
    }

    @Test
    @DisplayName("Rule Test: Edge case with repeated letters at end")
    void testEdgeCaseWithRepeatedLettersAtEnd() {
        String guess = "MOMMY";
        String answer = "GLOOM";

        Color[] result = wordle.evaluate(guess, answer);

        assertEquals(Color.YELLOW, result[0]);
        assertEquals(Color.YELLOW, result[1]);
        assertEquals(Color.GRAY, result[2]);
        assertEquals(Color.GRAY, result[3]);
        assertEquals(Color.GRAY, result[4]);
    }

}