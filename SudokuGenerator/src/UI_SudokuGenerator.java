import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.io.*;
import java.util.Date;

class UI_SudokuGenerator extends JFrame
{
    private static final Color PRIMARY_COLOR = new Color(240, 248, 255); // AliceBlue
    private static final Color SECONDARY_COLOR = new Color(70, 130, 180); // SteelBlue
    private static final Color TEXT_COLOR = new Color(30, 30, 30); // 深灰色文字

    public UI_SudokuGenerator() {
        super("数独游戏 - 数独大师");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);

        /*设置背景渐变面板*/
        JPanel backgroundPanel = getJPanel();

        /*标题标签*/
        JLabel msg = new JLabel("请选择数独类型：", SwingConstants.CENTER);
        msg.setFont(new Font("微软雅黑", Font.BOLD, 20));
        msg.setForeground(TEXT_COLOR);
        msg.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        backgroundPanel.add(msg, BorderLayout.NORTH);

        JPanel radioPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        radioPanel.setOpaque(false);
        JRadioButton[] jRadioButtons = new JRadioButton[3];
        jRadioButtons[0] = createStyledRadioButton("四宫数独");
        jRadioButtons[1] = createStyledRadioButton("六宫数独");
        jRadioButtons[2] = createStyledRadioButton("九宫数独");

        ButtonGroup b = new ButtonGroup();
        for (JRadioButton j : jRadioButtons) {
            b.add(j);
            radioPanel.add(j);
        }
        backgroundPanel.add(radioPanel, BorderLayout.CENTER);

        /*添加“开始游戏”按钮*/
        JButton startButton = new JButton("开始游戏");
        styleButton(startButton);
        startButton.setForeground(Color.black);
        startButton.addActionListener(e -> {
            if (b.getSelection() != null){
                String selected = b.getSelection().getActionCommand();
                if (selected != null) {
                    switch(selected){
                        case "四":
                            generateAndStartGame(4);
                            break;
                        case "六":
                            generateAndStartGame(6);
                            break;
                        case "九":
                            generateAndStartGame(9);
                            break;
                        default:
                            throw new IllegalArgumentException("错误的数独宫数！");
                    }
                }
            }else {
                JOptionPane.showMessageDialog(this, "请先选择一个数独类型", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(backgroundPanel);
        setVisible(true);
    }
    private static JPanel getJPanel() {
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(220, 235, 245),
                        0, h, new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return backgroundPanel;
    }
    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setActionCommand(text.substring(0, 1));
        radioButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        radioButton.setForeground(TEXT_COLOR);
        radioButton.setOpaque(false);
        radioButton.setFocusPainted(false);
        radioButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        //radioButton.addActionListener(this);
        return radioButton;
    }
    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR.darker(), 2));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
    }
    private void generateAndStartGame(int size) {
        SudokuGenerator generator = new SudokuGenerator(size);
        int[][] sudoku = generator.generateSudoku();
        int[][] ans = generator.getSolution();
        WriteSudoku(sudoku);
        SwingUtilities.invokeLater(() -> new UI_Play(sudoku, ans));
    }
    private static void WriteSudoku(int[][] s) {
        try {
            FileWriter fileWriter = new FileWriter("Sudoku_" + new Date().toString().replace(' ', '_') + ".txt");
            fileWriter.write(Arrays.deepToString(s));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}