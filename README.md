<!-- Improved compatibility of back to top link: See: https://github.com/YanivRabin/Scrabble-Project#readme -->
<a name="readme-top"></a>


<div align="center">
  <a href="https://github.com/github_username/repo_name">
    <img src="/Scrabble_icon.png" alt="Logo" width="100" height="100">
  </a>


<h3 align="center">Book Scrabble</h3>
  <p align="center">
    <a href="https://github.com/github_username/repo_name">View Demo</a>
    ·
    <a href="https://view.monday.com/1187919453-d087be1e389a3575ae0b3f30a8bc5adb?r=euc1">View Gantt</a>
·
    <a href="https://YanivRabin.github.io/Book-Scrabble/JavaDoc_Scrabble/index.html">Documentations</a>
  </p>
</div>


## Installation
To play the game, you need to have Java JDK 1.8 or above installed on your computer.
<br/>
You can download the version of Java from the official website: https://www.oracle.com/java/technologies/javase-downloads.html
or from your local IDE.

After installing Java, you can clone this repository using the following command in your terminal:

   ```sh
   git clone https://github.com/YanivRabin/Book-Scrabble.git
   ```
<p align="right"><a href="#readme-top">back to top</a></p>


## How to Play
#### Setup:
* Each player randomly draws a tile from the bag.
* The order of players is determined by the order of the letters drawn (from smallest to largest).
    * If an empty tile is drawn, it is returned to the bag and another tile is drawn.
* All tiles are returned to the bag.
* Each player randomly draws 7 tiles.

#### Gameplay:
* The first player (who drew the smallest letter in the draw) must form a legal word that passes through the central square (the star) on the board.
    * Only this player receives double points for the word.
    * The player then replenishes their tiles to 7 by drawing from the bag.
* Each player in turn forms a legal word from their tiles :
    * Like in a crossword, each word must be anchored to one of the tiles on the board.
    * After forming the word, the player replenishes their tiles to 7 by drawing from the bag.
    * The player's score accumulates according to the words formed on the board using their tiles.
* A player who cannot form a legal word forfeits their turn.
* The game ends after N rounds.

#### Scoring Calculation:
* Tiles placed on premium squares (double or triple letter/word) will multiply or triple their corresponding value accordingly.
* Then, the word receives the sum of its tiles' values.
* This sum will be multiplied or tripled for each square where a tile was placed for a double or triple letter/word, respectively. (That is, there may be a multiplication by 4 or 9 if a word covers two double-letter or triple-word squares, respectively.)
* The above calculation applies to any new word created on the board as a result of the placement.

#### Word Requirements:
* Written from left to right or from top to bottom (and not in any other direction)
* A word that appears in one of the selected books for the game
* Leans on one of the existing tiles on the board
* Does not create other words on the board that are invalid.
<p align="right"><a href="#readme-top">back to top</a></p>


## Features
* Support for 2-4 players
* Random distribution of tiles at the beginning of the game
* Ability to exchange tiles and skip turns
* Scoring system based on the values of each letter tile
* Validation of word placement according to the rules of Scrabble
<p align="right"><a href="#readme-top">back to top</a></p>


## Contributors
* [Yaniv Rabin](https://github.com/YanivRabin)
* [Yehonatan Malki](https://github.com/JoniMalki)
* [Roy Toledo](https://github.com/Roytol)
<p align="right"><a href="#readme-top">back to top</a></p>
