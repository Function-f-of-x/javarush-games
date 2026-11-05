package io.github.function.game2048;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextArea;

import com.javarush.engine.cell.*;

public class Game2048 extends Game {
  private static final int SIDE = 4;
  private int[][] gameField = new int[SIDE][SIDE];
  private boolean isGameStopped = false;
  private boolean isWin = false;
  private boolean isWinPending = false;
  private boolean isPause = false;
  private int score = 0;
  private int maxTile = 0;

  public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
      InvocationTargetException, InstantiationException, IllegalAccessException {
    Game.main(args);
  }

  @Override
  public void initialize() {
    setScreenSize(SIDE, SIDE);
    createGame();
    drawScene();
  }

  @Override
  public void onKeyPress(Key key) {
    if (isGameStopped) {
      if (key == Key.SPACE) {
        isGameStopped = false;
        score = 0;
        setScore(score);
        createGame();
        drawScene();
        isWin = false;
      } else {
        if (isWin) {
          showMessageDialog(Color.BLUEVIOLET, "Конец игры!:\nПробел: Новая игра \nРекорд: " + maxTile
              + " Рекорд за игру: " + getMaxTileValue() + "Очки: " + score, Color.GOLD, 20);
        } else {
          showMessageDialog(Color.BLACK, "Конец игры!:\nПробел: Новая игра \nРекорд: " + maxTile
              + " Рекорд за игру: " + getMaxTileValue() + "Очки: " + score, Color.RED, 20);
        }
        return;
      }
    }
    if (isWinPending) {
      if (key == Key.ESCAPE) {
        isWinPending = false;
        isGameStopped = true;
        showMessageDialog(Color.BLUEVIOLET, "Конец игры!:\nПробел: Новая игра \nРекорд: " + maxTile
            + " Рекорд за игру: " + getMaxTileValue() + "Очки: " + score, Color.GOLD, 20);
      } else if (key == Key.UNKNOWN || key == Key.ENTER || key == Key.SPACE) {
        showMessageDialog(Color.BLUEVIOLET, "Вы победили! Продолжить играть?\nЛюбой ход: ДА  Esc: НЕТ", Color.GOLD, 30);
        return;
      } else {
        isWinPending = false;
        isWin = true;
      }
    }
    if (isPause) {
      if (key == Key.SPACE) {
        createGame();
        drawScene();
        isPause = false;
      } else if (key == Key.ESCAPE) {
        isPause = false;
        return;
      } else {
        showMessageDialog(Color.WHITE, "Меню:\nПробел: Новая игра  Esc: Продолжить\nРекорд: " + maxTile
            + " Рекорд за игру: " + getMaxTileValue() + "Очки: " + score, Color.BLACK, 20);
        return;
      }
    }

    if (!canUserMove()) {
      showMessageDialog(Color.BLACK, "Вы проиграли\nПробел: Новая игра  Enter: Отмена", Color.RED, 30);
      isGameStopped = true;
      return;
    }

    if (key == Key.UP) {
      moveUp();
    } else if (key == Key.RIGHT) {
      moveRight();
    } else if (key == Key.DOWN) {
      moveDown();
    } else if (key == Key.LEFT) {
      moveLeft();
    } else if (key == Key.ESCAPE) {
      showMessageDialog(Color.WHITE, "Меню:\nПробел: Новая игра  Esc: Продолжить\nРекорд: " + maxTile
          + " Рекорд за игру: " + getMaxTileValue() + "Очки: " + score, Color.BLACK, 20);
      isPause = true;
    } else {
      return;
    }
    drawScene();
  }

  private void createGame() {
    for (int x = 0; x < SIDE; x++) {
      for (int y = 0; y < SIDE; y++) {
        gameField[y][x] = 0;
      }
    }
    createNewNumber();
    createNewNumber();
  }

  private boolean canUserMove() {
    for (int y = 0; y < SIDE; y++) {
      for (int x = 0; x < SIDE; x++) {
        if (gameField[y][x] == 0) {
          return true;
        } else if (y < SIDE - 1 && gameField[y][x] == gameField[y + 1][x]) {
          return true;
        } else if ((x < SIDE - 1) && gameField[y][x] == gameField[y][x + 1]) {
          return true;
          // } else if (gameField[y][x] == 1 || gameField[y][x] == 3 || gameField[y][x] ==
          // 5 || gameField[y][x] == 7) {
          // return true;
        }
      }
    }
    return false;
  }

