import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;

public class UI_play extends JFrame implements ActionListener{
    private final Properties words_hints;
    private Collection<Object> hints;
    private final Tile[][] blueprint;
    private int rows, cols, finished;
    private static final int len = 64;
    private static final int gapLen = 10;
    private final JMenu preference;
    private final JMenu languageSetting;
    private final JMenu help;
    private final JMenuItem changeColor;
    private final JMenuItem changeFont;
    private final JMenuItem help3;  //更改颜色、字体、帮填3个选项
    private final JRadioButtonMenuItem CHN, ENG;  //单选菜单项目
    private static MyPanel myPanel;
    private final String language;
    private boolean helpHasUsed;

    private final Map<Point, JTextField> pointJTextFieldMap;
    private final Map<JTextField, Point> jTextFieldPointMap;
    private final Map<Document, JTextField> documentJTextFieldMap;

    /*public UI_play() {
        myPanel = new MyPanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Mypanel demo"));
        setContentPane(myPanel);
        setVisible(true);
    }*/
    public UI_play(Properties p, Tile[][] b){
        helpHasUsed = false;
        pointJTextFieldMap = new HashMap<>();
        jTextFieldPointMap = new HashMap<>();
        documentJTextFieldMap = new HashMap<>();
        setVisible(true);
        words_hints = p;
        language = "中文";
        finished = 0;
        if (p != null){
            hints = words_hints.values();
            SwingUtilities.invokeLater(() ->{HintShower hs = new HintShower(hints.toArray(), language);});
        }
        blueprint = b;
        if (b != null){
            rows = blueprint.length;
            cols = blueprint[0].length;
            setSize(rows * len + (rows - 1) * gapLen, cols * len + (cols - 1) * gapLen);
        }
        myPanel = new MyPanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("纵横字谜游戏(CrossWords)"));
        setContentPane(myPanel);

        /*Swing下拉菜单设置*/
        JMenuBar menuBar = new JMenuBar();
        preference  = new JMenu();
        languageSetting  = new JMenu();
        help = new JMenu();

        changeColor = new JMenuItem();
        changeFont = new JMenuItem();
        CHN = new JRadioButtonMenuItem("中文");
        CHN.setSelected(true);
        ENG = new JRadioButtonMenuItem("English");
        help3 = new JMenuItem();
        /*根据颜色调整显示内容*/
        ChineseView();
        preference.add(changeColor);
        preference.add(changeFont);
        menuBar.add(preference);
        menuBar.add(languageSetting);
        menuBar.add(help);
        ButtonGroup group = new ButtonGroup();
        group.add(CHN);
        group.add(ENG);
        languageSetting.add(CHN);
        languageSetting.add(ENG);
        help.add(help3);
        /*为菜单项注册事件监听器*/
        changeColor.addActionListener(this);
        changeFont.addActionListener(this);
        CHN.addActionListener(this);
        ENG.addActionListener(this);
        help3.addActionListener(this);

