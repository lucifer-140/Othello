package com.example.othello;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;


public class BoardView extends View {
    private Paint paint = new Paint();
    private int boardSize = 8;
    private int boardLength;
    private int startX, startY;
    private int cellSize;
    private ImageView gameOverImage;


    private int[][] board;
    private boolean isBlackTurn = true;
    private TextView turnIndicator;

    private boolean isVsCPU;
    private int playerColor;
    private boolean isCpuThinking = false;


    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        board = new int[boardSize][boardSize];
        initializeBoard();
    }

    public void setGameMode(boolean isVsCPU, int playerColor) {
        this.isVsCPU = isVsCPU;
        this.playerColor = playerColor;
    }

    public void setTurnIndicator(TextView turnIndicator) {
        this.turnIndicator = turnIndicator;
        updateTurnIndicator();
    }

    public void setGameOverImage(ImageView imageView) {
        this.gameOverImage = imageView;
    }


    public void resetGame() {
        isBlackTurn = true;
        isCpuThinking = false;
        board = new int[boardSize][boardSize];

        board[boardSize / 2 - 1][boardSize / 2 - 1] = 2;
        board[boardSize / 2][boardSize / 2] = 2;
        board[boardSize / 2 - 1][boardSize / 2] = 1;
        board[boardSize / 2][boardSize / 2 - 1] = 1;

        gameOverImage.setVisibility(View.GONE);

        invalidate();
        updateTurnIndicator();

        if (isVsCPU && playerColor == 2) {
            postDelayed(() -> triggerCpuMove(), 250);
        }
    }


    private void initializeBoard() {
        int mid = boardSize / 2;
        board[mid - 1][mid - 1] = 2;
        board[mid][mid] = 2;
        board[mid - 1][mid] = 1;
        board[mid][mid - 1] = 1;
    }

    private void updateTurnIndicator() {
        if (turnIndicator != null) {
            turnIndicator.setText("Turn: " + (isBlackTurn ? "Black" : "White"));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        boardLength = Math.min(screenWidth, screenHeight) * 9 / 10;
        cellSize = boardLength / boardSize;
        startX = (screenWidth - boardLength) / 2;
        startY = (screenHeight - boardLength) / 2;

        paint.setColor(Color.parseColor("#228B22"));
        canvas.drawRect(startX, startY, startX + boardLength, startY + boardLength, paint);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        for (int i = 0; i <= boardSize; i++) {
            float x = startX + i * cellSize;
            canvas.drawLine(x, startY, x, startY + boardLength, paint);
        }
        for (int i = 0; i <= boardSize; i++) {
            float y = startY + i * cellSize;
            canvas.drawLine(startX, y, startX + boardLength, y, paint);
        }

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                float cx = startX + col * cellSize + cellSize / 2;
                float cy = startY + row * cellSize + cellSize / 2;
                float radius = cellSize * 0.4f;

                if (board[row][col] == 1 || board[row][col] == 2) {
                    paint.setColor(board[row][col] == 1 ? Color.BLACK : Color.WHITE);
                    canvas.drawCircle(cx, cy, radius, paint);
                }
            }
        }
        paint.setColor(Color.GRAY);
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == 0 && isValidMove(row, col, isBlackTurn ? 1 : 2)) {
                    float cx = startX + col * cellSize + cellSize / 2f;
                    float cy = startY + row * cellSize + cellSize / 2f;
                    float radius = cellSize / 6f;
                    canvas.drawCircle(cx, cy, radius, paint);
                }
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCpuThinking) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            if (x >= startX && x <= startX + boardLength &&
                    y >= startY && y <= startY + boardLength) {

                int col = (int) ((x - startX) / cellSize);
                int row = (int) ((y - startY) / cellSize);

                if (board[row][col] == 0 && isValidMove(row, col, isBlackTurn ? 1 : 2)) {
                    board[row][col] = isBlackTurn ? 1 : 2;
                    flipPieces(row, col, isBlackTurn ? 1 : 2);
                    nextTurn();
                    invalidate();

                    if (isVsCPU && isBlackTurn != (playerColor == 1)) {
                        cpuMove();
                    }
                }
            }
        }
        return true;
    }

    private void nextTurn() {
        isBlackTurn = !isBlackTurn;
        updateTurnIndicator();

        if (!hasValidMove(isBlackTurn ? 1 : 2)) {
            isBlackTurn = !isBlackTurn;
            updateTurnIndicator();

            if (!hasValidMove(isBlackTurn ? 1 : 2)) {
                determineWinner();
            }
        }
    }

    public void triggerCpuMove() {
        if (isVsCPU && isBlackTurn) {
            cpuMove();
        }
    }


    private void cpuMove() {
        isCpuThinking = true;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<int[]> validMoves = new ArrayList<>();
            int maxFlips = 0;
            int bestRow = -1, bestCol = -1;

            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (board[row][col] == 0) {
                        int flips = countFlippedPieces(row, col, isBlackTurn ? 1 : 2);
                        if (flips > maxFlips) {
                            maxFlips = flips;
                            bestRow = row;
                            bestCol = col;
                        }
                    }
                }
            }

            if (bestRow != -1 && bestCol != -1) {
                board[bestRow][bestCol] = isBlackTurn ? 1 : 2;
                flipPieces(bestRow, bestCol, isBlackTurn ? 1 : 2);
                nextTurn();
                invalidate();
            }
            isCpuThinking = false;
        }, 1500);
    }

    private void flipPieces(int row, int col, int player) {
        int opponent = (player == 1) ? 2 : 1;
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},         {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int r = row + dir[0], c = col + dir[1];
            List<int[]> toFlip = new ArrayList<>();

            while (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == opponent) {
                toFlip.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
            }

            if (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == player) {
                for (int[] pos : toFlip) {
                    board[pos[0]][pos[1]] = player;
                }
            }
        }
    }

    private boolean hasValidMove(int player) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == 0 && isValidMove(row, col, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void determineWinner() {
        int blackCount = 0, whiteCount = 0;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == 1) {
                    blackCount++;
                } else if (board[row][col] == 2) {
                    whiteCount++;
                }
            }
        }

        String result;
        if (blackCount > whiteCount) {
            result = "Black wins!";
        } else if (whiteCount > blackCount) {
            result = "White wins!";
            if (gameOverImage != null) {
                gameOverImage.setVisibility(View.VISIBLE);
            }
        } else {
            result = "It's a tie!";
        }

        turnIndicator.setText(result);
    }



    private boolean isValidMove(int row, int col, int player) {
        int opponent = (player == 1) ? 2 : 1;
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},         {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int r = row + dir[0], c = col + dir[1];
            boolean hasOpponent = false;

            while (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == opponent) {
                hasOpponent = true;
                r += dir[0];
                c += dir[1];
            }

            if (hasOpponent && r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == player) {
                return true;
            }
        }
        return false;
    }

    private int countFlippedPieces(int row, int col, int player) {
        int count = 0;
        int opponent = (player == 1) ? 2 : 1;
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},         {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int r = row + dir[0], c = col + dir[1];
            int flips = 0;

            while (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == opponent) {
                flips++;
                r += dir[0];
                c += dir[1];
            }

            if (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == player) {
                count += flips;
            }
        }
        return count;
    }
}
