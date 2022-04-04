package chess

import kotlin.math.abs


fun main() {
    println("Pawns-Only Chess")
    val board = mutableListOf(
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf('W', 'W', 'W', 'W', 'W', 'W', 'W', 'W'),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf('B', 'B', 'B', 'B', 'B', 'B', 'B', 'B'),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
    )
    val passant = Array(2){ -1 }
    println("First PLayer's name:")
    val firstPlayer = readln()
    println("Second PLayer's name:")
    val secondPlayer = readln()
    var currentPlayer = firstPlayer
    var currentColor = 'W'
    while (true) {
        showBoard(board)
        val move = getMove(board, currentPlayer, currentColor, passant)
        if (move[0] == -1) {
            break
        } else {
            makeMove(board, move, passant)
            if (hasBlackWon(board)) {
                showBoard(board)
                println("Black Wins!")
                break
            } else if (hasWhiteWon(board)) {
                showBoard(board)
                println("White Wins!")
                break
            } else if (isStalemate(board)) {
                showBoard(board)
                println("Stalemate!")
                break
            }
            currentPlayer = if (currentPlayer == firstPlayer) secondPlayer else firstPlayer
            currentColor = if (currentColor == 'W') 'B' else 'W'
        }
    }
    println("Bye!")
}

fun showBoard(board: MutableList<MutableList<Char>>) {
    for (i in board.size-1 downTo 0) {
        printStroke()
        printCells(i + 1, board[i].joinToString(""))
    }
    printStroke()
    printLetters()
}

fun printStroke() {
    print("  ")
    for (i in 1..8) {
        print("+---")
    }
    print("+\n")
}

fun printCells(line: Int, figures: CharSequence) {
    print("$line ")
    for (f in figures) {
        print("| $f ")
    }
    print("|\n")
}

fun printLetters() {
    print(" ")
    for (l in 'a'..'h') {
        print("   $l")
    }
    print("\n")
}

fun getMove(board: MutableList<MutableList<Char>>, player: String, color: Char, passant: Array<Int>): Array<Int> {
    val move = Array(4) { -1 }
    while (true) {
        println("$player's turn:")
        val input = readln()
        if (input == "exit") {
            move[0] = -1
            break
        }
        if (isValidInput(input)) {
            val letters = "abcdefgh"
            move[0] = letters.indexOf(input[0])         // fromX
            move[1] = input[1].toString().toInt() - 1   // fromY
            move[2] = letters.indexOf(input[2])         // toX
            move[3] = input[3].toString().toInt() - 1   // toY

            if (isCorrectStartPosition(board, move, color)) {
                if (isValidMove(board, move, color, passant)) {
                    break
                } else {
                    println("Invalid Input")
                    continue
                }
            } else {
                println("No ${if (color == 'W') "white" else "black"} pawn at ${input.take(2)}")
                continue
            }
        } else {
            println("Invalid Input")
            continue
        }
    }
    return move
}

fun isValidInput(move: String): Boolean {
    val regex = Regex("[a-h][1-8][a-h][1-8]")
    return move.matches(regex)
}

fun isCorrectStartPosition(board: MutableList<MutableList<Char>>, move: Array<Int>, color: Char): Boolean {
    return (board[move[1]][move[0]] == color)
}

fun isValidMove(board: MutableList<MutableList<Char>>, move: Array<Int>, color: Char, passant: Array<Int>): Boolean {
    if (board[move[3]][move[2]] == ' ' && move[0] == move[2]) {
        return if (color == 'W') {
            if (move[1] == 1) {
                (move[3] == move[1] + 2 && board[move[3] - 1][move[2]] == ' ') || move[3] == move[1] + 1
            } else {
                move[3] == move[1] + 1
            }
        } else {
            if (move[1] == 6) {
                (move[3] == move[1] - 2 && board[move[3] + 1][move[2]] == ' ') || move[3] == move[1] - 1
            } else {
                move[3] == move[1] - 1
            }
        }
    } else if (move[2] == move[0] + 1 || move[2] == move[0] - 1) {
        return if (color == 'W' && move[3] == move[1] + 1) {
            if (board[move[3]][move[2]] == 'B') {
                true
            } else board[move[3]][move[2]] == ' ' && passant[0] == move[2] && passant[1] == move[3] - 1
        } else if (move[3] == move[1] - 1) {
            if (board[move[3]][move[2]] == 'W') {
                true
            } else board[move[3]][move[2]] == ' ' && passant[0] == move[2] && passant[1] == move[3] + 1
        } else {
            false
        }
    } else {
        return false
    }
}

fun makeMove(board: MutableList<MutableList<Char>>, move: Array<Int>, passant: Array<Int>) {
    board[move[3]][move[2]] = board[move[1]][move[0]]
    board[move[1]][move[0]] = ' '
    if (passant[1] == move[1] && passant[0] == move[2]) board[passant[1]][passant[0]] = ' '
    if (abs(move[3] - move[1]) == 2) {
        passant[0] = move[2]
        passant[1] = move[3]
    } else {
        passant[0] = -1
        passant[1] = -1
    }
}

fun hasBlackWon(board: MutableList<MutableList<Char>>): Boolean {
    if (countPawns('W', board) == 0) return true
    if ('B' in board[0].map { it }) return true
    return false
}

fun hasWhiteWon(board: MutableList<MutableList<Char>>): Boolean {
    if (countPawns('B', board) == 0) return true
    if ('W' in board[board.lastIndex].map { it }) return true
    return false
}

fun isStalemate(board: MutableList<MutableList<Char>>) = isBlackStuck(board) || isWhiteStuck(board)

fun isBlackStuck(board: MutableList<MutableList<Char>>): Boolean {
    for (row in board.indices) {
        for (col in board[row].indices) {
            if (board[row][col] == 'B') {
                if (board[row - 1][col] == 'W') {
                    if (col == 0) {
                        if (board[row - 1][1] != 'W') {
                            continue
                        }
                    } else if (col == 7) {
                        if (board[row - 1][6] != 'W') {
                            continue
                        }
                    } else if (board[row - 1][col + 1] != 'W') {
                        if (board[row - 1][col - 1] != 'W') {
                            continue
                        }
                    }
                }
                return false
            }
        }
    }
    return true
}

fun isWhiteStuck(board: MutableList<MutableList<Char>>): Boolean {
    for (row in board.indices) {
        for (col in board[row].indices) {
            if (board[row][col] == 'W') {
                if (board[row + 1][col] == 'B') {
                    if (col == 0) {
                        if (board[row + 1][1] != 'B') {
                            continue
                        }
                    } else if (col == 7) {
                        if (board[row + 1][6] != 'B') {
                            continue
                        }
                    } else if (board[row + 1][col + 1] != 'B') {
                        if (board[row + 1][col - 1] != 'B') {
                            continue
                        }
                    }
                }
                return false
            }
        }
    }
    return true
}

fun countPawns(pawn: Char, board: MutableList<MutableList<Char>>): Int {
    var count = 0
    for (row in board) {
        for (cell in row) {
            if (cell == pawn) count++
        }
    }
    return count
}