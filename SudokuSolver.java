import java.util.Arrays;
import java.util.Random;

class SudokuWorker extends Thread
{
    private int[][] board;
    public int startValue;
    public boolean solved = false;

    @Override
    public void run() 
    {
        this.solved = solve(board, 0, 0);

        if (this.solved)
        {
            System.out.println("Solved by thread with start value: "+this.startValue);
        }else
        {
            System.out.println("Not solved by thread with start value: "+this.startValue);
        }
    }

    public SudokuWorker(int[][] board, int startValue)
    {
        this.board = board;
        this.startValue = startValue;
    }

    public SudokuWorker(int startValue)
    {
        this.board = createBoard(10);
        this.startValue = startValue;
    }

    public int[][] createBoard(int N)
    {
        int[][] board = new int[9][9];
        
        Random r = new Random();

        for (int i = 0; i < N;)
        {
            int num = r.nextInt(9) + 1;
            int x = r.nextInt(9);
            int y = r.nextInt(9);
            
            if (isSave(board, 9, y, x, num))
            {
                board[y][x] = num;
                i++;
            }
        }

        return board;

    }

    public boolean isSave(int[][] board, int size,  int row, int col, int num)
    {
        for (int i = 0; i < size ; i++)
        {
            if(board[i][col] == num)
            {
                return false;
            }

            if(board[row][i] == num)
            {
                return false;
            }
        } 


        int boxY = row - row % 3;
        int boxX = col - col % 3;
        
        
        for (int y = boxY; y < boxY+3; y++)
        {
            for (int x = boxX; x < boxX+3; x++)
            {
                if(board[y][x] == num)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean solve(int[][] board, int y, int x)
    {
        if (y >= 9)
        {
            return true;
        }

        if(x >= 9)
        {
            return solve(board, y+1, 0);
        }

        if(board[y][x] != 0)
        {
            return solve(board, y, x+1);
        }

        for (int i = 1; i <= 9; i++)
        {
            if(isSave(board, 9, y, x, i))
            {
                board[y][x] = i;
                if(solve(board, y, x+1))
                {
                    return true;
                }else
                {
                    board[y][x] = 0;
                };        
            }
        }

        return false;
    }    



    void printBoard()
    {
        System.out.println("\n \n");

        for (int i = 0; i < 9 ; i++)
        {
           for (int j = 0; j < 9 ; j++)
           {
                System.out.print("| "+this.board[i][j]+" |\t");
           }
           System.out.println("\n");
        }   


        System.out.println("\n \n");

    }
}


public class SudokuSolver
{
    public static void main(String[] args)
    {
        var workers = new SudokuWorker[9];
        var board = new int[][]
        {
            new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0},
            new int[]{0, 0, 0, 4, 0, 0, 0, 2, 0},
            new int[]{0, 0, 0, 0, 0, 0, 8, 0, 0},
            new int[]{4, 0, 0, 0, 6, 9, 0, 0, 0},
            new int[]{0, 3, 0, 0, 0, 5, 1, 0, 0},
            new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0},
            new int[]{7, 0, 0, 0, 0, 0, 0, 0, 0},
            new int[]{0, 8, 0, 1, 0, 0, 0, 0, 0},
            new int[]{0, 0, 0, 0, 0, 0, 0, 3, 1},
        };

        for (int i = 0; i < 9 ; i++)
        {
           for (int j = 0; j < 9 ; j++)
           {
                System.out.print("| "+board[i][j]+" |\t");
           }
           System.out.println("\n");
        }

        System.out.println("\n \n \n \n \n");

        for (int i = 1; i <= 9; i++)
        {
            int [][] newBoard = new int[9][9];
            for (int j = 0; j < 9 ; j++)
            {
                newBoard[j] = Arrays.copyOf(board[j], 9);
            }
            workers[i-1] = new SudokuWorker(newBoard, i);
        }

        Arrays.stream(workers).forEach(Thread::start);


        Arrays.stream(workers).forEach(w -> 
        {
            try
            {
                w.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });

        boolean solved = Arrays.stream(workers).anyMatch(w -> w.solved);

        if (!solved)
        {
            System.out.println("NO NO NO NO NO :(");
        }else
        {
            Arrays.stream(workers).forEach(w ->{
                    if (w.solved) {
                        System.out.println("\n \n \n ");
                        System.out.println("Solved by thread with start value: "+ w.startValue);
                        w.printBoard();  
                        System.out.println("\n \n \n ");
                    }
            });

        }
    }
}

