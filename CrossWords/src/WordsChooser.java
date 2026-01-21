import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
public class WordsChooser extends JFrame implements ActionListener {
    private String[] wordsArr;
    private final Properties pr;
    private final Properties origin;
    private final int maxWords;
    private String language;
    private final JLabel msg;
    private final JRadioButton selectFront;
    private final JRadioButton selectEnd;
    private final JRadioButton selectRandomly;
    private final JButton switchLang;
    public WordsChooser(Properties p, int wordNum, String lang){
        language = lang;
        maxWords = wordNum;
        wordsArr = new String[p.size()];
        pr = new Properties(wordNum);
        origin = p;

        StringBuilder buf = new StringBuilder(p.keySet().toString());
        String tmp = buf.substring(1, buf.length() - 1);
        //System.out.println(tmp);
        wordsArr = tmp.split(",");
        for(int i = 0; i < wordsArr.length; i++){
            wordsArr[i] = wordsArr[i].trim();
            //System.out.println(wordsArr[i]);
        }

        this.setLayout(new GridLayout(5, 1, 10,10));
        msg = new JLabel();
        this.add(msg);

        selectFront = new JRadioButton();
        selectFront.setActionCommand("Front");
        selectEnd = new JRadioButton();
        selectEnd.setActionCommand("End");
        selectRandomly = new JRadioButton();
        selectRandomly.setActionCommand("Randomly");

        ButtonGroup choices = new ButtonGroup();
        choices.add(selectFront);
        choices.add(selectEnd);
        choices.add(selectRandomly);

        this.add(selectFront);
        this.add(selectEnd);
        this.add(selectRandomly);

        selectFront.addActionListener(this);
        selectEnd.addActionListener(this);
        selectRandomly.addActionListener(this);

        switchLang = new JButton();
        switchLang.addActionListener(_ -> {
            if (Objects.equals(language, "中文"))  EnglishView();
            else  ChineseView();
        });
        this.add(switchLang);
        /*根据语言调整*/
        if (Objects.equals(language, "中文")){
            ChineseView();
        } else if (Objects.equals(language, "English")) {
            EnglishView();
        }
    }

    private static List<Integer> GenerateUniqueRandomNum(int max, int n){
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i <= max; i++){
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers.subList(0, n);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pr.clear();
        String choices = e.getActionCommand();
        if (Objects.equals(choices, "Front")){
            for (int i = 0; i < maxWords; i++){
                pr.put(wordsArr[i], origin.get(wordsArr[i]));
            }
        } else if (Objects.equals(choices, "End")) {
            for (int i = wordsArr.length - 1; i >= wordsArr.length - maxWords; i--){
                pr.put(wordsArr[i], origin.get(wordsArr[i]));
            }
        } else if (Objects.equals(choices, "Randomly")) {
            List<Integer> l = GenerateUniqueRandomNum(wordsArr.length - 1, maxWords);
            for (Integer num : l) {
                pr.put(wordsArr[num], origin.get(wordsArr[num]));
            }
        }
        GameGenerator.Generate(pr);
    }

    private void EnglishView(){
        msg.setText("There are many words in your word sheet. But you can only choose " + maxWords + " to join the game." +
                "How do you want to choose?");
        selectFront.setText(maxWords + " words in the front");
        selectEnd.setText(maxWords + " words in the end");
        selectRandomly.setText("Choose " + maxWords + " randomly");
        language = "English";
        switchLang.setText("切换为中文");
    }
    private void ChineseView(){
        msg.setText("您的词表中有许多单词，但只能选" + maxWords + "个加入游戏。请问您要如何选择？");
        selectFront.setText("选择头" + maxWords + "个");
        selectEnd.setText("选择最后" + maxWords + "个");
        selectRandomly.setText("随机选" + maxWords + "个");
        language = "中文";
        switchLang.setText("Switch to English");
    }
}