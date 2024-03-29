package mipsassembler

val REGISTERS: Map<String, Int> =
    mapOf(
        "zero" to 0,
        "at" to 1,
        "v0" to 2,
        "v1" to 3,
        "a0" to 4,
        "a1" to 5,
        "a2" to 6,
        "a3" to 7,
        "t0" to 8,
        "t1" to 9,
        "t2" to 10,
        "t3" to 11,
        "t4" to 12,
        "t5" to 13,
        "t6" to 14,
        "t7" to 15,
        "s0" to 16,
        "s1" to 17,
        "s2" to 18,
        "s3" to 19,
        "s4" to 20,
        "s5" to 21,
        "s6" to 22,
        "s7" to 23,
        "t8" to 24,
        "t9" to 25,
        "k0" to 26,
        "k1" to 27,
        "gp" to 28,
        "sp" to 29,
        "fp" to 30,
        "ra" to 31,
    )

val DIRECTIVES =
    listOf(
        "align",
        "ascii",
        "asciiz",
        "byte",
        "data",
        "double",
        "extern",
        "float",
        "globl",
        "half",
        "kdata",
        "ktext",
        "space",
        "text",
        "word",
    )

enum class InstructionType {
    R_TYPE,
    I_TYPE,
    J_TYPE,
}

val INSTRUCTIONS: Map<String, InstructionType> =
    mapOf(
        "add" to InstructionType.R_TYPE,
        "addu" to InstructionType.R_TYPE,
        "addi" to InstructionType.I_TYPE,
        "addiu" to InstructionType.I_TYPE,
        "and" to InstructionType.R_TYPE,
        "andi" to InstructionType.I_TYPE,
        "beq" to InstructionType.I_TYPE,
        "bne" to InstructionType.I_TYPE,
        "bgez" to InstructionType.I_TYPE,
        "bgtz" to InstructionType.I_TYPE,
        "blez" to InstructionType.I_TYPE,
        "bltz" to InstructionType.I_TYPE,
        "div" to InstructionType.R_TYPE,
        "divu" to InstructionType.R_TYPE,
        "j" to InstructionType.J_TYPE,
        "jal" to InstructionType.J_TYPE,
        "jr" to InstructionType.R_TYPE,
        "jalr" to InstructionType.R_TYPE,
        "lb" to InstructionType.I_TYPE,
        "lbu" to InstructionType.I_TYPE,
        "lh" to InstructionType.I_TYPE,
        "lhu" to InstructionType.I_TYPE,
        "lui" to InstructionType.I_TYPE,
        "lw" to InstructionType.I_TYPE,
        "mul" to InstructionType.R_TYPE,
        "mult" to InstructionType.R_TYPE,
        "multu" to InstructionType.R_TYPE,
        "mfhi" to InstructionType.R_TYPE,
        "mflo" to InstructionType.R_TYPE,
        "mthi" to InstructionType.R_TYPE,
        "mtlo" to InstructionType.R_TYPE,
        "nor" to InstructionType.R_TYPE,
        "or" to InstructionType.R_TYPE,
        "ori" to InstructionType.I_TYPE,
        "slt" to InstructionType.R_TYPE,
        "slti" to InstructionType.I_TYPE,
        "sltiu" to InstructionType.I_TYPE,
        "sltu" to InstructionType.R_TYPE,
        "sll" to InstructionType.R_TYPE,
        "srl" to InstructionType.R_TYPE,
        "sb" to InstructionType.I_TYPE,
        "sc" to InstructionType.I_TYPE,
        "sh" to InstructionType.I_TYPE,
        "sw" to InstructionType.I_TYPE,
        "sub" to InstructionType.R_TYPE,
        "subu" to InstructionType.R_TYPE,
        "xor" to InstructionType.R_TYPE,
        "xori" to InstructionType.I_TYPE,
    )

val SIMPLE_RTYPE =
    mapOf(
        "add" to 0x20,
        "addu" to 0x21,
        "sub" to 0x22,
        "subu" to 0x23,
        "and" to 0x24,
        "xor" to 0x26,
        "or" to 0x25,
        "nor" to 0x27,
        "slt" to 0x2a,
        "sltu" to 0x2b,
        "sllv" to 0x04,
        "srlv" to 0x06,
        "srav" to 0x07,
    )

val MULDIV_RTYPE =
    mapOf(
        "mult" to 0x18,
        "multu" to 0x19,
        "div" to 0x1a,
        "divu" to 0x1b,
    )
