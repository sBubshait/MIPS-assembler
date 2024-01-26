
import kotlin.test.Test
import kotlin.test.assertEquals

class RTypeTests {
    @Test
    fun `can parse simple R-type instructions`() {
        val lexer = Lexer(
            "main:\n" +
                    "add \$t0, \$t1, \$t2\n" +
                    "addu \$t0, \$t1, \$t2\n" +
                    "and \$t0, \$t1, \$t2\n" +
                    "nor \$t0, \$t1, \$t2\n" +
                    "or \$t0, \$t1, \$t2\n" +
                    "slt \$t0, \$t1, \$t2\n"
        )
        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("00000001001010100100000000100000", instructions[0].toBinary())
        assertEquals("00000001001010100100000000100001", instructions[1].toBinary())
        assertEquals("00000001001010100100000000100100", instructions[2].toBinary())
        assertEquals("00000001001010100100000000100111", instructions[3].toBinary())
        assertEquals("00000001001010100100000000100101", instructions[4].toBinary())
        assertEquals("00000001001010100100000000101010", instructions[5].toBinary())
    }

    @Test
    fun `can parse mult-div R-type instructions`() {
        val lexer = Lexer(
            "main:\n" +
                    "div \$s0, \$s1\n" +
                    "mult \$s0, \$s1\n"
        )

        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("00000010000100010000000000011010", instructions[0].toBinary())
        assertEquals("00000010000100010000000000011000", instructions[1].toBinary())
    }

    @Test
    fun `can parse shift R-type instructions`() {
        val lexer = Lexer(
            "main:\n" +
                    "sll \$t0, \$t1, 2\n"
        )

        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("00000000000010010100000010000000", instructions[0].toBinary())
    }

    @Test
    fun `can parse other R-type instructions`() {
        val lexer = Lexer(
            "main:\n" +
                    "jr \$s0\n" +
                    "mfhi \$s2\n"
        )

        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("00000010000000000000000000001000", instructions[0].toBinary())
        assertEquals("00000000000000001001000000010000", instructions[1].toBinary())
    }
}