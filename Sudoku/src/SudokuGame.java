import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class SudokuGame {
    JFrame frame = new JFrame("Sudoku");

    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    JLabel textLabel = new JLabel();
    JLabel timerLabel = new JLabel("Time: 0s");

    JButton numSelected = null;
    Tile[][] tiles = new Tile[9][9];

    Timer gameTimer = new Timer();
    int secondsElapsed = 0;
    int errors = 0;

    String[] selectedPuzzle;
    String[] selectedSolution;

    // Puzzle banks
    String[][] easyPuzzles = {
        {
            "--74916-5", "2---6-3-9", "-----7-1-",
            "-586----4", "--3----9-", "--62--187",
            "9-4-7---2", "67-83----", "81--45---"
        }
    };

    String[][] mediumPuzzles = {
        {
            "8--9---5-", "-1--6----", "---87-2--",
            "3--8----9", "--4--1---", "6---3-1--",
            "-8--4----", "5----9--3", "--1--2--8"
        }
    };

    String[][] hardPuzzles = {
        {
            "----9----", "-2-------", "---8--1--",
            "-1---6-9-", "--7-----3", "4---2----",
            "---6-----", "9--4-----", "--3--7--5"
        }
    };

    public SudokuGame() {
        selectDifficulty();

        setupFrame();
        setupTextLabel();
        setupTiles();
        setupButtons();
        setupBottomPanel();
        startTimer();

        frame.setVisible(true);
    }

    void selectDifficulty() {
        String[] options = {"Easy", "Medium", "Hard"};
        String choice = (String) JOptionPane.showInputDialog(
            frame,
            "Choose difficulty:",
            "Sudoku",
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            "Easy"
        );

        if (choice == null) System.exit(0); // User closed the dialog

        switch (choice) {
            case "Easy":
                selectedPuzzle = easyPuzzles[0];
                selectedSolution = new String[] {
                    "387491625", "241568379", "569327418",
                    "758619234", "123784596", "496253187",
                    "934176852", "675832941", "812945763"
                };
                break;
            case "Medium":
                selectedPuzzle = mediumPuzzles[0];
                selectedSolution = new String[] {
                    "836941752", "219563874", "547872693",
                    "375816429", "984721365", "621439187",
                    "198345726", "752698143", "463217958"
                };
                break;
            case "Hard":
                selectedPuzzle = hardPuzzles[0];
                selectedSolution = new String[] {
                    "416795328", "823146597", "579832164",
                    "218356794", "967481253", "345927681",
                    "781263419", "692514837", "153678942"
                };
                break;
        }
    }

    void setupFrame() {
        frame.setSize(600, 700);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
    }

    void setupTextLabel() {
        textLabel.setFont(new Font("Arial", Font.BOLD, 24));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Sudoku: 0 Errors");
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);
    }

    void setupTiles() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(9, 9));

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Tile tile = new Tile(r, c);
                char ch = selectedPuzzle[r].charAt(c);

                if (ch != '-') {
                    tile.setText(String.valueOf(ch));
                    tile.setFont(new Font("Arial", Font.BOLD, 20));
                    tile.setBackground(Color.LIGHT_GRAY);
                    tile.isFixed = true;
                } else {
                    tile.setFont(new Font("Arial", Font.PLAIN, 20));
                    tile.setBackground(Color.white);
                    tile.isFixed = false;
                }

                tile.setFocusable(false);
                setTileBorder(tile, r, c);
                boardPanel.add(tile);
                tiles[r][c] = tile;

                tile.addActionListener(e -> handleTileClick(tile));
            }
        }

        frame.add(boardPanel, BorderLayout.CENTER);
    }

    void setupButtons() {
        buttonsPanel.removeAll();
        buttonsPanel.setLayout(new GridLayout(1, 9));

        for (int i = 1; i < 10; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setFocusable(false);
            button.setBackground(Color.white);

            button.addActionListener(e -> {
                if (numSelected != null)
                    numSelected.setBackground(Color.white);
                numSelected = button;
                numSelected.setBackground(Color.lightGray);
            });

            buttonsPanel.add(button);
        }

        frame.add(buttonsPanel, BorderLayout.SOUTH);
    }

    void setupBottomPanel() {
        bottomPanel.setLayout(new BorderLayout());

        JButton resetButton = new JButton("Reset");
        resetButton.setFocusable(false);
        resetButton.addActionListener(e -> resetGame());
        bottomPanel.add(resetButton, BorderLayout.WEST);

        timerLabel.setHorizontalAlignment(JLabel.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bottomPanel.add(timerLabel, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.AFTER_LAST_LINE);
    }

    void startTimer() {
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                secondsElapsed++;
                timerLabel.setText("Time: " + secondsElapsed + "s");
            }
        }, 1000, 1000);
    }

    void handleTileClick(Tile tile) {
        if (numSelected == null || tile.isFixed || !tile.getText().isEmpty())
            return;

        int r = tile.r;
        int c = tile.c;
        String selectedNum = numSelected.getText();
        String correct = String.valueOf(selectedSolution[r].charAt(c));

        if (selectedNum.equals(correct)) {
            tile.setText(selectedNum);
            tile.setForeground(Color.BLUE);
            checkWinCondition();
        } else {
            errors++;
            textLabel.setText("Sudoku: " + errors + " Errors");
            tile.setBackground(Color.PINK);

            Timer tempTimer = new Timer();
            tempTimer.schedule(new TimerTask() {
                public void run() {
                    tile.setBackground(Color.white);
                }
            }, 500);
        }
    }

    void checkWinCondition() {
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (!tiles[r][c].getText().equals(String.valueOf(selectedSolution[r].charAt(c))))
                    return;

        gameTimer.cancel();
        JOptionPane.showMessageDialog(frame,
            "🎉 You solved it in " + secondsElapsed + " seconds with " + errors + " errors!");
    }

    void resetGame() {
        secondsElapsed = 0;
        errors = 0;
        textLabel.setText("Sudoku: 0 Errors");
        timerLabel.setText("Time: 0s");
        numSelected = null;
        gameTimer.cancel();
        gameTimer = new Timer();
        startTimer();
        setupTiles();
    }

    void setTileBorder(Tile tile, int r, int c) {
        int top = 1, left = 1, bottom = 1, right = 1;
        if (r == 2 || r == 5) bottom = 5;
        if (c == 2 || c == 5) right = 5;
        tile.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
    }
}
