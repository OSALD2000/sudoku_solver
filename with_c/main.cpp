#include <iostream>
#include <string>
#include <vector>
#include <thread>
#include <mutex>
#include <chrono>

#define WORKERS 5

const std::string RED = "\033[31m";
const std::string GREEN = "\033[32m";
const std::string YELLOW = "\033[33m";
const std::string BLUE = "\033[34m";
const std::string MAGENTA = "\033[35m";
const std::string CYAN = "\033[36m";
const std::string RESET = "\033[0m";


struct Sudoku;

bool isSafe(int **, int, int, int);
void deleteBoard(int**);
bool solveSudoku(int **, int , int , Sudoku*);



struct Sudoku
{
  std::vector<int**> boards;
  std::vector<int**> solvedBoards;

  std::mutex mtxBoards;
  std::mutex mtxSolvedBoards;
  std::mutex mtxCounter;
  int counter = 0;
  int idx = 0;

  int** getBoard()
  {
    mtxBoards.lock();
    if (idx >= boards.size())
    {
      mtxBoards.unlock();
      return nullptr;
    }
    int** board = boards[idx];
    idx++;
    mtxBoards.unlock();
    return board;
  }

  void incrementCounter()
  {
    mtxCounter.lock();
    counter++;
    mtxCounter.unlock();
  }

  void cleanUp()
  {
    for (int** b : boards) {
          deleteBoard(b);
    }
  }

  void cleanUpSolved()
  {
    for (int** b : solvedBoards) {
          deleteBoard(b);
    }
  }

  void addSolvedBoard(int **board)
  {
    mtxSolvedBoards.lock();
    solvedBoards.push_back(board);
    mtxSolvedBoards.unlock();
  }
};


class SudokuWorker
{
  public:
    SudokuWorker(Sudoku *sudoku)
    {
      m_sudoku = sudoku;
    }

    void operator()()
    {
      std::cout << "new worker Thread started" << std::endl;
      int **board;
      while ((board = m_sudoku->getBoard()) != nullptr)
      {
        solveSudoku(board, 0, 0, m_sudoku);
      }
    }

  private:
    Sudoku *m_sudoku;
};


int** createNewBoard(int **board)
{
  int **newBoard = new int *[9];
  for (int i = 0; i < 9; i++)
  {
    newBoard[i] = new int[9];
    for (int j = 0; j < 9; j++)
    {
      newBoard[i][j] = board[i][j];
    }
  }
  return newBoard;
}

void deleteBoard(int** board) {
    for (int i = 0; i < 9; ++i) {
        delete[] board[i];
    }
    delete[] board;
}

void createPermutation(std::vector<int **> *boards, int **board, int col)
{
    if (col == 9) {
        boards->push_back(createNewBoard(board)); 
        return;
    }

    if(board[0][col] != 0) {
        createPermutation(boards, board, col + 1);
        return;
    }

    if (board[0][col] == 0) { 
            for (int k = 1; k <= 9; k++) {
                if (isSafe(board, 0, col, k)) {
                    board[0][col] = k;
                    createPermutation(boards, board, col + 1);
                    board[0][col] = 0;
                }
            }
    }
}

bool isSafe(int **first, int row, int col, int num)
{
  int boxStartRow = row - (row % 3);
  int boxStartCol = col - (col % 3);

  for (int i = 0; i < 9; i++)
  {
    if (first[row][i] == num || first[i][col] == num)
    {
      return false;
    }
  }

  for (int i = 0; i < 3; i++)
  {
    for (int j = 0; j < 3; j++)
    {
      if (first[boxStartRow + i][boxStartCol + j] == num)
      {
        return false;
      }
    }
  }

  return true;
}

bool solveSudoku(int **first, int row, int col, Sudoku *sudoku)
{
  if (row == 9)
  {
    return true;
  }

  if (col == 9)
  {
    return solveSudoku(first, row + 1, 0, sudoku);
  }

  if (first[row][col] != 0)
  {
    return solveSudoku(first, row, col + 1, sudoku);
  }

  for (int num = 1; num <= 9; num++)
  {
    if (isSafe(first, row, col, num))
    {
      first[row][col] = num;

      if (solveSudoku(first, row, col + 1, sudoku))
      {
        sudoku->addSolvedBoard(createNewBoard(first));
        sudoku->incrementCounter();
        return true;
      }

      first[row][col] = 0;
    }
  }

  return false;
}

void printBoard(int **board)
{
  for (int row = 0; row < 9; row++)
  {
    for (int col = 0; col < 9; col++)
    {
      std::cout << GREEN << " | " << CYAN << board[row][col] << GREEN << " | " << RESET;
    }
    std::cout << std::endl;
    std::cout << std::endl;
  }
}




int main()
{
  Sudoku sudoku_1;
  Sudoku sudoku_2;

  int **init_board = new int *[9];

  init_board[0] = new int[9]{   2, 0, 0,    0, 0, 0,    0, 0, 0  };
  init_board[1] = new int[9]{   1, 0, 7,    4, 0, 0,    0, 0, 3  };
  init_board[2] = new int[9]{   3, 0, 0,    2, 0, 0,    0, 5, 0  };

  init_board[3] = new int[9]{   0, 0, 5,    0, 2, 0,    0, 0, 0  };
  init_board[4] = new int[9]{   0, 6, 0,    1, 0, 0,    4, 0, 0  };
  init_board[5] = new int[9]{   0, 0, 0,    0, 0, 7,    0, 0, 0  };

  init_board[6] = new int[9]{   0, 0, 0,    0, 0, 3,    0, 0, 8  };
  init_board[7] = new int[9]{   8, 0, 0,    6, 0, 0,    2, 0, 0  };
  init_board[8] = new int[9]{   0, 4, 0,    8, 0, 0,    0, 3, 1  };



  createPermutation(&sudoku_1.boards, init_board, 0);
  createPermutation(&sudoku_2.boards, init_board, 0);


  std::cout << "Number of permutations 1 : " << sudoku_1.boards.size() << std::endl;
  std::cout << "Number of permutations 2 : " << sudoku_2.boards.size() << std::endl;

  auto start_seq = std::chrono::high_resolution_clock::now();

  for (int **b : sudoku_1.boards)
  {
    solveSudoku(b, 0, 0, &sudoku_1);
  }

  auto end_seq = std::chrono::high_resolution_clock::now();

  std::chrono::duration<double> duration_seq = end_seq - start_seq;
  std::cout << "Sequential duration: " << duration_seq.count() << "s" << std::endl;
  std::cout << "Number of solutions: " << sudoku_1.counter << std::endl;


  auto start_mult = std::chrono::high_resolution_clock::now();

  std::vector<std::thread> workers;

  for(int i = 0; i < WORKERS; i++)
  {
    workers.emplace_back(std::thread { SudokuWorker(&sudoku_2) });
  }

  for (std::thread &t : workers)
  {
    t.join();
  }

  auto end_mult = std::chrono::high_resolution_clock::now();


  std::chrono::duration<double> duration_mult = end_mult - start_mult;
  std::cout << "Sequential duration: " << duration_mult.count() << "s" << std::endl;
  std::cout << "Number of solutions: " << sudoku_2.counter << std::endl;

  printBoard(sudoku_1.solvedBoards[0]);

  sudoku_1.cleanUp();
  sudoku_2.cleanUp();
  sudoku_1.cleanUpSolved();
  sudoku_2.cleanUpSolved();

  deleteBoard(init_board);

  return 0;
}
