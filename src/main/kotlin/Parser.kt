
class Parser(private val tokens: List<Token>) {

    private val instructionTokens = getInstructionTokens().toMutableList()
    private val instructions = mutableListOf<Instruction>()
    private val labels
        get() = tokens.filter { it.type == TokenType.LABEL }.map { it.value }
    private val labelAddresses = mutableMapOf<String, Int>()
    private var currentAddress = 0x00400000 // A common starting address for MIPS programs.

    private fun peek() = instructionTokens.firstOrNull()
    private fun next() = instructionTokens.removeFirstOrNull()

    fun parse(): List<Instruction> {
        val textSection = tokens.dropWhile { it.type != TokenType.DIRECTIVE || it.value != "text" }.drop(1)
        for (token in textSection) {
            when (token.type) {
                TokenType.LABEL -> labelAddresses[token.value] = currentAddress
                TokenType.IDENTIFIER -> if (token.value in INSTRUCTION_NAMES) currentAddress += 4
                else -> {}
            }
        }

        currentAddress = 0x00400000
        instructions.clear()
        while (instructionTokens.isNotEmpty()) {
            val instruction = parseInstruction()
            instructions.add(instruction)
            currentAddress += 4
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
            "mul" -> parseMulRTypeInstruction()
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

    private fun parseMulRTypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rd = getRegister()
        skipComma()
        val rs = getRegister()
        skipComma()
        val rt = getRegister()
        return RTypeInstruction(0x1c, rs, rt, rd, 0, 0x02)
    }

    private fun parseITypeInstruction(): Instruction {
        val instructionName = peek()!!.value
        return when (instructionName) {
            in Constant_IType -> parseConstantITypeInstruction()
            in Branch_IType -> parseBranchITypeInstruction()
            else -> throwErr("Invalid instruction $instructionName")
        }
    }

    private fun parseConstantITypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rt = getRegister()
        skipComma()
        val rs = getRegister()
        skipComma()
        val immediate = getInteger()
        return ITypeInstruction(Constant_IType[instructionName]!!, rs, rt, immediate)
    }

    private fun parseBranchITypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rs = getRegister()
        skipComma()
        var rt = if (instructionName == "bgez") 0x01 else 0x00
        if (Branch_IType_Args[instructionName] == 2) {
            rt = getRegister()
            skipComma()
        }
        val address = getLabelAddress()
        val offset_imm = (address - currentAddress) / 4
        println("$address, $currentAddress, ${address - currentAddress}, $offset_imm")
        return ITypeInstruction(Branch_IType[instructionName]!!, rs, rt, offset_imm)
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

    private fun getLabelAddress(): Int {
        kotlin.assert(peek() != null && peek()!!.type == TokenType.IDENTIFIER) { "Expected an identifier for a label, got ${peek()!!.value}" }
        val label = next()!!.value
        return labelAddresses[label] ?: throwErr("Invalid label: $label. Label must be defined before use.")
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