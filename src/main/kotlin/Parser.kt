
class Parser(private val tokens: List<Token>) {

    private val instructionTokens = getInstructionTokens().toMutableList()


    private fun peek() = instructionTokens.firstOrNull()
    private fun next() = instructionTokens.removeFirstOrNull()

    fun parse(): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        while (instructionTokens.isNotEmpty()) {
            val instruction = parseInstruction()
            instructions.add(instruction)
        }

        return instructions
    }

    private fun parseInstruction(): Instruction {
        val instructionToken = peek()!!
        if (instructionToken.type != TokenType.IDENTIFIER) {
            throwErr("Expected instruction, got ${instructionToken.value}")
        }
        if (instructionToken.value !in INSTRUCTION_NAMES) {
            throwErr("Invalid instruction ${instructionToken.value}")
        }
        val instructionName = instructionToken.value
        val instructionType = INSTRUCTIONS[instructionName] ?: throwErr("Invalid instruction $instructionName")

        when (instructionType) {
            INSTRUCTION_TYPE.R_TYPE -> return parseRTypeInstruction()
            INSTRUCTION_TYPE.I_TYPE -> return parseITypeInstruction()
            INSTRUCTION_TYPE.J_TYPE -> return parseJTypeInstruction()
        }
    }

    private fun parseRTypeInstruction(): Instruction {
        val instructionName = next()!!.value
        if (instructionName in SIMPLE_RTYPE_NAMES) {
            assert(peek() != null && peek()!!.type == TokenType.REGISTER) { "Expected register, got ${peek()!!.value}" }
            val rd = REGISTERS[next()!!.value] ?: throwErr("Invalid register name: ${next()!!.value}")
            assert(peek() != null && next()!!.type == TokenType.COMMA) { "Expected comma, got ${peek()!!.value}" }
            assert(peek() != null && peek()!!.type == TokenType.REGISTER) { "Expected register, got ${peek()!!.value}" }
            val rs = REGISTERS[next()!!.value] ?: throwErr("Invalid register name: ${next()!!.value}")
            assert(peek() != null && next()!!.type == TokenType.COMMA) { "Expected comma, got ${peek()!!.value}" }
            assert(peek() != null && peek()!!.type == TokenType.REGISTER) { "Expected register, got ${peek()!!.value}" }
            val rt = REGISTERS[next()!!.value] ?: throwErr("Invalid register name: ${next()!!.value}")
            return RTypeInstruction(0, rs, rt, rd, 0, SIMPLE_RTYPE[instructionName]!!)
        }
        return RTypeInstruction(0, 0, 0, 0, 0, 0)
    }

    private fun parseITypeInstruction(): Instruction {
        return RTypeInstruction(0, 0, 0, 0, 0, 0)
    }

    private fun parseJTypeInstruction(): Instruction {
        return RTypeInstruction(0, 0, 0, 0, 0, 0)
    }

    private fun getInstructionTokens(): List<Token> {
        val tokensFromMain = tokens.dropWhile { it.type != TokenType.LABEL || it.value != "main" }
        val instructionTokens = tokensFromMain.drop(1).takeWhile { it.type != TokenType.LABEL }

        return instructionTokens
    }
}