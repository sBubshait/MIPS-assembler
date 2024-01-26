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

class Lexer (private val input: String) {

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
            val c = peekChar()
            if (c == null) {
                break
            }
            when (c) {
                ' ', '\t', '\n', '\r' -> {nextChar(); continue}
                '$' -> {
                    nextChar()
                    val name = readWord()
                    if (name !in REGISTER_NAMES) {
                        throwErr("Invalid register name $name")
                    }
                    tokens.add(Token(TokenType.REGISTER, name))
                }
                in '0'..'9' -> tokens.add(Token(TokenType.NUMBER, readNumber()))
                in 'a'..'z', in 'A'..'Z' -> tokens.add(tokenizeIdentifier())
                ',' -> tokens.add(Token(TokenType.COMMA, nextChar().toString()))
                '#' -> readComment()
                '.' -> {
                    nextChar()
                    val name = readWord()
                    if (name !in DIRECTIVES) {
                        throwErr("Invalid directive $name")
                    }
                    tokens.add(Token(TokenType.DIRECTIVE, name))
                }
                '(' -> tokens.add(Token(TokenType.OPEN_PAREN, nextChar().toString()))
                ')' -> tokens.add(Token(TokenType.CLOSE_PAREN, nextChar().toString()))
                else -> throwErr("Invalid character $c")
            }
        }
        return tokens
    }

    private fun readNumber(): String {
        val number = StringBuilder()
        while (true) {
            val next = peekChar()
            if (next == null || next.isWhitespace() || next in PUNCTUATION) {
                break
            }
            assert(next.isDigit(), { "Invalid number character $next" })
            number.append(next)
            nextChar()
        }
        return number.toString()
    }

    private fun readWord(): String {
        val word = StringBuilder()
        while (true) {
            val next = peekChar()
            if (next == null || next.isWhitespace() || next in PUNCTUATION) {
                break
            }
            assert(next.isLetterOrDigit(), { "Invalid character $next" })
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

    private fun tokenizeIdentifier(): Token {
        val word = readWord()
        if (peekChar() == ':') {
            nextChar()
            return Token(TokenType.LABEL, word)
        }
        return Token(TokenType.IDENTIFIER, word)
    }


}

