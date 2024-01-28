# MIPS Assembler
Welcome to the MIPS Assembler! This is a simple assembler that takes in MIPS assembly code and outputs the corresponding machine code written fully in Kotlin.

## How to use
1. Clone the repository
2. Edit the input.asm file in src/main/resources directory with your own MIPS assembly code
3. Run the main function in src/main/kotlin/Main.kt
4. The output in machine code will be in the output.txt file in src/main/resources directory

## Supported Instructions
All standard MIPS instructions are supported, of all formats (R, I, J). The following is a list of all supported instructions:
```asm
add,addu,addi,addiu,and,andi,beq,bne,bgez,bgtz,blez,bltz,div,divu,j,jal,jr,jalr,lb,lbu,lh,lhu,lui,lw,mul,mult,multu,mfhi,mflo,mthi,mtlo,nor,or,ori,slt,slti,sltiu,sltu,sll,srl,sb,sc,sh,sw,sub,subu,xor,xori
```

## Features
- Supports all standard MIPS instructions
- Supports labels
- Supports comments
- Supports hexadecimal, binary, and decimal numbers.
- Supports Signed and Unsigned instructions.
- Whitespaces are ignored

## Examples
The following example is also in the input.asm file in src/main/resources directory.
```asm
# A Simple MIPS program to compute N*(N+3)
	.text 		     # Code Segment
	.globl main          # Declaring main as global
main:
    lw     $t0, 4($gp)       # fetch N
    add    $t1, $t0, $zero   # copy N to $t1
    addi   $t1, $t1, 3       # N+3
    mul    $t1, $t1, $t0     # N*(N+3)
    sw     $t1, 0($gp)       # i = ...
```

Which outputs the following machine code:
```
10001111100010000000000000000100
00000001000000000100100000100000
00100001001010010000000000000011
01110001001010000100100000000010
10101111100010010000000000000000
```