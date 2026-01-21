import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JigsawPuzzle extends JFrame
{
    private static final int ROWS = 3; // 拼图行数
    private static final int COLS = 3; // 拼图列数
    private static final int PIECE_WIDTH = 150; // 每块拼图宽度
    private static final int PIECE_HEIGHT = 150; // 每块拼图高度

    private JPanel puzzlePanel;
    private PuzzlePiece[][] pieces;
    private PuzzlePiece selectedPiece = null;
    private BufferedImage originalImage;
    private boolean gameCompleted = false;

    public JigsawPuzzle() {
        initUI();
        loadImage();
        createPuzzle();
    }

    private void initUI() {
        setTitle("拼图游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        puzzlePanel = new JPanel();
        puzzlePanel.setLayout(new GridLayout(ROWS, COLS, 2, 2));
        puzzlePanel.setPreferredSize(new Dimension(COLS * PIECE_WIDTH, ROWS * PIECE_HEIGHT));
        puzzlePanel.setBackground(Color.BLACK);
        add(puzzlePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton resetButton = new JButton("重新开始");
        resetButton.addActionListener(e -> resetGame());
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadImage() {
        // 创建一个默认的彩色图像用于演示
        originalImage = new BufferedImage(
                COLS * PIECE_WIDTH,
                ROWS * PIECE_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = originalImage.createGraphics();

        // 绘制彩色渐变背景
        GradientPaint gradient = new GradientPaint(
                0, 0, Color.RED,
                COLS * PIECE_WIDTH, ROWS * PIECE_HEIGHT, Color.BLUE
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, COLS * PIECE_WIDTH, ROWS * PIECE_HEIGHT);

        // 绘制网格线
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i <= COLS; i++) {
            g2d.drawLine(i * PIECE_WIDTH, 0, i * PIECE_WIDTH, ROWS * PIECE_HEIGHT);
        }
        for (int i = 0; i <= ROWS; i++) {
            g2d.drawLine(0, i * PIECE_HEIGHT, COLS * PIECE_WIDTH, i * PIECE_HEIGHT);
        }

        // 在每个格子中绘制数字
        g2d.setColor(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 40);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String number = String.valueOf(row * COLS + col + 1);
                int x = col * PIECE_WIDTH + (PIECE_WIDTH - fm.stringWidth(number)) / 2;
                int y = row * PIECE_HEIGHT + (PIECE_HEIGHT + fm.getAscent()) / 2 - fm.getDescent();
                g2d.drawString(number, x, y);
            }
        }

        g2d.dispose();
    }

    private void createPuzzle() {
        pieces = new PuzzlePiece[ROWS][COLS];

        // 创建拼图块
        List<PuzzlePiece> pieceList = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                BufferedImage subImage = originalImage.getSubimage(
                        col * PIECE_WIDTH,
                        row * PIECE_HEIGHT,
                        PIECE_WIDTH,
                        PIECE_HEIGHT
                );

                PuzzlePiece piece = new PuzzlePiece(subImage, row, col);
                pieceList.add(piece);
                pieces[row][col] = piece;
            }
        }

        // 移除最后一个块，用作空位
        pieceList.remove(pieceList.size() - 1);
        pieces[ROWS-1][COLS-1] = null;

        // 随机打乱
        Collections.shuffle(pieceList);

        // 重新放置打乱后的块
        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (row == ROWS-1 && col == COLS-1) {
                    // 最后一个位置保持空
                    continue;
                }
                pieces[row][col] = pieceList.get(index++);
            }
        }

        // 更新显示
        updatePuzzleDisplay();
    }

    private void updatePuzzleDisplay() {
        puzzlePanel.removeAll();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                PuzzlePiece piece = pieces[row][col];
                if (piece != null) {
                    puzzlePanel.add(piece);
                } else {
                    // 添加一个空的占位面板
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setBackground(Color.LIGHT_GRAY);
                    emptyPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    puzzlePanel.add(emptyPanel);
                }
            }
        }

        puzzlePanel.revalidate();
        puzzlePanel.repaint();

        // 检查游戏是否完成
        if (isPuzzleCompleted() && !gameCompleted) {
            gameCompleted = true;
            JOptionPane.showMessageDialog(this, "恭喜你，完成拼图！", "游戏完成", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean isPuzzleCompleted() {
        int index = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                PuzzlePiece piece = pieces[row][col];
                // 最后一个位置应该是空的
                if (row == ROWS - 1 && col == COLS - 1) {
                    if (piece != null) return false;
                } else {
                    if (piece == null) return false;
                    if (piece.getOriginalRow() != row || piece.getOriginalCol() != col) return false;
                }
                index++;
            }
        }
        return true;
    }

    private void resetGame() {
        gameCompleted = false;
        createPuzzle();
    }

    // 拼图块类
    class PuzzlePiece extends JLabel {
        private int originalRow;
        private int originalCol;

        public PuzzlePiece(BufferedImage image, int originalRow, int originalCol) {
            this.originalRow = originalRow;
            this.originalCol = originalCol;

            setIcon(new ImageIcon(image));
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            // 添加鼠标监听器以支持拖拽
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handlePieceClick();
                }
            });
        }

        private void handlePieceClick() {
            // 找到当前块的位置
            int currentRow = -1, currentCol = -1;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (pieces[row][col] == this) {
                        currentRow = row;
                        currentCol = col;
                        break;
                    }
                }
                if (currentRow != -1) break;
            }

            // 查找相邻的空位
            int emptyRow = -1, emptyCol = -1;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (pieces[row][col] == null) {
                        emptyRow = row;
                        emptyCol = col;
                        break;
                    }
                }
                if (emptyRow != -1) break;
            }

            // 检查当前块是否与空位相邻
            boolean isAdjacent = false;
            if (currentRow == emptyRow && Math.abs(currentCol - emptyCol) == 1) {
                isAdjacent = true;
            } else if (currentCol == emptyCol && Math.abs(currentRow - emptyRow) == 1) {
                isAdjacent = true;
            }

            // 如果相邻，则交换位置
            if (isAdjacent) {
                pieces[emptyRow][emptyCol] = this;
                pieces[currentRow][currentCol] = null;
                updatePuzzleDisplay();
            }
        }

        public int getOriginalRow() {
            return originalRow;
        }

        public int getOriginalCol() {
            return originalCol;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new JigsawPuzzle().setVisible(true);
        });
    }
}