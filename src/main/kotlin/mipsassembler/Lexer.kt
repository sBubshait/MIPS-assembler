package mipsassembler

enum class TokenType {
    REGISTER,
    NUMBER,
    LABEL,
    IDENTIFIER,
    DIRECTIVE,
    COMMA,
    OPEN_PAREN,
    CLOSE_PAREN,
}

const val PUNCTUATION = ".,:()"

data class Token(val type: TokenType, val value: String = "")

class Lexer(private val input: String) {
    private var pos = 0

    fun nextChar(): Char? {
        if (pos >= input.length) {
            return null
        }
        return input[pos++]
    }

    fun peekChar(): Char? {
        if (pos >= input.length) {
            return null
        }
        return input[pos]
    }

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (pos < input.length) {
            val c = peekChar() ?: break
            when (c) {
                ' ', '\t', '\n', '\r' -> {
                    nextChar()
                    continue
                }
                '$' -> tokens.add(readRegister())
                in '0'..'9', '-' -> tokens.add(readNumber())
                in 'a'..'z', in 'A'..'Z' -> tokens.add(readIdentifier())
                ',' -> tokens.add(Token(TokenType.COMMA, nextChar().toString()))
                '#' -> readComment()
                '.' -> tokens.add(readDirective())
                '(' -> tokens.add(Token(TokenType.OPEN_PAREN, nextChar().toString()))
                ')' -> tokens.add(Token(TokenType.CLOSE_PAREN, nextChar().toString()))
                else -> throwErr("Invalid character $c")
            }
        }
        return tokens
    }

    private fun readNumber(): Token {
        val number = StringBuilder()
        val fst = peekChar()
        var base = 10
        if (fst == '-') {
            number.append(nextChar())
        }
        while (true) {
            var next = peekChar() ?: break
            if (next.isWhitespace() || next in PUNCTUATION) {
                break
            }
            if ((fst == '-' && number.length == 1 || number.isEmpty()) && next == '0') {
                number.append(nextChar())
                val nextNext = peekChar()
                when (nextNext) {
                    'x', 'X' -> {
                        base = 16
                    }
                    'b', 'B' -> {
                        base = 2
                    }
                    in '0'..'9' -> {
                        base = 10
                        number.append(nextNext)
                    }
                    ' ', '\t', '\n', '\r', null, in PUNCTUATION -> {
                        return Token(TokenType.NUMBER, "0")
                    }
                    else -> throwErr("Invalid number starting with $number")
                }
                nextChar()
            }
            next = peekChar() ?: break
            assert(
                (base == 10 && next.isDigit()) ||
                    (base == 16 && (next in '0'..'9' || next in 'a'..'f' || next in 'A'..'F')) ||
                    (base == 2 && next in '0'..'1'),
            ) {
                "Invalid character in integer starting with $next"
            }
            number.append(next)
            nextChar()
        }

        val n = number.toString().toIntOrNull(base) ?: throwErr("Invalid number $number")
        return Token(TokenType.NUMBER, n.toString())
    }

    private fun readWord(): String {
        val word = StringBuilder()
        while (true) {
            val next = peekChar()
            if (next == null || next.isWhitespace() || next in PUNCTUATION) {
                break
            }
            assert(next.isLetterOrDigit()) { "Invalid character $next" }
            word.append(next.lowercase())
            nextChar()
        }
        return word.toString()
    }

    private fun readComment() {
        while (peekChar() != null && peekChar() != '\n') {
            nextChar()
        }
    }

    private fun readRegister(): Token {
        nextChar()
        val name = readWord()
        if (name !in REGISTERS) {
            throwErr("Invalid register name $name")
        }
        return Token(TokenType.REGISTER, name)
    }

    private fun readDirective(): Token {
        nextChar()
        val name = readWord()
        if (name !in DIRECTIVES) {
            throwErr("Invalid directive $name")
        }
        return Token(TokenType.DIRECTIVE, name)
    }

    private fun readIdentifier(): Token {
        val word = readWord()
        if (peekChar() == ':') {
            nextChar()
            return Token(TokenType.LABEL, word)
        }
        return Token(TokenType.IDENTIFIER, word)
    }
}
