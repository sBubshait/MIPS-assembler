
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
}