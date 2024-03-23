package miniJava.CodeGeneration.x64.ISA;

import miniJava.CodeGeneration.x64.Instruction;
import miniJava.CodeGeneration.x64.Reg64;
import miniJava.CodeGeneration.x64.x64;

public class Mov_ri64 extends Instruction {
	// mov r64,imm64 variant
	public Mov_ri64(Reg64 reg, long imm64) {
		rexW = true; // operand is 64bit
		// TODO: first, check if the Reg64 is R8-R15, if it is, set one of rexB,rexW,rexR,rexX to true (which one?)
		// TODO: second, find the opcode for pop r, where r is a plain 64-bit register
		// NOTE: x64.getIdx(r) will return a 0-7 index, whereas r.getIdx() returns an index from 0-15
		opcodeBytes.write( ?? );
		x64.writeLong(immBytes,imm64);
	}
}
