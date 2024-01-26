
const val REGISTER_COUNT = 32
val REGISTERS: Map<String, Int> = mapOf(
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
    "ra" to 31
)
val REGISTER_NAMES = REGISTERS.keys.toList()
val DIRECTIVES = listOf(
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
    "word"
)

enum class INSTRUCTION_TYPE {
    R_TYPE,
    I_TYPE,
    J_TYPE
}

val INSTRUCTIONS: Map<String, INSTRUCTION_TYPE> = mapOf(
    "add" to INSTRUCTION_TYPE.R_TYPE,
    "addu" to INSTRUCTION_TYPE.R_TYPE,
    "addi" to INSTRUCTION_TYPE.I_TYPE,
    "addiu" to INSTRUCTION_TYPE.I_TYPE,
    "and" to INSTRUCTION_TYPE.R_TYPE,
    "andi" to INSTRUCTION_TYPE.I_TYPE,
    "beq" to INSTRUCTION_TYPE.I_TYPE,
    "bne" to INSTRUCTION_TYPE.I_TYPE,
    "bgez" to INSTRUCTION_TYPE.I_TYPE,
    "bgtz" to INSTRUCTION_TYPE.I_TYPE,
    "blez" to INSTRUCTION_TYPE.I_TYPE,
    "bltz" to INSTRUCTION_TYPE.I_TYPE,
    "div" to INSTRUCTION_TYPE.R_TYPE,
    "divu" to INSTRUCTION_TYPE.R_TYPE,
    "j" to INSTRUCTION_TYPE.J_TYPE,
    "jal" to INSTRUCTION_TYPE.J_TYPE,
    "jr" to INSTRUCTION_TYPE.R_TYPE,
    "lb" to INSTRUCTION_TYPE.I_TYPE,
    "lbu" to INSTRUCTION_TYPE.I_TYPE,
    "lh" to INSTRUCTION_TYPE.I_TYPE,
    "lhu" to INSTRUCTION_TYPE.I_TYPE,
    "lui" to INSTRUCTION_TYPE.I_TYPE,
    "lw" to INSTRUCTION_TYPE.I_TYPE,
    "mul" to INSTRUCTION_TYPE.R_TYPE,
    "mult" to INSTRUCTION_TYPE.R_TYPE,
    "multu" to INSTRUCTION_TYPE.R_TYPE,
    "nor" to INSTRUCTION_TYPE.R_TYPE,
    "or" to INSTRUCTION_TYPE.R_TYPE,
    "ori" to INSTRUCTION_TYPE.I_TYPE,
    "slt" to INSTRUCTION_TYPE.R_TYPE,
    "slti" to INSTRUCTION_TYPE.I_TYPE,
    "sltiu" to INSTRUCTION_TYPE.I_TYPE,
    "sltu" to INSTRUCTION_TYPE.R_TYPE,
    "sll" to INSTRUCTION_TYPE.R_TYPE,
    "srl" to INSTRUCTION_TYPE.R_TYPE,
    "sb" to INSTRUCTION_TYPE.I_TYPE,
    "sc" to INSTRUCTION_TYPE.I_TYPE,
    "sh" to INSTRUCTION_TYPE.I_TYPE,
    "sw" to INSTRUCTION_TYPE.I_TYPE,
    "sub" to INSTRUCTION_TYPE.R_TYPE,
    "subu" to INSTRUCTION_TYPE.R_TYPE,
    "xor" to INSTRUCTION_TYPE.R_TYPE,
    "xori" to INSTRUCTION_TYPE.I_TYPE
)

val SIMPLE_RTYPE = mapOf(
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

val MULDIV_RTYPE = mapOf(
    "mult" to 0x24,
    "multu" to 0x25,
    "div" to 0x1a,
    "divu" to 0x1b
)
val MULDIV_RTYPE_NAMES = MULDIV_RTYPE.keys.toList()

val SHIFT_RTYPE = mapOf(
    "sll" to 0x00,
    "srl" to 0x02,
    "sra" to 0x03
)
val SHIFT_RTYPE_NAMES = SHIFT_RTYPE.keys.toList()

val SIMPLE_RTYPE_NAMES = SIMPLE_RTYPE.keys.toList()

val INSTRUCTION_NAMES = INSTRUCTIONS.keys.toList()


interface Instruction {
    val opcode: Int

    fun toBinary(): String
}

class RTypeInstruction(
    override val opcode: Int,
    val rs: Int,
    val rt: Int,
    val rd: Int,
    val shamt: Int,
    val funct: Int
) : Instruction {
    override fun toBinary(): String {
        return  "%06d".format(opcode.toString(2).toInt()) +
                "%05d".format(rs.toString(2).toInt()) +
                "%05d".format(rt.toString(2).toInt()) +
                "%05d".format(rd.toString(2).toInt()) +
                "%05d".format(shamt.toString(2).toInt()) +
                "%06d".format(funct.toString(2).toInt())
    }

    override fun toString(): String {
        return "RTypeInstruction(opcode=$opcode, rs=$rs, rt=$rt, rd=$rd, shamt=$shamt, funct=$funct)"
    }
}

class ITypeInstruction(
    override val opcode: Int,
    val rs: Int,
    val rt: Int,
    val immediate: Int
) : Instruction {
    override fun toBinary(): String {
        return "%06d".format(opcode.toString(2).toInt()) +
                "%05d".format(rs.toString(2).toInt()) +
                "%05d".format(rt.toString(2).toInt()) +
                "%016d".format(immediate.toString(2).toInt())
    }
}

class JTypeInstruction(
    override val opcode: Int,
    val address: Int
) : Instruction {
    override fun toBinary(): String {
        return "%06d".format(opcode.toString(2).toInt()) +
                "%026d".format(address.toString(2).toInt())
    }
}


fun assert(cond: Boolean, msg: () -> String) {
    if (!cond) {
        throwErr(msg())
    }
}

fun throwErr(msg: String): Nothing {
    throw Exception(msg)
}