  private void createNewNumber() {
    if (!isWin) {
      if (getMaxTileValue() >= 2048) {
        showMessageDialog(Color.BLUEVIOLET, "Вы победили! Продолжить играть?\nЛюбой ход: ДА  Esc: НЕТ", Color.GOLD, 30);
        isWinPending = true;
        return;
      }
    }

    boolean isCreated = false;
    do {
      int x = getRandomNumber(SIDE);
      int y = getRandomNumber(SIDE);
      int random = getRandomNumber(1066);
      if (gameField[y][x] == 0) {
        if (random < 900) {
          gameField[y][x] = 2;
        } else if (random >= 900 && random < 1000) {
          gameField[y][x] = 4;
        } else if (random >= 1000 && random < 1050) {
          gameField[y][x] = 1;
        } else if (random >= 1050 && random < 1060) {
          gameField[y][x] = 3;
        } else if (random >= 1060 && random < 1065) {
          gameField[y][x] = 5;
        } else if (random == 1065) {
          gameField[y][x] = 7;
        } else {
          continue;
        }
        isCreated = true;
      }
    } while (!isCreated);
  }

  private int getMaxTileValue() {
    int max = gameField[0][0];
    for (int y = 0; y < SIDE; y++) {
      for (int x = 0; x < SIDE; x++) {
        if (gameField[y][x] > max) {
          max = gameField[y][x];
        }
      }
    }
    return max;
  }

  private void setCellColoredNumber(int x, int y, int value) {
    Color color = getColorByValue(value);
    Color strColor = color == Color.BLACK ? Color.WHITE : Color.BLACK;
    String strValue = value > 0 ? (value == 1 || value == 3 || value == 5 || value == 7 ? "?" : "" + value) : "";
    int strSize = value < 10000 ? 38 : (value < 1000000 ? 25 : 17);
    setCellValueEx(x, y, color, strValue, strColor, strSize);
  }

  private Color getColorByValue(int value) {
    switch (value) {
      case 0: {
        return Color.BEIGE;
      }
      case 2: {
        return Color.WHEAT;
      }
      case 4: {
        return Color.KHAKI;
      }
      case 8: {
        return Color.GOLD;
      }
      case 16: {
        return Color.ORANGERED;
      }
      case 32: {
        return Color.HOTPINK;
      }
      case 64: {
        return Color.BLUEVIOLET;
      }
      case 128: {
        return Color.DODGERBLUE;
      }
      case 256: {
        return Color.CYAN;
      }
      case 512: {
        return Color.MEDIUMSPRINGGREEN;
      }
      case 1024: {
        return Color.LIME;
      }
      case 2048: {
        return Color.GREEN;
      }
      case 1: {
        return Color.SILVER;
      }
      case 3: {
        return Color.DEEPSKYBLUE;
      }
      case 5: {
        return Color.PURPLE;
      }
      case 7: {
        return Color.YELLOW;
      }
      default: {
        return Color.BLACK;
      }
    }
  }

  private void moveLeft() {
    boolean isNewNumberNeeded = false;
    for (int[] row : gameField) {
      boolean wasCompressed = compressRow(row);
      boolean wasMerged = mergeRow(row);
      if (wasMerged) {
        compressRow(row);
      }
      if (wasCompressed || wasMerged) {
        isNewNumberNeeded = true;
      }
    }
    if (isNewNumberNeeded) {
      createNewNumber();
    }
  }

  private void moveUp() {
    rotateClockwise();
    rotateClockwise();
    rotateClockwise();
    moveLeft();
    rotateClockwise();
  }

  private void moveRight() {
    rotateClockwise();
    rotateClockwise();
    moveLeft();
    rotateClockwise();
    rotateClockwise();
  }

  private void moveDown() {
    rotateClockwise();
    moveLeft();
    rotateClockwise();
    rotateClockwise();
    rotateClockwise();
  }

  private boolean compressRow(int[] row) {
    int insertPosition = 0;
    boolean result = false;
    for (int x = 0; x < SIDE; x++) {
      if (row[x] > 0) {
        if (x != insertPosition) {
          row[insertPosition] = row[x];
          row[x] = 0;
          result = true;
        }
        insertPosition++;
      }
    }
    return result;
  }

  private boolean mergeRow(int[] row) {
    boolean result = false;
    for (int i = 0; i < row.length - 1; i++) {
      if (row[i] != 0 && row[i] == row[i + 1]) {
        row[i] += row[i + 1];
        row[i + 1] = 0;
        result = true;
        score += row[i];
        setScore(score);
      }
    }
    return result;
  }

  private void rotateClockwise() {
    int[][] result = new int[SIDE][SIDE];
    for (int i = 0; i < SIDE; i++) {
      for (int j = 0; j < SIDE; j++) {
        result[j][SIDE - 1 - i] = gameField[i][j];
      }
    }
    gameField = result;
  }

  private void drawScene() {
    for (int y = 0; y < SIDE; y++) {
      for (int x = 0; x < SIDE; x++) {
        setCellColoredNumber(x, y, gameField[y][x]);
      }
    }
  }
}
