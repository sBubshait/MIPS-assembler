
import MIPSAssembler.Lexer
import MIPSAssembler.Parser
import kotlin.test.Test
import kotlin.test.assertEquals

class JTypeTests {
    @Test
    fun `can parse simple J-type instructions`() {
        val lexer = Lexer(
            "test:\n" +
                    "add \$s0, \$s1, \$s2\n" +
                    "main:\n" +
                    "j test"
        )
        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        val instructions = parser.parse()
        assertEquals("00001000010000000000000000000000", instructions[0].toBinary())

    }
}