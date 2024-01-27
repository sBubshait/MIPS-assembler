# Sample Program to add two numbers and store it in a register

	.data					# Data Segment
hello:  .asciiz 3	# A Null terminated string
	.text 					# Code Segment
	.globl main 				# Declaring main as global
main:
	add $t0, $zero, $zero	# $t0 = 0
	nor $t1, $t0, $t0		# $t1 = ~$t0
	mult $t0, $t1			# $t0 = $t0 * $t1
	sll $s0, $s1, 2
	jr $s0
	MFHI $t0
	mul $t0, $t1, $t2

repeat:
    addi $t0, $t0, 1