        setJMenuBar(menuBar);
    }

    public static MyPanel MyPanel(){
        return myPanel;
    }

    class MyPanel extends JPanel{
        public MyPanel() {
            this.setLayout(new GridLayout(rows, cols));
            //this.setBackground(Color.GREEN);
            for (int i = 0; i < rows; i++){
                for (int j = 0; j < cols; j++){
                    int FONTSIZE = 20;
                    if (blueprint[i][j].getStatus() == TileStatus.DECORATION){
                        this.add(new JLabel());
                    }else if (blueprint[i][j].getStatus() == TileStatus.CROSS) {
                        Font testFont = new Font("Times New Roman", Font.BOLD, FONTSIZE);
                        JTextField jtf = new JTextField(blueprint[i][j].getCh() + "", 1);
                        jtf.setFont(testFont);
                        jtf.setEditable(false);
                        jtf.setHorizontalAlignment(SwingConstants.CENTER);
                        pointJTextFieldMap.put(new Point(i, j), jtf);
                        jTextFieldPointMap.put(jtf, new Point(i, j));
                        jtf.addFocusListener(new FocusListener() {
                            @Override
                            public void focusGained(FocusEvent e) { }
                            @Override
                            public void focusLost(FocusEvent e) {
                                Point p = jTextFieldPointMap.get((JTextField) e.getSource());  //获得触发该事件的JTextField的点坐标
                                int x = p.getX();
                                int y = p.getY();
                                //System.out.println(p);
                                TileStatus s = blueprint[x][y].getStatus();
                                Point p2 = new Point(x, y + 1);  //一个水平方向的试探点
                                if (s == TileStatus.END_HORIZONTAL || pointJTextFieldMap.get(p2) == null){
                                    //System.out.println("1111");
                                    /*检查前面的字母是否都填对了*/
                                    if (Check_H(p)){
                                        for (; blueprint[x][y].getStatus() != TileStatus.START_HORIZONTAL; y--){
                                            JTextField textField = pointJTextFieldMap.get(new Point(x, y));
                                            textField.setForeground(Color.GREEN); // 设置文本颜色为绿色
                                            textField.repaint(); // 强制刷新组件
                                            textField.setEditable(false);  //设置为不可编辑
                                        }
                                        pointJTextFieldMap.get(new Point(x, y)).setForeground(Color.GREEN);
                                        pointJTextFieldMap.get(new Point(x, y)).repaint();
                                        pointJTextFieldMap.get(new Point(x, y)).setEditable(false);
                                        finished++;
                                        if (finished == words_hints.size()){
                                            if (Objects.equals(language, "中文")) JOptionPane.showMessageDialog(UI_play.this, "恭喜！您填完了所有词！");
                                            else if (Objects.equals(language, "English")) JOptionPane.showMessageDialog(UI_play.this, "Congratulation! You filled all blacks!");
                                        }
                                    }else {
                                        for (; blueprint[x][y].getStatus() != TileStatus.START_HORIZONTAL; y--){
                                            JTextField textField = pointJTextFieldMap.get(new Point(x, y));
                                            textField.setForeground(Color.RED); // 设置文本颜色为红色
                                            textField.repaint(); // 强制刷新组件
                                        }
                                        pointJTextFieldMap.get(new Point(x, y)).setForeground(Color.RED);
                                        pointJTextFieldMap.get(new Point(x, y)).repaint();
                                    }
                                }
                                if (s == TileStatus.END_VERTICAL){
                                    /*检查前面的字母是否都填对了*/
                                    if (Check_V(p)){
                                        for (; blueprint[x][y].getStatus() != TileStatus.START_VERTICAL; x--){
                                            if (blueprint[x][y].getStatus() == TileStatus.DECORATION) break;
                                            JTextField textField = pointJTextFieldMap.get(new Point(x, y));
                                            textField.setForeground(Color.GREEN); // 设置文本颜色为绿色
                                            textField.repaint(); // 强制刷新组件
                                            textField.setEditable(false);  //设置为不可编辑
                                        }
                                        /*以下代码是为了修改第一个文本框的属性*/
                                        if (blueprint[x][y].getStatus() != TileStatus.DECORATION){
                                            pointJTextFieldMap.get(new Point(x, y)).setForeground(Color.GREEN);
                                            pointJTextFieldMap.get(new Point(x, y)).repaint();
                                            pointJTextFieldMap.get(new Point(x, y)).setEditable(false);
                                        }
                                        finished++;
                                        if (finished == words_hints.size()){
                                            if (Objects.equals(language, "中文")) JOptionPane.showMessageDialog(UI_play.this, "恭喜！您填完了所有词！");
                                            else if (Objects.equals(language, "English")) JOptionPane.showMessageDialog(UI_play.this, "Congratulation! You filled all blacks!");
                                        }
                                    }else {
                                        for (; blueprint[x][y].getStatus() != TileStatus.START_VERTICAL; x--){
                                            if (blueprint[x][y].getStatus() == TileStatus.DECORATION) break;
                                            JTextField textField = pointJTextFieldMap.get(new Point(x, y));
                                            textField.setForeground(Color.RED); // 设置文本颜色为红色
                                            textField.repaint(); // 强制刷新组件
                                        }
                                        /*以下代码是为了修改第一个文本框的属性*/
                                        if (blueprint[x][y].getStatus() != TileStatus.DECORATION){
                                            pointJTextFieldMap.get(new Point(x, y)).setForeground(Color.RED);
                                            pointJTextFieldMap.get(new Point(x, y)).repaint();
                                        }
                                    }
                                }
                            }
                        });
                        this.add(jtf);
                    }else{
                        Font testFont = new Font("Arial", Font.PLAIN, FONTSIZE);
                        JTextField jtf = new JTextField(1);
                        jtf.setFont(testFont);
                        jtf.setHorizontalAlignment(SwingConstants.CENTER);
                        pointJTextFieldMap.put(new Point(i, j), jtf);
                        jTextFieldPointMap.put(jtf, new Point(i, j));
                        Document document = jtf.getDocument();
                        documentJTextFieldMap.put(document, jtf);
                        jtf.addFocusListener(new FocusListener() {
                            @Override
                            public void focusGained(FocusEvent e) { }

                            @Override
                            public void focusLost(FocusEvent e) {
                                Point p = jTextFieldPointMap.get((JTextField) e.getSource());  //获得触发该事件的JTextField的点坐标
                                int x = p.getX();
                                int y = p.getY();
                                //System.out.println(p);
                                TileStatus s = blueprint[x][y].getStatus();
                                //Point p2 = new Point(x, y + 1);  //一个水平方向的试探点
                                if (s == TileStatus.END_HORIZONTAL){
                                    //System.out.println("1111");
                                    /*检查前面的字母是否都填对了*/
                                    if (Check_H(p)){
                                        for (; y >= 0 && blueprint[x][y].getStatus() != TileStatus.START_HORIZONTAL; y--){
                                            if (blueprint[x][y].getStatus() == TileStatus.DECORATION) break;
                                            JTextField textField = pointJTextFieldMap.get(new Point(x, y));
                                            textField.setForeground(Color.GREEN); // 设置文本颜色为绿色
                                            textField.repaint(); // 强制刷新组件
                                            textField.setEditable(false);  //设置为不可编辑
                                        }
                                        /*以下3行是为了修改第一个文本框的属性*/
                                        if (y >= 0 && blueprint[x][y].getStatus() != TileStatus.DECORATION){
                                            pointJTextFieldMap.get(new Point(x, y)).setForeground(Color.GREEN);
                                            pointJTextFieldMap.get(new Point(x, y)).repaint();
                                            pointJTextFieldMap.get(new Point(x, y)).setEditable(false);
                                        }
                                        finished++;
                                        if (finished == words_hints.size()){
                                            if (Objects.equals(language, "中文")) JOptionPane.showMessageDialog(UI_play.this, "恭喜！您填完了所有词！");
                                            else if (Objects.equals(language, "English")) JOptionPane.showMessageDialog(UI_play.this, "Congratulation! You filled all blacks!");
                                        }
                                    }else {
                                        for (; y >= 0 && blueprint[x][y].getStatus() != TileStatus.START_HORIZONTAL; y--){
                                            if (blueprint[x][y].getStatus() == TileStatus.DECORATION) break;
                                            JTextField textField = pointJTextFieldMap.get(new Point(x, y));
                                            textField.setForeground(Color.RED); // 设置文本颜色为红色
                                            textField.repaint(); // 强制刷新组件
                                        }
                                        if (y >= 0 && blueprint[x][y].getStatus() != TileStatus.DECORATION){
                                            pointJTextFieldMap.get(new Point(x, y)).setForeground(Color.RED);
                                            pointJTextFieldMap.get(new Point(x, y)).repaint();
                                        }
                                    }
                                }
                                if (s == TileStatus.END_VERTICAL){
                                    /*检查前面的字母是否都填对了*/
                                    if (Check_V(p)){
                                        for (; x >= 0 && blueprint[x][y].getStatus() != TileStatus.START_VERTICAL; x--){
                                            if (blueprint[x][y].getStatus() == TileStatus.DECORATION) break;
                                            JTextField textField = pointJTextFieldMap.get(new Point(x, y));
                                            textField.setForeground(Color.GREEN); // 设置文本颜色为绿色
                                            textField.repaint(); // 强制刷新组件
                                            textField.setEditable(false);
                                        }
                                        //以下代码是为了修改第一个文本框的属性
                                        if (x >=0 && blueprint[x][y].getStatus() != TileStatus.DECORATION){
                                            pointJTextFieldMap.get(new Point(x, y)).setForeground(Color.GREEN);
                                            pointJTextFieldMap.get(new Point(x, y)).repaint();
                                            pointJTextFieldMap.get(new Point(x, y)).setEditable(false);
                                        }
                                        finished++;
                                        if (finished == words_hints.size()){
                                            if (Objects.equals(language, "中文")) JOptionPane.showMessageDialog(UI_play.this, "恭喜！您填完了所有词！");
                                            else if (Objects.equals(language, "English")) JOptionPane.showMessageDialog(UI_play.this, "Congratulation! You filled all blacks!");
                                        }
                                    }else {
                                        for (; x >= 0 && blueprint[x][y].getStatus() != TileStatus.START_VERTICAL; x--){
                                            if ( blueprint[x][y].getStatus() == TileStatus.DECORATION) break;
                                            JTextField textField = pointJTextFieldMap.get(new Point(x, y));
                                            textField.setForeground(Color.RED); // 设置文本颜色为红色
                                            textField.repaint(); // 强制刷新组件
                                        }
                                        /*以下代码是为了修改第一个文本框的属性*/
                                        if (x >= 0 && blueprint[x][y].getStatus() != TileStatus.DECORATION){
                                            pointJTextFieldMap.get(new Point(x, y)).setForeground(Color.RED);
                                            pointJTextFieldMap.get(new Point(x, y)).repaint();
                                        }
                                    }
                                }
                            }
                        });
                        document.addDocumentListener(new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                JTextField t = documentJTextFieldMap.get(e.getDocument());  //由Document对象得到其对应的JTextField
                                Point p = jTextFieldPointMap.get(t);
                                int x = p.getX();
                                int y = p.getY();
                                TileStatus s = blueprint[x][y].getStatus();
                                if (s == TileStatus.START_HORIZONTAL || s == TileStatus.HAS_CHAR_HORIZONTAL){
                                    do {
                                        if (blueprint[x][y].getStatus() == TileStatus.END_HORIZONTAL) break;
                                        y++;
                                        Point p2 = new Point(x, y);
                                        JTextField j2 = pointJTextFieldMap.get(p2);
                                        if (j2 == null) break;
                                        j2.requestFocusInWindow();
                                    }while (blueprint[x][y].getStatus() == TileStatus.CROSS);
                                    s = blueprint[x][y - 1].getStatus();  //回退一格，否则越界
                                }
                                if (s == TileStatus.START_VERTICAL || s == TileStatus.HAS_CHAR_VERTICAL){
                                    do {
                                        if (blueprint[x][y].getStatus() == TileStatus.END_VERTICAL) break;
                                        x++;
                                        Point p2 = new Point(x, y);
                                        JTextField j2 = pointJTextFieldMap.get(p2);
                                        if (j2 == null) break;
                                        j2.requestFocusInWindow();
                                    }while (blueprint[x][y].getStatus() == TileStatus.CROSS);
                                    s = blueprint[x - 1][y].getStatus();  //回退一格，否则越界
                                }
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) { }

                            @Override
                            public void changedUpdate(DocumentEvent e) { }
                        });
                        this.add(jtf);
                    }
                }
            }

        }
        public boolean Check_H(Point point){
            int x = point.getX();
            int y = point.getY();
            //if (!(pointJTextFieldMap.get(point).getText()).equals(blueprint[x][y].toString())) return false;
            while (y >=0 && blueprint[x][y].getStatus() != TileStatus.START_HORIZONTAL){
                //System.out.println("Text=" + pointJTextFieldMap.get(new Point(x, y)).getText() + " Ans=" + blueprint[x][y].toString());
                if (!(pointJTextFieldMap.get(new Point(x, y)).getText()).equals(blueprint[x][y].toString())) return false;
                y--;
            }
            return true;
        }
        public boolean Check_V(Point point){
            int x = point.getX();
            int y = point.getY();
            /*do {
                System.out.println("Text=" + pointJTextFieldMap.get(new Point(x, y)).getText() + " Ans=" + blueprint[x][y].toString());
                if (!(pointJTextFieldMap.get(new Point(x, y)).getText()).equals(blueprint[x][y].toString())) return false;
                x--;
            }while (blueprint[x][y].getStatus() == TileStatus.DECORATION || blueprint[x][y].getStatus() == TileStatus.START_VERTICAL);*/
            //Point tryp = new Point(x, y);  //试探点
            while (x >= 0 && blueprint[x][y].getStatus() != TileStatus.START_VERTICAL){
                if (pointJTextFieldMap.get(new Point(x, y)) == null) break;
                //System.out.println("Text=" + pointJTextFieldMap.get(new Point(x, y)).getText() + " Ans=" + blueprint[x][y].toString());
                if (!(pointJTextFieldMap.get(new Point(x, y)).getText()).equals(blueprint[x][y].toString())) return false;
                //tryp.setX(--x);
                x--;
            }
            return true;
        }
    }

    private void ChineseView(){
        preference.setText("外观");
        languageSetting.setText("Language");
        help.setText("帮助");
        changeColor.setText("更改颜色");
        changeFont.setText("更改字体");
        help3.setText("帮填3个字母");
    }
    private void EnglishView(){
        preference.setText("Preference");
        languageSetting.setText("切换语言");
        help.setText("Help");
        changeColor.setText("Change Colour");
        changeFont.setText("Change Font");
        help3.setText("Fill 3 blacks");
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == changeColor){
            Color color = JColorChooser.showDialog(UI_play.this, "选择背景颜色(Choose background colour)", UI_play.this.getBackground());
            if (color != null){
                UI_play.MyPanel().setBackground(color);
            }
        }else if(e.getSource() == changeFont){
            FontChooser fontChooser = new FontChooser(UI_play.this, "选择字体", true);
            fontChooser.setVisible(true);

            if (fontChooser.isOkPressed()) {
                Font selectedFont = fontChooser.getSelectedFont();
                Set<JTextField> s = jTextFieldPointMap.keySet();
                for (JTextField jtf : s) {
                    jtf.setFont(selectedFont);
                }
            }
        } else if (e.getSource() == help3) {
            if (helpHasUsed){
                if (Objects.equals(language, "中文")){
                    JOptionPane.showMessageDialog(UI_play.this, "帮填虽好，可不要贪多哦！");
                }else{
                    JOptionPane.showMessageDialog(UI_play.this, "It seems that you have used this function🤪");
                }
            }else{
                int i = 0;
                for (JTextField jtf: jTextFieldPointMap.keySet()){
                    if (i <= 2){
                        i++;
                        int _x = jTextFieldPointMap.get(jtf).getX();
                        int _y = jTextFieldPointMap.get(jtf).getY();
                        jtf.setText(blueprint[_x][_y].toString());
                        jtf.setEditable(false);
                        helpHasUsed = true;
                    }
                }
            }
        } else if (e.getSource() == CHN) {
            ChineseView();
        } else if (e.getSource() == ENG) {
            EnglishView();
        }
    }
}

class Point{
    private int x, y;
    public Point(){
        x = y = 0;
    }
    public Point(int _x, int _y){
        x = _x;
        y = _y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}