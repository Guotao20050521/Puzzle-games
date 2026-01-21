import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class CrossWords extends JFrame{
    private final int WORDNUM = 6;
    private String language;
    private final JButton btn;
    private final JButton switchLang;
    private final JButton quit;
    private final JLabel msg;
    public CrossWords(){
        super();
        btn = new JButton("选择词表文件🧾");
        switchLang = new JButton("English mode");
        msg = new JLabel("词表文件是一个txt格式的文本文档。");
        quit = new JButton("退出");

        language = "中文";

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 设置组件间距
        /*添加组件*/
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(btn, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(quit, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(msg, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(switchLang, gbc);

        getContentPane().setBackground(new Color(240, 240, 240));
        btn.setBackground(new Color(0, 120, 215));
        btn.setOpaque(true);
        btn.setForeground(Color.WHITE);
        quit.setBackground(new Color(220, 53, 69));
        quit.setOpaque(true);
        quit.setForeground(Color.WHITE);
        switchLang.setBackground(new Color(40, 167, 69));
        switchLang.setOpaque(true);
        switchLang.setForeground(Color.WHITE);

        Font font = new Font("SansSerif", Font.BOLD, 14);
        btn.setFont(font);
        quit.setFont(font);
        switchLang.setFont(font);
        msg.setFont(font);

        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        quit.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        switchLang.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt file", "txt");  //构建文件扩展名过滤器
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               JFileChooser fileChooser = new JFileChooser();
               fileChooser.addChoosableFileFilter(filter);
               fileChooser.setMultiSelectionEnabled(false);
               if(fileChooser.showOpenDialog(CrossWords.this) == JFileChooser.APPROVE_OPTION) {
                   Properties p = new Properties();
                   try {
                       p = CrossWords.FileAnalysis(fileChooser.getSelectedFile().getAbsolutePath());
                   } catch (Exception ex) {
                       String msg = "";
                       if (ex instanceof IOException) msg = Objects.equals(language, "中文") ? "在读取文件时出现问题。": "Oops, we met a problem when reading the file!";
                       else if (ex instanceof IndexOutOfBoundsException) msg = Objects.equals(language, "中文") ? "您似乎未按格式编写词表文件。": "It seems that you don't edit the word list with format.";
                       else msg = ex.toString();

                       if (Objects.equals(language, "中文")) JOptionPane.showMessageDialog(CrossWords.this, msg, "出错了！", JOptionPane.ERROR_MESSAGE);
                       else JOptionPane.showMessageDialog(CrossWords.this, msg, "Error!", JOptionPane.ERROR_MESSAGE);
                       return;
                   }

                   if (p.size() < WORDNUM){
                       if (Objects.equals(language, "中文")) JOptionPane.showMessageDialog(CrossWords.this, "您词表中的单词似乎少于" + WORDNUM + "个，无法开始游戏！");
                       else JOptionPane.showMessageDialog(CrossWords.this, "The words in the list is less than" + WORDNUM + ". Please add more words!");
                   } else if (p.size() == WORDNUM) {
                       try{
                           Properties finalP = p;
                           SwingUtilities.invokeLater(() ->{GameGenerator.Generate(finalP);});
                       } catch (NullPointerException ex) {
                           if (Objects.equals(language, "中文")) JOptionPane.showMessageDialog(CrossWords.this, "生成失败！请更换词表重试！", "出错了！", JOptionPane.ERROR_MESSAGE);
                           else JOptionPane.showMessageDialog(CrossWords.this, "Generate game sheet failed! Please use another word list!", "Error!", JOptionPane.ERROR_MESSAGE);
                       }
                   }else {
                       WordsChooser wordsChooser = new WordsChooser(p, WORDNUM, language);
                       wordsChooser.setSize(400, 300);
                       wordsChooser.setVisible(true);
                       CrossWords.this.setFocusable(false);
                   }
               }
            }
        });
        quit.addActionListener(_ -> System.exit(0));
        switchLang.addActionListener(_ -> {
            if (Objects.equals(switchLang.getText(), "English mode")){
                language = "English";
                btn.setText("Choose the word list🧾");
                msg.setText("Word list is a txt file.");
                quit.setText("Quit");
                switchLang.setText("中文");
            }else{
                language = "中文";
                btn.setText("选择词表文件🧾");
                msg.setText("词表文件是一个txt格式的文本文档。");
                quit.setText("退出");
                switchLang.setText("English mode");
            }
        });

    }
    private static Properties FileAnalysis(String fileAbsolutePath) throws Exception{
        Path file = Paths.get(fileAbsolutePath);
        List<String> lines;
        lines = Files.readAllLines(file);

        Properties p = new Properties();
        for (String line : lines){
            String[] strArr = line.split(":");
            p.put(strArr[0], strArr[1]);
        }
        return p;
    }
    public static void main(String[] args) {
        JFrame x = new CrossWords();
        x.setSize(500,200);
        x.setVisible(true);
    }
}