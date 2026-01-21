import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class UI_Play extends JFrame
{
    private final int[][] sudoku;
    private final int[][] ans;
    private final int blockNum_width;
    private final int blockNum_height;
    private int emptyTiles;
    private final int maxEmptyTiles;
    private final Block[] blocks;

    public UI_Play(int[][] sudoku, int[][] ans){
        this.sudoku = sudoku;
        this.ans = ans;
        if(sudoku.length == 4){
            blockNum_width = blockNum_height = 2;
            emptyTiles = maxEmptyTiles = 11;
            setSize(400, 400);
        } else if (sudoku.length == 6) {
            blockNum_width = 3;
            blockNum_height = 2;
            emptyTiles = maxEmptyTiles = 17;
            setSize(600, 600);
        }else{
            blockNum_width = blockNum_height = 3;
            emptyTiles = maxEmptyTiles = 58;
            setSize(900, 900);
        }
        blocks = new Block[sudoku.length];
        /*界面绘制*/
        if (sudoku.length == 6) this.setLayout(new GridLayout(3, 2, 10, 10));  //针对六宫数独的情况特殊处理
        else this.setLayout(new GridLayout(blockNum_height, blockNum_width, 10, 10));
        for (int i = 0; i < blockNum_height * blockNum_width; i++) {
            blocks[i] = new Block(i);
            blocks[i].setBorder(BorderFactory.createLineBorder(Color.gray, 2));
            this.add(blocks[i]);
        }
        setVisible(true);
    }

    /**
     * 操作emptyTiles变量，为0时自动触发检查。true表示减1，false表示加1。
     * @author Gt
     */
    public void OperateEmptyTiles(boolean op){
        if (op && this.emptyTiles != 0){
            emptyTiles--;
            if (emptyTiles == 0){
                boolean[] barr = new boolean[sudoku.length];
                for (int i = 0; i < blocks.length; i++){
                    barr[i] = blocks[i].Check();
                }
                for (boolean b: barr){
                    if(!b){
                        //System.out.println("将显示正确答案。");
                        try{
                            FileWriter fileWriter = new FileWriter("Ans_" + new Date().toString().replace(' ', '_') + ".txt");
                            fileWriter.write(Arrays.deepToString(ans));
                            fileWriter.close();
                        }catch (IOException e){e.printStackTrace();}
                        SwingUtilities.invokeLater(() ->{new UI_Answer(ans);});
                        return;
                    }
                }
                try{
                    FileWriter fileWriter = new FileWriter("Ans_" + new Date().toString().replace(' ', '_') + ".txt");
                    fileWriter.write(Arrays.deepToString(ans));
                    fileWriter.close();
                }catch (IOException e){e.printStackTrace();}
                JOptionPane.showMessageDialog(this, "恭喜！您填对了所有格子！", "您完成了数独！", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if(!op && this.emptyTiles <= maxEmptyTiles) emptyTiles++;
    }

    /*内嵌类，用来绘制各个块*/
    class Block extends JPanel{
        private int start_x;
        private int start_y;
        private final int num;
        private final JTextField[] jTextFields;

        public void paintComponect(Graphics g){
            super.paintComponent(g);  //调用父类方法绘制背景
        }
        public Block(int num){
            this.num = num;
            /*判断是几宫数组并采取对应赋值操作*/
            switch (blockNum_width * blockNum_height){
                case 4:
                    sudoku_4();
                    break;
                case 6:
                    sudoku_6();
                    break;
                case 9:
                    sudoku_9();
                    break;
            }
            /*界面绘制*/
            this.setLayout(new GridLayout(blockNum_height, blockNum_width));
            int t = blockNum_height * blockNum_width;
            jTextFields = new JTextField[t];
            for (int i = 0; i < t; i++){
                jTextFields[i] = new JTextField(1);
                jTextFields[i].setFont(new Font("Arial", Font.PLAIN, 20));
                jTextFields[i].setHorizontalAlignment(JTextField.CENTER);
                int x_offset = i % blockNum_width;
                int y_offset = i / blockNum_width;
                if (sudoku[start_x + x_offset][start_y + y_offset] == 0){
                    Document document = jTextFields[i].getDocument();
                    document.addDocumentListener(new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {OperateEmptyTiles(true);}

                        @Override
                        public void removeUpdate(DocumentEvent e) {OperateEmptyTiles(false);}

                        @Override
                        public void changedUpdate(DocumentEvent e) {OperateEmptyTiles(true);}
                    });
                }else{
                    jTextFields[i].setEditable(false);
                    jTextFields[i].setText("" + sudoku[start_x + x_offset][start_y + y_offset]);
                }
                this.add(jTextFields[i]);
            }
        }
        public boolean Check(){
            boolean ret_val = true;
            for (int i = 0; i < jTextFields.length; i++){
                int x_offset = i % blockNum_width;
                int y_offset = i / blockNum_width;
                if (!Objects.equals(jTextFields[i].getText(), String.valueOf(ans[start_x + x_offset][start_y + y_offset]))) {
                    jTextFields[i].setForeground(Color.RED);
                    ret_val = false;
                } else jTextFields[i].setForeground(Color.GREEN);
                jTextFields[i].repaint();  //强制刷新组件
            }
            if (ret_val){
                for(JTextField jtf: jTextFields)  jtf.setEditable(false);
            }
            return ret_val;
        }
        private void sudoku_4(){
            switch(num){
                case 0:
                    start_x = start_y = 0;
                    break;
                case 1:
                    start_x = 2;
                    start_y = 0;
                    break;
                case 2:
                    start_x = 0;
                    start_y = 2;
                    break;
                case 3:
                    start_x = start_y = 2;
                    break;
            }
        }
        private void sudoku_6(){
            switch(num){
                case 0:
                    start_x = start_y = 0;
                    break;
                case 1:
                    start_x = 3;
                    start_y = 0;
                    break;
                case 2:
                    start_x = 0;
                    start_y = 2;
                    break;
                case 3:
                    start_x = 3;
                    start_y = 2;
                    break;
                case 4:
                    start_x = 0;
                    start_y = 4;
                    break;
                case 5:
                    start_x = 3;
                    start_y = 4;
                    break;
            }
        }
        private void sudoku_9(){
            switch(num){
                case 0:
                    start_x = start_y = 0;
                    break;
                case 1:
                    start_x = 3;
                    start_y = 0;
                    break;
                case 2:
                    start_x = 6;
                    start_y = 0;
                    break;
                case 3:
                    start_x = 0;
                    start_y = 3;
                    break;
                case 4:
                    start_x = start_y = 3;
                    break;
                case 5:
                    start_x = 6;
                    start_y = 3;
                    break;
                case 6:
                    start_x = 0;
                    start_y = 6;
                    break;
                case 7:
                    start_x = 3;
                    start_y = 6;
                    break;
                case 8:
                    start_x = start_y = 6;
                    break;
            }
        }
    }
}
