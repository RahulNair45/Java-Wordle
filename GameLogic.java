import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.awt.event.KeyEvent;
import java.io.*;

public class GameLogic {

   // Name of file containing all the possible "secret words"
   private static final String SECRET_WORDS_FILENAME = "secrets.txt";

   // Name of file containing all the valid guess words
   private static final String VALID_GUESSES_FILENAME = "valids.txt";

   // Use for generating random numbers!
   private static final Random rand = new Random();

   // Dimensions of the game grid in the game window
   public static final int MAX_ROWS = 6;
   public static final int MAX_COLS = 5;

   // Character codes for the enter and backspace key press
   public static final char ENTER_KEY = KeyEvent.VK_ENTER;
   public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE;

   // The null character value (used to represent an "empty" value for a spot on
   // the game grid)
   public static final char NULL_CHAR = 0;

   // Various Color Values
   private static final Color CORRECT_COLOR = new Color(53, 209, 42); // (Green)
   private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52); // (Yellow)
   private static final Color WRONG_COLOR = Color.DARK_GRAY; // (Dark Gray [obviously])
   private static final Color DEFAULT_KEY_COLOR = new Color(160, 163, 168); // (Light Gray)

   // A preset, hard-coded secret word to be use when the resepective debug is
   // enabled
   private static final char[] DEBUG_PRESET_SECRET = { 'S', 'H', 'I', 'R', 'E' };
   //private static final char[] DEBUG_PRESET_SECRET = { 'S', 'L', 'E', 'E', 'K' };
   //private static final char[] DEBUG_PRESET_SECRET = { 'S', 'W', 'I', 'S', 'H' };

   // Array storing all valid guesses read out of the respective file
   private static String[] validGuesses;

   // The current row/col where the user left off typing
   private static int currentRow, currentCol;

   // *******************************************************************

   // This function gets called ONCE when the game is very first launched
   // before the user has the opportunity to do anything.
   //
   // Should perform any initialization that needs to happen at the start of the
   // game,
   // and return the randomly chosen "secret word" as a char array
   //
   // If either of the valid guess or secret words files cannot be read, or are
   // missing the word count in the first line, this function returns null.
   public static char[] initializeGame() {
      // char[] secretWord = GameGUI.getSecretWordArr();
      currentRow = 0;
      currentCol = 0;
      char[] secretWord = getSecretWords(SECRET_WORDS_FILENAME); // sets a char array from a word file as the secret
                                                                 // word
      if (JWordleLauncher.DEBUG_USE_PRESET_SECRET) {
         secretWord = DEBUG_PRESET_SECRET;
      }
      return secretWord;
   }

   public static char[] getSecretWords(String textFile) { // gets a word from a word file and returns it as a char array
      try {
         Scanner secretWordReader = new Scanner(new File(textFile));
         int wordCount = secretWordReader.nextInt();
         String secretWord = "";
         int randomNumber = (int) (Math.random() * (wordCount-1));
         for (int i = 0; i < randomNumber; i++) {
            secretWord = secretWordReader.next();
         }
         char[] secretWordArray = new char[secretWord.length()];
         for (int i = 0; i < secretWordArray.length; i++) {
            secretWordArray[i] = secretWord.charAt(i);
         }
         return secretWordArray;
      } catch (FileNotFoundException e) {
         return null;
      }
   }

   public static boolean readValidGuesses(String textFile, char[] guessArray) { // checks if the inputed word is in a
                                                                                // wordfile
      try {
         Scanner validGuessReader = new Scanner(new File(textFile));
         boolean checker = false;
         String guess = "";
         for (int i = 0; i < guessArray.length; i++) {
            guess += guessArray[i];
         }
         while (validGuessReader.hasNext()) {
            String word = validGuessReader.next();
            if (guess.equals(word)) {
               checker = true;
               return checker;
            }
         }
         return checker;
      } catch (FileNotFoundException e) {
         return false;
      }
   }

   // Complete your warmup task (Section 3.1.1 part 2) here by calling the
   // requisite
   // functions out of GameGUI.
   // This function gets called ONCE after the graphics window has been
   // initialized and initializeGame has been called.

   public static void warmup() {
      /*
       * GameGUI.setGridChar(0, 0, 'C');
       * GameGUI.setGridColor(0, 0, CORRECT_COLOR);
       * GameGUI.setGridChar(1, 3, 'O');
       * GameGUI.setGridColor(1, 3, WRONG_COLOR);
       * GameGUI.setGridChar(3, 4, 'S');
       * GameGUI.setGridChar(5, 4, 'C');
       * GameGUI.setGridColor(5, 4, WRONG_PLACE_COLOR);
       * GameGUI.setKeyColor('U', CORRECT_COLOR);
       * GameGUI.setKeyColor('C', WRONG_COLOR);
       */

      // All of your warmup code will go in here except for the
      // "wiggle" task (3.1.1 part 3)... where will that go?

   }

   // This function gets called everytime the user types a valid key on the
   // keyboard (alphabetic character, enter, or backspace) or clicks one of the
   // keys on the graphical keyboard interface.
   //
   // The key pressed is passed in as a char value.
   public static void reactToKey(char key) {
      /*
       * if (key == 'W'){
       * GameGUI.wiggle(3);
       * }
       * 
       * System.out.println("reactToKey(...) called! key (int value) = '" + ((int)key)
       * + "'");
       */
      char[] wordYouGuessing = GameGUI.getSecretWordArr(); // deletes letters
      if (key == BACKSPACE_KEY) {
         if (currentCol > 0) {
            currentCol--;
            GameGUI.setGridChar(currentRow, currentCol, NULL_CHAR);

         }
      } else if (key == ENTER_KEY) { // goes to the next next line
         if (currentCol == MAX_COLS) {
            char[] guessWord = storeGuess();
            if (readValidGuesses(VALID_GUESSES_FILENAME, guessWord)) { // checks if the inputed guess is a valid word
                                                                       // from a word file
               char[] oldWord = colorInGridAndKeys(wordYouGuessing, guessWord); // compares the inputed line to the line
                                                                                // you are guessing and colores in the
                                                                                // grid and keys
               if (Arrays.equals(oldWord, wordYouGuessing)) { // ends game if you get the right word
                  // System.out.println("hi");
                  GameGUI.gameOver(true);
               }
               currentCol = 0;
               currentRow++;
               if (currentRow > 5) { // ends game if you run out of guesses
                  if (Arrays.equals(oldWord, wordYouGuessing)) {
                     GameGUI.gameOver(true);
                  } else {
                     GameGUI.gameOver(false);
                  }
               }
            } else {
               GameGUI.wiggle(currentRow);
            }
         } else { // if conditions are not met in order to go to next Row, causes row to wiggle
            GameGUI.wiggle(currentRow);
         }

      } else {
         if (currentCol < MAX_COLS) { // inputs a letter into the row
            GameGUI.setGridChar(currentRow, currentCol, key);
            currentCol++;
         }
      }
   }

   // this code checks if it is right
   public static char[] colorInGridAndKeys(char[] secretWord, char[] wordYouGuessed) {
      // char[] colorGuide = new char[5];
      char[] secretWordCopy = new char[5];
      for (int letterPosition = 0; letterPosition < secretWordCopy.length; letterPosition++){ //creates a copy of the word the player is guessing to deal with
         secretWordCopy[letterPosition] = secretWord[letterPosition]; // keeping track of what letters have been identified
      }
      for (int colGuess = 0; colGuess < secretWord.length; colGuess++) { // turns grid spot and key on keyboard green if                                                
         if (secretWordCopy[colGuess] == wordYouGuessed[colGuess]) {
            GameGUI.setGridColor(currentRow, colGuess, CORRECT_COLOR);
            char charForGreen = GameGUI.getGridChar(currentRow, colGuess);
            GameGUI.setKeyColor(charForGreen, CORRECT_COLOR);
            secretWordCopy[colGuess] = NULL_CHAR; // removes the letter from the copy of the secret word when it was identified as being the right letter in the righ spot
         } 
         else {
            for (int colSecret = 0; colSecret < secretWord.length; colSecret++) { // turns grid spot and key on keyboard
                                                                                  // yellow if letter is the right
                                                                                  // letter in the wrong spot
               if (secretWordCopy[colSecret] == wordYouGuessed[colGuess]) {
                  GameGUI.setGridColor(currentRow, colGuess, WRONG_PLACE_COLOR);
                  char charForYellow = GameGUI.getGridChar(currentRow, colGuess);
                  if (GameGUI.getKeyColor(charForYellow) != CORRECT_COLOR) {
                     GameGUI.setKeyColor(charForYellow, WRONG_PLACE_COLOR);
                  }
                  secretWordCopy[colSecret] = NULL_CHAR; // removes the letter from the copy when it is identified as the right letter in the wrong spot 
                  break;
               } 
               else {
                  GameGUI.setGridColor(currentRow, colGuess, WRONG_COLOR); // turns grid spot and key on keyboard red if
                                                                           // letter is wrong
                  char charForGray = GameGUI.getGridChar(currentRow, colGuess);
                  if (GameGUI.getKeyColor(charForGray) != CORRECT_COLOR
                        && GameGUI.getKeyColor(charForGray) != WRONG_PLACE_COLOR) {
                     GameGUI.setKeyColor(charForGray, WRONG_COLOR);
                  }

               }
            }
         }
      }
      //removeDuplicates(wordYouGuessed, secretWord);
      return wordYouGuessed;
   }

   // public static void removeDuplicates(char[] wordguessed, char[] wordYouWant){
   //    int[] answerLetterCounter = new int[5];
   //    for (int letterPosition = 0; letterPosition < wordYouWant.length; letterPosition++){
   //       for (int wordIteration = 0; wordIteration < wordYouWant.length; wordIteration++){
   //          if (wordYouWant[letterPosition] == wordYouWant[wordIteration]){
   //             answerLetterCounter[letterPosition]++;
   //          }
   //       }
   //    }
   //    //System.out.println(answerLetterCounter.toString());
   //    // System.out.println(answerLetterCounter[0]);
   //    // System.out.println(answerLetterCounter[1]);
   //    // System.out.println(answerLetterCounter[2]);
   //    // System.out.println(answerLetterCounter[3]);
   //    // System.out.println(answerLetterCounter[4]);

      
   //    int[] guessLetterCounter = new int[5];
   //    for (int guessletterTracker = 0; guessletterTracker < guessLetterCounter.length; guessletterTracker++){
   //       for (int answerletterTracker = 0; answerletterTracker < guessLetterCounter.length; answerletterTracker++){
   //          if(wordguessed[guessletterTracker] == wordYouWant[answerletterTracker]){
   //             guessLetterCounter[answerletterTracker]++;
   //             }
   //          }
   //       }
   //       System.out.println(guessLetterCounter[0]);
   //       System.out.println(guessLetterCounter[1]);
   //       System.out.println(guessLetterCounter[2]);
   //       System.out.println(guessLetterCounter[3]);
   //       System.out.println(guessLetterCounter[4]);
   //       System.out.println();
   // .
   //    for (int guessTracker = 0; guessTracker < wordYouWant.length; guessTracker++){
   //       for (int answerTracker = 0; answerTracker < wordYouWant.length; answerTracker++){

   //       }
   //    }
   // }


   public static char[] storeGuess() { // turns the inputed row of letters into an array
      char[] inputWord = new char[5];
      for (int col = 0; col < MAX_COLS; col++) {
         inputWord[col] = GameGUI.getGridChar(currentRow, col);
      }
      return inputWord;
   }
}
