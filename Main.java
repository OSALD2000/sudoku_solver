import java.util.Random;


public class Main 
{
    public static int[][] finalBoard;

    public static int[][] createBoard(int N)
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

    public static boolean isSave(int[][] board, int size,  int row, int col, int num)
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


    public static boolean solve(int[][] board, int y, int x)
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
                    finalBoard = board;
                    return true;
                }else
                {
                    board[y][x] = 0;
                };        
            }
        }

        return false;
    }

    public static void main(String[] args) 
    {

        //var board = createBoard(17);
        
        var board = new int[][]
        {
            new int[]{6, 0, 0, 0, 0, 0, 0, 0, 0},
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

        long start_time = System.currentTimeMillis();
        boolean solved = solve(board, 0, 0);
        long end_time = System.currentTimeMillis();

        if(solved)
        {
            for (int i = 0; i < 9 ; i++)
            {
               for (int j = 0; j < 9 ; j++)
               {
                    System.out.print("| "+finalBoard[i][j]+" |\t");
               }
               System.out.println("\n");
            }   
        }
        else
        {
            System.out.println("NO NO NO NO NO :(");
        } 

        System.out.println("\n");

        System.out.println(" Time needed is : " + ((end_time - start_time)));
    }
    
}






