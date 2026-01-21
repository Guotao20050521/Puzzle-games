import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
enum TileStatus{
    DECORATION, HAS_CHAR_HORIZONTAL, HAS_CHAR_VERTICAL, START_HORIZONTAL, START_VERTICAL, END_HORIZONTAL, END_VERTICAL, CROSS;
}

class Tile{
    private char ch;
    private TileStatus status;

    public Tile(){
        ch = '*';
        status = TileStatus.DECORATION;
    }
    public char getCh() {
        return ch;
    }
    public TileStatus getStatus() {
        return status;
    }
    public void setCh(char ch) {
        this.ch = ch;
    }
    public void setStatus(TileStatus status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return ch + "";  // + "_" + status + " ";
        //return status + " ";
    }
}

public class GameGenerator {
    private static String[] wordsArr;
    private static Tile[][] gameSheet;
    private static int rows, cols;
    private static boolean[] put;  //对于排好序后的wordsArr，表征其是否被放置进sheet的标志数组

    public static void Generate(Properties p) throws NullPointerException{
        wordsArr = new String[p.size()];
        /*做出单词数组*/
        StringBuilder buf = new StringBuilder(p.keySet().toString());
        String tmp = buf.substring(1, buf.length() - 1);
        //System.out.println(tmp);
        wordsArr = tmp.split(",");
        for(int i = 0; i < wordsArr.length; i++){
            wordsArr[i] = wordsArr[i].trim().toLowerCase();
            //System.out.println(wordsArr[i]);
        }
        put = new boolean[p.size()];
        Arrays.fill(put, false);
        wordsArr = GameGenerator.SortStr(wordsArr);

        String longest, sec_longest;  //最长的和第二长的单词
        try{
            longest = wordsArr[0];
            sec_longest = wordsArr[1];
        }catch (ArrayIndexOutOfBoundsException e){
            return;
        }

        gameSheet = new Tile[sec_longest.length() + 1][longest.length()];  //分配空间
        rows = sec_longest.length();
        cols = longest.length();
        /*初始化*/
        for (int i = 0; i < rows + 1; i++){
            for (int j = 0; j < cols; j++){
                gameSheet[i][j] = new Tile();
            }
        }

        Character ch = GameGenerator.findFirstCommonLetter(longest, sec_longest);
        //System.out.println(ch);
        if (ch != null){  //说明两个单词有相同的字母
            int longest_row = sec_longest.indexOf(ch);  //最长的单词所在的行
            int sec_longest_col = longest.indexOf(ch);  //第二长的单词应摆在的列号
            GameGenerator.PutTheFirstWord_H(longest, longest_row);
            GameGenerator.PutTheFirstWord_V(sec_longest, sec_longest_col);
            gameSheet[longest_row][sec_longest_col].setStatus(TileStatus.CROSS);  //设置交叉标志
            put[0] = put[1] = true;
            /*放置剩余单词*/
            GameGenerator.Insert_Left(longest_row, sec_longest_col);
            GameGenerator.Insert_Up(longest_row, sec_longest_col);
            GameGenerator.Insert_Right(longest_row, sec_longest_col);
            GameGenerator.Insert_Down(longest_row, sec_longest_col);
        }else {
            PutTheFirstWord_H(longest, 0);
            /*竖向放置第二长的单词*/
            for (int i = 1; i < rows + 1; i++){
                gameSheet[i][0].setCh(sec_longest.charAt(i - 1));
                if (i == 1){
                    gameSheet[i][0].setStatus(TileStatus.START_VERTICAL);
                } else if (i == rows) {
                    gameSheet[i][0].setStatus(TileStatus.END_VERTICAL);
                }else{
                    gameSheet[i][0].setStatus(TileStatus.HAS_CHAR_VERTICAL);
                }
            }
            put[0] = put[1] = true;
            Insert_Left(0, cols);
            Insert_Down(0, 0);
        }
        /*如果这样都还有单词剩余
         * 则是用最暴力的算法：不管交叉，哪里有空塞哪里*/
        if (!AllPut()){
            for (int i = 2; i < put.length; i++){  //两个单词有相同字母的前提下，前两个单词肯定能塞进sheet里
                if (!put[i]){
                    for (int j = 0; j < rows; j++){
                        for (int k = 0; k < cols; k++){
                            boolean h = ObstacleChk_H(k, k + wordsArr[i].length() - 1, j);
                            if (h && !put[i] && gameSheet[j][k].getCh() == '*'){
                                for (int l = k; l < k + wordsArr[i].length(); l++){
                                    gameSheet[j][l].setCh(wordsArr[i].charAt(l - k));
                                    if (l == k){
                                        gameSheet[j][l].setStatus(TileStatus.START_HORIZONTAL);
                                    } else if (l == k + wordsArr[i].length() - 1) {
                                        gameSheet[j][l].setStatus(TileStatus.END_HORIZONTAL);
                                    }else {
                                        gameSheet[j][l].setStatus(TileStatus.HAS_CHAR_HORIZONTAL);
                                    }
                                }
                                put[i] = true;
                            }else {  //如果横着不能放，就再试试看竖着能不能放
                                boolean v = ObstacleChk_V(j, j + wordsArr[i].length() - 1, k);
                                if (v && !put[i] && gameSheet[j][k].getCh() == '*'){
                                    for (int l = j; l < j + wordsArr[i].length(); l++){
                                        gameSheet[l][k].setCh(wordsArr[i].charAt(l - j));
                                        if (l == j){
                                            gameSheet[l][k].setStatus(TileStatus.START_VERTICAL);
                                        } else if (l == j + wordsArr[i].length() - 1) {
                                            gameSheet[l][k].setStatus(TileStatus.END_VERTICAL);
                                        }else {
                                            gameSheet[l][k].setStatus(TileStatus.HAS_CHAR_VERTICAL);
                                        }
                                    }
                                    put[i] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (AllPut()){
            new UI_play(p, gameSheet);
        }
    }
    /*用选择排序法将字符串按长度从长到短排序*/
    private static String[] SortStr(String[] strArr){
        for (int top = 0; top < strArr.length - 1; top++){
            for (int seek = top; seek < strArr.length; seek++){
                if (strArr[top].length() <= strArr[seek].length()){
                    String temp = strArr[seek];
                    strArr[seek] = strArr[top];
                    strArr[top] = temp;
                }
            }
        }
        return strArr;
    }
    private static Character findFirstCommonLetter(String word1, String word2) {
        // 使用一个布尔数组记录word1中出现的字母
        /*boolean[] letterFlags = new boolean[26]; // 英文字母共26个

        // 遍历第一个单词，标记出现的字母
        for (char c : word1.toCharArray()) {
            letterFlags[c - 'a'] = true;
        }

        // 遍历第二个单词，找到第一个在word1中也出现的字母
        for (char c : word2.toCharArray()) {
            if (letterFlags[c - 'a']) {
                return c; // 返回第一个相同的字母
            }
        }

        return null; // 如果没有相同字母，返回null
        */
        // 使用HashSet存储word1中出现的字符
        HashSet<Character> charSet = new HashSet<>();

        // 遍历第一个字符串，将所有字符存入HashSet
        for (char c : word1.toCharArray()) {
            charSet.add(c);
        }

        // 遍历第二个字符串，找到第一个在word1中也出现的字符
        for (char c : word2.toCharArray()) {
            if (charSet.contains(c)) {
                return c; // 返回第一个相同的字符
            }
        }

        return null; // 如果没有相同字符，返回null
    }
    /*放置水平方向的第一个单词*/
    private static void PutTheFirstWord_H(String str, int row){
        for (int i = 0; i < cols; i++){
            gameSheet[row][i].setCh(str.charAt(i));
            if (i == 0){
                gameSheet[row][i].setStatus(TileStatus.START_HORIZONTAL);
            } else if (i == cols - 1) {
                gameSheet[row][i].setStatus(TileStatus.END_HORIZONTAL);
            }else{
                gameSheet[row][i].setStatus(TileStatus.HAS_CHAR_HORIZONTAL);
            }
        }
    }
    /*放置垂直方向的第一个单词*/
    private static void PutTheFirstWord_V(String str, int col){
        for (int i = 0; i < rows; i++){
            gameSheet[i][col].setCh(str.charAt(i));
            if (i == 0){
                gameSheet[i][col].setStatus(TileStatus.START_VERTICAL);
            } else if (i == rows - 1) {
                gameSheet[i][col].setStatus(TileStatus.END_VERTICAL);
            }else{
                gameSheet[i][col].setStatus(TileStatus.HAS_CHAR_VERTICAL);
            }
        }
    }
    /*检查所有单词是否都已被加入sheet*/
    private static boolean AllPut(){
        for (boolean b : put) {
            if (!b) return false;
        }
        return true;
    }

    private static void Insert_Left(int x, int y){
        if (!AllPut() && x >= 0 && y > 0){
            for (int i = 2; i < wordsArr.length; i++) {
                if (!put[i]){  //第i个单词未放置
                    for (int j = y - 1; j >= 0; j--){
                        int index = wordsArr[i].indexOf(gameSheet[x][j].getCh());
                        /*检查是否满足没有障碍、有公共字母
                        * 没有方可插入单词*/
                        if (index != -1 && ObstacleChk_V(x, x - index, j) && ObstacleChk_V(x, wordsArr[i].length() + x - 1 - index, j)){
                            for (int k = x - index; k < x - index + wordsArr[i].length(); k++){
                                gameSheet[k][j].setCh(wordsArr[i].charAt(k - x + index));
                                if (k == x - index){
                                    gameSheet[k][j].setStatus(TileStatus.START_VERTICAL);
                                } else if (k == (x - index + wordsArr[i].length() - 1)) {
                                    gameSheet[k][j].setStatus(TileStatus.END_VERTICAL);
                                }else{
                                    gameSheet[k][j].setStatus(TileStatus.HAS_CHAR_VERTICAL);
                                }
                            }
                            gameSheet[x][j].setStatus(TileStatus.CROSS);
                            put[i] = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    private static void Insert_Right(int x, int y){
        if (!AllPut() && x >= 0 && y >= 0 && y < cols){
            for (int i = 2; i < wordsArr.length; i++) {  //因为前两个单词已经放进去了，所以从第三个单词开始放置
                if (!put[i]){  //第i个单词未放置
                    for (int j = y + 1; j < cols; j++){
                        int index = wordsArr[i].indexOf(gameSheet[x][j].getCh());
                        /*检查是否满足没有障碍、有公共字母
                         * 没有方可插入单词*/
                        if (index != -1 && ObstacleChk_V(x, x - index, j) && ObstacleChk_V(x, wordsArr[i].length() + x - 1 - index, j)){
                            for (int k = x - index; k < x - index + wordsArr[i].length(); k++){
                                gameSheet[k][j].setCh(wordsArr[i].charAt(k - x + index));
                                if (k == x - index){
                                    gameSheet[k][j].setStatus(TileStatus.START_VERTICAL);
                                } else if (k == (x - index + wordsArr[i].length() - 1)) {
                                    gameSheet[k][j].setStatus(TileStatus.END_VERTICAL);
                                }else{
                                    gameSheet[k][j].setStatus(TileStatus.HAS_CHAR_VERTICAL);
                                }
                            }
                            gameSheet[x][j].setStatus(TileStatus.CROSS);  //设置交叉标志
                            put[i] = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    private static void Insert_Up(int x, int y){
        if (!AllPut() && x > 0 && y >= 0 && y < cols){
            for (int i = 2; i < wordsArr.length; i++) {  //因为前两个单词已经放进去了，所以从第三个单词开始放置
                if (!put[i]){  //第i个单词未放置
                    for (int j = x - 1; j >= 0; j--){
                        int index = wordsArr[i].indexOf(gameSheet[j][y].getCh());
                        /*检查是否满足没有障碍、有公共字母
                         * 没有方可插入单词*/
                        if (index != -1 && ObstacleChk_H(y, y - index, j) && ObstacleChk_V(y, wordsArr[i].length() + y - 1 - index, j)){
                            for (int k = y - index; k < y - index + wordsArr[i].length(); k++){
                                gameSheet[j][k].setCh(wordsArr[i].charAt(k - y + index));
                                if (k == y - index){
                                    gameSheet[j][k].setStatus(TileStatus.START_HORIZONTAL);
                                } else if (k == (y - index + wordsArr[i].length() - 1)) {
                                    gameSheet[j][k].setStatus(TileStatus.END_HORIZONTAL);
                                }else{
                                    gameSheet[j][k].setStatus(TileStatus.HAS_CHAR_HORIZONTAL);
                                }
                            }
                            gameSheet[j][y].setStatus(TileStatus.CROSS);  //设置交叉标志
                            put[i] = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    private static void Insert_Down(int x, int y){
        if (!AllPut() && x >= 0 && y >= 0){
            for (int i = 2; i < wordsArr.length; i++) {  //因为前两个单词已经放进去了，所以从第三个单词开始放置
                if (!put[i]){  //第i个单词未放置
                    for (int j = x + 1; j < rows; j++){
                        int index = wordsArr[i].indexOf(gameSheet[j][y].getCh());
                        /*检查是否满足没有障碍、有公共字母
                         * 没有方可插入单词*/
                        if (index != -1 && ObstacleChk_H(y, y - index, j) && ObstacleChk_V(y, wordsArr[i].length() + y - 1 - index, j)){
                            for (int k = y - index; k < y - index + wordsArr[i].length(); k++){
                                gameSheet[j][k].setCh(wordsArr[i].charAt(k - y + index));
                                if (k == y - index){
                                    gameSheet[j][k].setStatus(TileStatus.START_HORIZONTAL);
                                } else if (k == (y - index + wordsArr[i].length() - 1)) {
                                    gameSheet[j][k].setStatus(TileStatus.END_HORIZONTAL);
                                }else{
                                    gameSheet[j][k].setStatus(TileStatus.HAS_CHAR_HORIZONTAL);
                                }
                            }
                            gameSheet[j][y].setStatus(TileStatus.CROSS);  //设置交叉标志
                            put[i] = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    /*返回false说明有障碍，下同*/
    private static boolean ObstacleChk_V(int x_start, int x_end, int y){
        try{
            if (x_start > x_end){
                for (int i = x_start - 1; i >= x_end; i--) {
                    if (gameSheet[i][y].getStatus() != TileStatus.DECORATION){
                        return false;
                    }
                }
                return true;
            } else if (x_start == x_end) {
                return true;
            }else {
                for (int i = x_start + 1; i <= x_end; i++) {
                    if (gameSheet[i][y].getStatus() != TileStatus.DECORATION){
                        return false;
                    }
                }
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {  //空间不足，位置不够放
            return false;
        }
    }
    private static boolean ObstacleChk_H(int y_start, int y_end, int x){
        try{
            if (y_start > y_end){
                for (int i = y_start - 1; i >= y_end; i--) {
                    if (gameSheet[x][i].getStatus() != TileStatus.DECORATION){
                        return false;
                    }
                }
                return true;
            } else if (y_start == y_end) {
                return true;
            }else {
                for (int i = y_start + 1; i <= y_end; i++) {
                    if (gameSheet[x][i].getStatus() != TileStatus.DECORATION){
                        return false;
                    }
                }
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {  //空间不足，位置不够放
            return false;
        }
    }
}