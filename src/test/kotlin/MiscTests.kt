package mipsassembler

import kotlin.test.Test
import kotlin.test.assertEquals

class MiscTests {
    @Test
    fun `fails on invalid instruction`() {
        val lexer =
            Lexer(
                "main:\n" +
                    "add \$t0, \$t1, \$t2\n" +
                    "invalid \$t0, \$t1, \$t2\n",
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
        val lexer =
            Lexer(
                "main:\n" +
                    "add \$t0, \$t1, \$t2\n" +
                    "add \$t0, \$hey, \$m1_X\n",
            )
        try {
            lexer.tokenize()
        } catch (e: Exception) {
            assertEquals("Invalid register name hey", e.message)
        }
    }

    @Test
    fun `Lexer can handle numbers in negative, binary, hexadecimal, and decimal`() {
        val lexer =
            Lexer(
                "main:\n" +
                    "addi \$t0, \$t1, -7\n" +
                    "addi \$t0, \$t1, 7\n" +
                    "addi \$t0, \$t1, 0xa\n" +
                    "addi \$t0, \$t1, 0b10\n" +
                    "addi \$t0, \$t1, 0b1110\n",
            )
        val tokens = lexer.tokenize()
        val nums = tokens.filter { it.type == TokenType.NUMBER }
        assertEquals(5, nums.size)
        assertEquals("-7", nums[0].value)
        assertEquals("7", nums[1].value)
        assertEquals("10", nums[2].value)
        assertEquals("2", nums[3].value)
        assertEquals("14", nums[4].value)
    }
}
