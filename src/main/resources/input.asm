# Sample Program to add two numbers and store it in a register

	.data					# Data Segment
hello:  .asciiz 3	# A Null terminated string
	.text 					# Code Segment
	.globl main 				# Declaring main as global
	
main:
	add $t0, $zero, $zero	# $t0 = 0
	nor $t1, $t0, $t0		# $t1 = ~$t0