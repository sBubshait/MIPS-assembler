
import kotlin.test.Test
import kotlin.test.assertEquals

class MiscTests {

    @Test
    fun `fails on invalid instruction`() {
        val lexer = Lexer(
            "main:\n"  +
                    "add \$t0, \$t1, \$t2\n" +
                    "invalid \$t0, \$t1, \$t2\n"
        )
        val tokens = lexer.tokenize()
        val parser = Parser(tokens)
        try {
            parser.parse()
        } catch (e: Exception) {
            assertEquals("Invalid instruction invalid", e.message)
        }
    }

    @Test
    fun `fails on invalid register`() {
        val lexer = Lexer(
            "main:\n"  +
                    "add \$t0, \$t1, \$t2\n" +
                    "add \$t0, \$hey, \$m1_X\n"
        )
        try {
            lexer.tokenize()
        } catch (e: Exception) {
            assertEquals("Invalid register name hey", e.message)
        }
    }

}