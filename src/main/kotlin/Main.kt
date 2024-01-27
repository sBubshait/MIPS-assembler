import java.io.File
fun main () {
    val file = File("src/main/resources/input.asm")
    val lexer = Lexer(file.readText())
    val tokens = lexer.tokenize()
    println(tokens)
    val parser = Parser(tokens)
    val instructions = parser.parse()

    INSTRUCTIONS.filter { it.value == INSTRUCTION_TYPE.I_TYPE }.minus(Constant_IType.keys).minus(Branch_IType.keys).minus(Memory_IType.keys).forEach {
        println(it.key)
    }
}