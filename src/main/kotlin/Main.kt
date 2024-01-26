import java.io.File
fun main () {
    val file = File("src/main/resources/input.asm")
    val lexer = Lexer(file.readText())
    val tokens = lexer.tokenize()
    println(tokens)
    val parser = Parser(tokens)
    val instructions = parser.parse()
    println(instructions)
    println("%05d".format(16.toString(2).toInt()))
}