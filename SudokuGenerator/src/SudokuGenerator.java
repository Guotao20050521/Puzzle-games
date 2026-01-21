import java.util.*;
import javax.swing.*;
import java.util.List;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class SudokuGenerator
{
    private final int[][] grid;  //数独网格
    private final int size; // 数独大小(4, 6, 9)
    private final int blockSize; // 子块的行数
    private final int boxHeight; // 子块的列数
    private static final int MAX_GENERATION_ATTEMPTS = 1000;
    private long generationStartTime;
    private static final long MAX_GENERATION_TIME = 5000; //5秒超时
    private int attempts = 0;

    /**
     * @author Gt
     * @param size
     * size是生成的数独的宫数
     */
    public SudokuGenerator(int size) {
        this.size = size;
        if (size == 4) {
            blockSize = boxHeight = 2;
        } else if (size == 6) {
            blockSize = 3;
            boxHeight = 2;
        } else if (size == 9) {
            blockSize = boxHeight = 3;
        } else {
            throw new IllegalArgumentException("不支持的数独大小: " + size);
        }
        grid = new int[size][size];

    }
    private boolean isSafe(int row, int col, int num) {
        /*检查行*/
        for (int x = 0; x < size; x++) {
            if (grid[row][x] == num) {
                return false;
            }
        }
        /*检查列*/
        for (int x = 0; x < size; x++) {
            if (grid[x][col] == num) {
                return false;
            }
        }

        int startRow = row - row % blockSize;
        int startCol = col - col % boxHeight;

        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < boxHeight; j++) { // 使用boxHeight作为列步长
                if (grid[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean generateCompleteSudoku(int row, int col) {
        /*初始化开始时间（如果是第一次调用）*/
        if (generationStartTime == 0) {
            generationStartTime = System.currentTimeMillis();
        }
        /*检查是否超时*/
        if (System.currentTimeMillis() - generationStartTime > MAX_GENERATION_TIME) {
            return false;
        }
        /*如果已经填完最后一个格子，返回成功*/
        if (row == size) {
            return true;
        }
        /*如果当前列已经填完，转到下一行第一列*/
        if (col == size) {
            return generateCompleteSudoku(row + 1, 0);
        }
        /*若尝试次数过多，则返回失败*/
        if (attempts >= MAX_GENERATION_ATTEMPTS) {
            return false;
        }
        /*如果当前位置已经被填充，移动到下一个位置*/
        if (grid[row][col] != 0) {
            return generateCompleteSudoku(row, col + 1);
        }
        /*生成1到size的随机排列*/
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums);
        /*尝试填入数字*/
        for (int num : nums) {
            if (isSafe(row, col, num)) {
                grid[row][col] = num;
                attempts++;  // 增加尝试计数器

                if (generateCompleteSudoku(row, col + 1)) {
                    return true;
                }
                grid[row][col] = 0; // 回溯
            }
        }
        return false;
    }

    /**
     * 生成数独方法。
     * @author Gt
     * @return 生成好的数独
     */
    public int[][] generateSudoku() {
        /*初始化网格*/
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = 0;
            }
        }
        /*生成完整的数独解*/
        generationStartTime = 0; // 重置开始时间
        attempts = 0; // 重置尝试计数器
        boolean success = generateCompleteSudoku(0, 0);
        /*如果第一次生成失败，尝试多次生成*/
        int retryCount = 0;
        while (!success && retryCount < 5 && size == 6) { // 对6x6数独特别处理
            /*重置网格*/
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    grid[i][j] = 0;
                }
            }

            /*重新尝试生成*/
            generationStartTime = 0;
            attempts = 0;
            success = generateCompleteSudoku(0, 0);
            retryCount++;
        }

        if (!success) {
            throw new RuntimeException("无法生成数独谜题。请重试或调整配置。");
        }

        // 移除固定数量的数字生成题目
        return removeNumbers();
    }
    /**
     * 返回最近生成的完整数独解答
     * @author Gt
     * @return 完整的数独解答数组
     */
    public int[][] getSolution() {
        // 创建一个新的二维数组以避免外部修改内部状态
        int[][] solution = new int[size][size];
        for (int i = 0; i < size; i++) {
            // 复制值而不是引用
            System.arraycopy(grid[i], 0, solution[i], 0, size);
        }
        return solution;
    }

    // 修改removeNumbers方法，删除难度参数
    private int[][] removeNumbers() {
        int[][] puzzle = new int[size][size];
        // 复制完整解
        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, puzzle[i], 0, size);
        }
        int removeCount;  // 根据数独大小设定固定移除数量
        /*按大作业文档例图，移除数字*/
        if (size == 4) {
            removeCount = 11;
        } else if (size == 6) {
            removeCount = 17;
        } else {
            removeCount = 58;
        }
        /*随机移除数字*/
        Random random = new Random();
        for (int i = 0; i < removeCount; i++) {
            int row, col;
            do {
                row = random.nextInt(size);
                col = random.nextInt(size);
            } while (puzzle[row][col] == 0); // 确保移除的是已填的格子
            puzzle[row][col] = 0;
        }

        return puzzle;
    }
    public static void main(String[] args) {
        JFrame x = new UI_SudokuGenerator();
        x.setSize(300, 400);
        x.setVisible(true);
        x.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}