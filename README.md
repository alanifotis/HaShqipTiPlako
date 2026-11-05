# HaShqipTiPlako

A custom, Wordle-inspired word-guessing Android game built in Java, heavily inspired by the CS50x curriculum and designed as an exciting mobile development project.

## Description
**HaShqipTiPlako** brings the addictive logic of word puzzles to Android. Players select their preferred word length, after which a random secret word is generated and masked with placeholders (`#`). With a limited number of tries based on the word's length, players must deduce the hidden word using real-time positional hints and strict input validation.

## Features & Game Rules
- **Customizable Difficulty:** Choose how many letters your target word should contain at the start of each game.
- **Dynamic Feedback System:**
  - **Correct Position:** When you guess a letter in the right spot, the corresponding `#` is replaced by that letter.
  - **Wrong Position:** If the word contains your guessed letter but it's in the wrong place, the app displays the letter followed by a `?`.
- **Strict Input Validation:** Guesses are automatically discarded if they contain spaces, punctuation, or don't match the exact length of the target word.
- **Limited Tries:** You are given `word.length + 1` attempts to crack the code.
- **Interactive Post-Game UI:** 
  - **On Win:** A congratulation message appears alongside options to replay with current settings or reset the word length.
  - **On Loss:** An encouraging "almost there" message appears with options to retry or reset the word length.

## Tech Stack
- **Language:** Java
- **Platform:** Android SDK
