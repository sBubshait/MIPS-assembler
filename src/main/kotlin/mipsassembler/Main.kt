package mipsassembler

import java.io.File

fun main() {
    val file = File("src/main/resources/input.asm")
    val lexer = Lexer(file.readText())
    val tokens = lexer.tokenize()
    val parser = Parser(tokens)
    val instructions = parser.parse()
    val binary = instructions.joinToString("\n") { it.toBinary() }
    File("src/main/resources/output.txt").writeText(binary)
}
