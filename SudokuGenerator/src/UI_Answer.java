import javax.swing.*;
import java.awt.*;

public class UI_Answer extends JFrame
{
    public UI_Answer(int[][] answer) {
        super("你填错了！正确答案如下：");
        int len = answer.length;
        setLayout(new GridLayout(len, len, 10, 10));
        // 使用嵌套循环并交换行列索引以显示转置矩阵
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                JLabel label = new JLabel(String.valueOf(answer[j][i])); // 注意这里是 [j][i] 实现转置
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                //label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                this.add(label);
            }
        }
        setSize(len * 100, len * 100);
        setVisible(true);
    }
}