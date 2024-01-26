
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

        return when (instructionType) {
            INSTRUCTION_TYPE.R_TYPE -> parseRTypeInstruction()
            INSTRUCTION_TYPE.I_TYPE -> parseITypeInstruction()
            INSTRUCTION_TYPE.J_TYPE -> parseJTypeInstruction()
        }
    }

    private fun parseRTypeInstruction(): Instruction {
        val instructionName = peek()!!.value
        return when (instructionName) {
            in SIMPLE_RTYPE_NAMES -> parseSimpleRTypeInstruction()
            in MULDIV_RTYPE_NAMES  -> parseMulDivRTypeInstruction()
            in SHIFT_RTYPE_NAMES ->  parseShiftRTypeInstruction()
            in OTHER_RTYPE_NAMES -> parseOtherRTypeInstruction()
            else -> throwErr("Invalid instruction $instructionName")
        }
    }

    private fun parseSimpleRTypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rd = getRegister()
        skipComma()
        val rs = getRegister()
        skipComma()
        val rt = getRegister()
        return RTypeInstruction(0, rs, rt, rd, 0, SIMPLE_RTYPE[instructionName]!!)
    }

    private fun parseMulDivRTypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rs = getRegister()
        skipComma()
        val rt = getRegister()
        // NOTE: rd is not used (dont care) so we just pass 0.
        return RTypeInstruction(0, rs, rt, 0, 0, MULDIV_RTYPE[instructionName]!!)
    }

    private fun parseShiftRTypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rd = getRegister()
        skipComma()
        val rt = getRegister()
        skipComma()
        val shamt = getInteger()
        // NOTE: Similarly, rs is arbitrarily chosen to be 0.
        return RTypeInstruction(0, 0, rt, rd, shamt, SHIFT_RTYPE[instructionName]!!)
    }

    private fun parseOtherRTypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rd = getRegister()

        val pos = OTHER_RTYPE_POS[instructionName]!!
        // Yet, again, we pass 0 for all unused registers.
        return if (pos == 1) RTypeInstruction(0, rd, 0,0,0, OTHER_RTYPE[instructionName]!!)
                    else RTypeInstruction(0, 0, 0, rd, 0, OTHER_RTYPE[instructionName]!!)
    }

    private fun parseITypeInstruction(): Instruction {
        return RTypeInstruction(0, 0, 0, 0, 0, 0)
    }

    private fun parseJTypeInstruction(): Instruction {
        return RTypeInstruction(0, 0, 0, 0, 0, 0)
    }

    private fun getRegister(): Int {
        kotlin.assert(peek() != null && peek()!!.type == TokenType.REGISTER) { "Expected register, got ${peek()!!.value}" }
        val name = next()!!.value
        return REGISTERS[name] ?: throwErr("Invalid register name: $name")
    }

    private fun getInteger(): Int {
        kotlin.assert(peek() != null && peek()!!.type == TokenType.NUMBER) { "Expected integer, got ${peek()!!.value}" }
        val value = next()!!.value
        return value.toIntOrNull() ?: throwErr("Invalid integer: $value")
    }

    private fun skipComma() {
        kotlin.assert(peek() != null && next()!!.type == TokenType.COMMA) { "Expected comma, got ${peek()!!.value}" }
    }

    private fun getInstructionTokens(): List<Token> {
        val tokensFromMain = tokens.dropWhile { it.type != TokenType.LABEL || it.value != "main" }
        val instructionTokens = tokensFromMain.drop(1).takeWhile { it.type != TokenType.LABEL }

        return instructionTokens
    }
}