val MULDIV_RTYPE_NAMES = MULDIV_RTYPE.keys.toList()

val SHIFT_RTYPE =
    mapOf(
        "sll" to 0x00,
        "srl" to 0x02,
        "sra" to 0x03,
    )
val SHIFT_RTYPE_NAMES = SHIFT_RTYPE.keys.toList()

val OTHER_RTYPE =
    mapOf(
        "jr" to 0x08,
        "jalr" to 0x09,
        "mfhi" to 0x10,
        "mthi" to 0x11,
        "mflo" to 0x12,
        "mtlo" to 0x13,
    )
val OTHER_RTYPE_NAMES = OTHER_RTYPE.keys.toList()
val OTHER_RTYPE_POS =
    mapOf(
        "jr" to 1,
        "jalr" to 1,
        "mfhi" to 3,
        "mthi" to 1,
        "mflo" to 3,
        "mtlo" to 1,
    )

val Constant_IType =
    mapOf(
        "addi" to 0x08,
        "addiu" to 0x09,
        "andi" to 0x0c,
        "ori" to 0x0d,
        "xori" to 0x0e,
        "slti" to 0x0a,
        "sltiu" to 0x0b,
        "lui" to 0x0f,
    )

val Branch_IType =
    mapOf(
        "beq" to 0x04,
        "bne" to 0x05,
        "bgez" to 0x01,
        "bgtz" to 0x07,
        "blez" to 0x06,
        "bltz" to 0x01,
    )

val Branch_IType_Args =
    mapOf(
        "beq" to 2,
        "bne" to 2,
        "bgez" to 1,
        "bgtz" to 1,
        "blez" to 1,
        "bltz" to 1,
    )

val Memory_IType =
    mapOf(
        "lb" to 0x20,
        "lbu" to 0x24,
        "lh" to 0x21,
        "lhu" to 0x25,
        "lw" to 0x23,
        "sb" to 0x28,
        "sh" to 0x29,
        "sw" to 0x2b,
        "sc" to 0x38,
    )

val JTypeInstructions =
    mapOf(
        "j" to 0x02,
        "jal" to 0x03,
    )

val SIMPLE_RTYPE_NAMES = SIMPLE_RTYPE.keys.toList()

val INSTRUCTION_NAMES = INSTRUCTIONS.keys.toList()

interface Instruction {
    val opcode: Int

    fun toBinary(): String
}

class RTypeInstruction(
    override val opcode: Int,
    private val rs: Int,
    private val rt: Int,
    private val rd: Int,
    private val shamt: Int,
    private val funct: Int,
) : Instruction {
    override fun toBinary(): String {
        return toBinaryString(opcode, 6) +
            toBinaryString(rs, 5) +
            toBinaryString(rt, 5) +
            toBinaryString(rd, 5) +
            toBinaryString(shamt, 5) +
            toBinaryString(funct, 6)
    }

    override fun toString(): String {
        return "RTypeInstruction(opcode=$opcode, rs=$rs, rt=$rt, rd=$rd, shamt=$shamt, funct=$funct)"
    }
}

class ITypeInstruction(
    override val opcode: Int,
    private val rs: Int,
    private val rt: Int,
    private val immediate: Int,
) : Instruction {
    override fun toBinary(): String {
        return toBinaryString(opcode, 6) +
            toBinaryString(rs, 5) +
            toBinaryString(rt, 5) +
            toBinaryStringForImmediate(immediate)
    }
}

class JTypeInstruction(
    override val opcode: Int,
    private val address: Int,
) : Instruction {
    override fun toBinary(): String {
        println("JTypeInstruction(opcode=$opcode, address=$address)")
        return "%06d".format(opcode.toString(2).toInt()) +
            toBinaryString(address, 26)
    }
}

fun toBinaryString(
    value: Int,
    bits: Int,
): String {
    return String.format("%${bits}s", Integer.toBinaryString(value and ((1 shl bits) - 1))).replace(' ', '0')
}

fun toBinaryStringForImmediate(value: Int): String {
    // Get the lower 16 bits for both positive and negative values correctly.
    val lower16Bits = value and 0xFFFF
    return String.format("%16s", Integer.toBinaryString(lower16Bits)).replace(' ', '0')
}

fun assert(
    cond: Boolean,
    msg: () -> String,
) {
    if (!cond) {
        throwErr(msg())
    }
}

fun throwErr(msg: String): Nothing {
    throw Exception(msg)
}
