import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Objects;

public class FontChooser extends JDialog {
    private Font selectedFont;
    private boolean okPressed = false;
    private JList<String> fontList;
    private JComboBox<String> styleBox;

    public FontChooser(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setupUI();
        setSize(450, 350);
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        Color bgColor = new Color(245, 245, 245);
        Color accentColor = new Color(30, 144, 255);
        Color listBgColor = new Color(255, 255, 230);

        // 设置窗口背景
        getContentPane().setBackground(bgColor);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel fontPanel = createFontListPanel(bgColor, listBgColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(fontPanel, gbc);

        /*样式面板*/
        JPanel controlPanel = createControlPanel(bgColor);
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(controlPanel, gbc);

        JPanel buttonPanel = createButtonPanel(bgColor, accentColor);
        gbc.gridy = 2;
        add(buttonPanel, gbc);

        setupKeyListeners();
    }

    private JPanel createFontListPanel(Color bgColor, Color listBgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createTitledBorder("字体(Fonts)"));

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = Arrays.stream(ge.getAvailableFontFamilyNames())
                .sorted()
                .toArray(String[]::new);

        fontList = new JList<>(fontNames);
        fontList.setSelectedValue("Arial", true);
        fontList.setFixedCellHeight(25);
        fontList.setBackground(listBgColor);
        fontList.setOpaque(true);
        fontList.setCellRenderer(new FontListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(fontList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createControlPanel(Color bgColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createTitledBorder("样式(Style)"));

        styleBox = new JComboBox<>(new String[]{"常规(Plain)", "粗体(Bold)", "斜体(Italic)"});
        styleBox.setSelectedItem("常规(Plain)");
        styleBox.setFont(new Font("黑体", Font.PLAIN, 14));
        panel.add(new JLabel("样式(Style):"));
        panel.add(styleBox);

        return panel;
    }

    // 创建按钮面板
    private JPanel createButtonPanel(Color bgColor, Color accentColor) {
        JButton okButton = new JButton("确定(OK)");
        JButton cancelButton = new JButton("取消(Cancel)");

        /*设置按钮样式*/
        okButton.setBackground(accentColor);
        okButton.setForeground(Color.WHITE);
        okButton.setOpaque(true);
        okButton.setFont(new Font("黑体", Font.BOLD, 14));
        okButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setOpaque(true);
        cancelButton.setFont(new Font("黑体", Font.BOLD, 14));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 设置默认按钮
        getRootPane().setDefaultButton(okButton);

        /*按钮动作*/
        okButton.addActionListener(e -> {
            String fontName = fontList.getSelectedValue();
            int style = switch (Objects.requireNonNull(styleBox.getSelectedItem()).toString()) {
                case "粗体(Bold)" -> Font.BOLD;
                case "斜体(Italic)" -> Font.ITALIC;
                default -> Font.PLAIN;
            };

            selectedFont = new Font(fontName, style, 20);
            okPressed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(bgColor);
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    /*设置键盘快捷键*/
    private void setupKeyListeners() {
        // ESC 取消
        getRootPane().registerKeyboardAction(_ -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /*字体列表单元格渲染器（显示实际字体效果）*/
    private static class FontListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof String fontName) {
                label.setFont(new Font(fontName, Font.PLAIN, 14));
            }
            return label;
        }
    }

    /*Getter*/
    public boolean isOkPressed() {
        return okPressed;
    }

    public Font getSelectedFont() {
        return selectedFont;
    }
}