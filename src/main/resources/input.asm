# A Simple MIPS program to compute N*(N+3)

	.text 					 # Code Segment
	.globl main 			 # Declaring main as global
main:
	lw     $t0, 4($gp)       # fetch N
    add    $t1, $t0, $zero   # copy N to $t1
    addi   $t1, $t1, 3       # N+3
    mul    $t1, $t1, $t0     # N*(N+3)
    sw     $t1, 0($gp)       # i = ...