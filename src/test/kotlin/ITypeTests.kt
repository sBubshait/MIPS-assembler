
import kotlin.test.Test
import kotlin.test.assertEquals

class ITypeTests {
    @Test
    fun `can parse simple constant I-type instructions`() {
        val lexer = Lexer(
            "main:\n" +
                    "addi \$s0, \$s1, 2\n"
        )
        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("00100010001100000000000000000010", instructions[0].toBinary())
    }

    @Test
    fun `can parse branch I-type instructions`() {
        val lexer = Lexer(
                ".text\n" +
            "main:\n" +
                    "beq \$s0, \$s1, test\n" +
                    "addi \$s0, \$s1, 2\n" +
                    "test:\n" +
                    "add \$t0, \$t1, \$t2\n"
        )
        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("00010010000100010000000000000010", instructions[0].toBinary())
    }

    @Test
    fun `can parse simple memory address I-type instructions, eg lb lw etc`() {
        val lexer = Lexer(
            "main:\n" +
                    "lw \$s0, 4(\$s1)\n"
        )
        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("10001110001100000000000000000100", instructions[0].toBinary())
    }

    @Test
    fun `can parse lui`() {
        val lexer = Lexer(
            "main:\n" +
                    "lui \$s0, 16\n"
        )
        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("00111100000100000000000000010000", instructions[0].toBinary())
    }
}