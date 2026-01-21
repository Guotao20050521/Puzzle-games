import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class HintShower extends JFrame{
    private final String language;
    public HintShower(Object[] array, String lang){
        Color bgColor = new Color(245, 245, 245);
        Color listBgColor = new Color(255, 255, 230);
        Color accentColor = new Color(30, 144, 255); // 道奇蓝

        /*使用更现代的字体*/
        Font titleFont = new Font("黑体", Font.BOLD, 18);
        Font listFont = new Font("Consolas", Font.PLAIN, 14);
        Font warningFont = new Font("黑体", Font.ITALIC, 12);

        setTitle("游戏提示");
        getContentPane().setBackground(bgColor);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        /*标题区域*/
        JLabel title = new JLabel("提示（Hint）", SwingConstants.CENTER);
        title.setFont(titleFont);
        title.setForeground(accentColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(title, gbc);

        DefaultListModel<String> model = new DefaultListModel<>();
        for (Object o: array){
            if (o instanceof String){
                model.addElement((String) o);
            }
        }
        JList<String> stringJList = new JList<>(model);
        stringJList.setFont(listFont);
        stringJList.setBackground(listBgColor);
        stringJList.setOpaque(true);
        stringJList.setSelectionBackground(accentColor);
        stringJList.setFixedCellHeight(25);

        JScrollPane scrollPane = new JScrollPane(stringJList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        language = lang;
        JLabel msg;
        if (Objects.equals(lang, "中文")){
            msg = new JLabel("该窗口关闭后不可再打开，请谨慎关闭！");
        }else{
            msg = new JLabel("Warning: This hint window cannot be opened after quit!");
        }
        msg.setFont(warningFont);
        msg.setForeground(Color.RED);
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(msg, gbc);
        this.setSize(450, 500);
        this.setVisible(true);
    }
}
