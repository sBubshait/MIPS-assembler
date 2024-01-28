package mipsassembler

class Parser(private val tokens: List<Token>) {
    private val instructionTokens = getInstructionTokens().toMutableList()
    private val instructions = mutableListOf<Instruction>()
    private val labelAddresses = mutableMapOf<String, Int>()
    private var currentAddress = 0x00400000 // A common starting address for MIPS programs.

    private fun peek() = instructionTokens.firstOrNull()

    private fun next() = instructionTokens.removeFirstOrNull()

    fun parse(): List<Instruction> {
        for (token in tokens) {
            when (token.type) {
                TokenType.LABEL -> labelAddresses[token.value] = currentAddress
                TokenType.IDENTIFIER -> if (token.value in INSTRUCTIONS) currentAddress += 4
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
            InstructionType.R_TYPE -> parseRTypeInstruction()
            InstructionType.I_TYPE -> parseITypeInstruction()
            InstructionType.J_TYPE -> parseJTypeInstruction()
        }
    }

    private fun parseRTypeInstruction(): Instruction {
        return when (peek()!!.value) {
            in SIMPLE_RTYPE -> parseSimpleRTypeInstruction()
            in MULDIV_RTYPE -> parseMulDivRTypeInstruction()
            in SHIFT_RTYPE -> parseShiftRTypeInstruction()
            in OTHER_RTYPE -> parseOtherRTypeInstruction()
            "mul" -> parseMulRTypeInstruction()
            else -> throwErr("Invalid instruction ${peek()!!.value}")
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
        // NOTE: rd is not used (don't care) so we just pass 0.
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
        return if (pos == 1) {
            RTypeInstruction(0, rd, 0, 0, 0, OTHER_RTYPE[instructionName]!!)
        } else {
            RTypeInstruction(0, 0, 0, rd, 0, OTHER_RTYPE[instructionName]!!)
        }
    }

    private fun parseMulRTypeInstruction(): Instruction {
        next()
        val rd = getRegister()
        skipComma()
        val rs = getRegister()
        skipComma()
        val rt = getRegister()
        return RTypeInstruction(0x1c, rs, rt, rd, 0, 0x02)
    }

    private fun parseITypeInstruction(): Instruction {
        return when (peek()!!.value) {
            in Constant_IType, "lui" -> parseConstantITypeInstruction()
            in Branch_IType -> parseBranchITypeInstruction()
            in Memory_IType -> parseMemoryITypeInstruction()
            else -> throwErr("Invalid instruction ${peek()!!.value}")
        }
    }

    private fun parseConstantITypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rt = getRegister()
        skipComma()
        var rs = 0
        if (instructionName != "lui") {
            rs = getRegister()
            skipComma()
        }
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
        val offset = (address - currentAddress) / 4
        println("$address, $currentAddress, ${address - currentAddress}, $offset")
        return ITypeInstruction(Branch_IType[instructionName]!!, rs, rt, offset)
    }

    private fun parseMemoryITypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val rt = getRegister()
        skipComma()
        val (base, offset) = getMemoryAddress()
        return ITypeInstruction(Memory_IType[instructionName]!!, base, rt, offset)
    }

    private fun parseJTypeInstruction(): Instruction {
        val instructionName = next()!!.value
        val address = getLabelAddress()
        return JTypeInstruction(JTypeInstructions[instructionName]!!, address)
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
        kotlin.assert(
            peek() != null && peek()!!.type == TokenType.IDENTIFIER,
        ) { "Expected an identifier for a label, got ${peek()!!.value}" }
        val label = next()!!.value
        return labelAddresses[label] ?: throwErr("Invalid label: $label. Label must be defined before use.")
    }

    private fun getMemoryAddress(): Pair<Int, Int> {
        val offset = getInteger()
        kotlin.assert(peek() != null && peek()!!.type == TokenType.OPEN_PAREN) { "Expected open parenthesis, got ${peek()!!.value}" }
        next()

        val base = getRegister()

        kotlin.assert(peek() != null && peek()!!.type == TokenType.CLOSE_PAREN) { "Expected close parenthesis, got ${peek()!!.value}" }
        next()

        return Pair(base, offset)
    }

    private fun skipComma() {
        kotlin.assert(peek() != null && next()!!.type == TokenType.COMMA) { "Expected comma, got ${peek()!!.value}" }
    }

    private fun getInstructionTokens(): List<Token> {
        val tokensFromMain = tokens.dropWhile { it.type != TokenType.LABEL || it.value != "main" }
        return tokensFromMain.drop(1).takeWhile { it.type != TokenType.LABEL }
    }
}
