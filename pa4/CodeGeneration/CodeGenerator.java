package miniJava.CodeGeneration;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.CodeGeneration.x64.*;
import miniJava.CodeGeneration.x64.ISA.*;

public class CodeGenerator implements Visitor<Object, Object> {
	private ErrorReporter _errors;
	private InstructionList _asm; // our list of instructions that are used to make the code section
	
	public CodeGenerator(ErrorReporter errors) {
		this._errors = errors;
	}
	
	public void parse(Package prog) {
		_asm = new InstructionList();
		
		// If you haven't refactored the name "ModRMSIB" to something like "R",
		//  go ahead and do that now. You'll be needing that object a lot.
		// Here is some example code.
		
		// Simple operations:
		// _asm.add( new Push(0) ); // push the value zero onto the stack
		// _asm.add( new Pop(Reg64.RCX) ); // pop the top of the stack into RCX
		
		// Fancier operations:
		// _asm.add( new Cmp(new ModRMSIB(Reg64.RCX,Reg64.RDI)) ); // cmp rcx,rdi
		// _asm.add( new Cmp(new ModRMSIB(Reg64.RCX,0x10,Reg64.RDI)) ); // cmp [rcx+0x10],rdi
		// _asm.add( new Add(new ModRMSIB(Reg64.RSI,Reg64.RCX,4,0x1000,Reg64.RDX)) ); // add [rsi+rcx*4+0x1000],rdx
		
		// Thus:
		// new ModRMSIB( ... ) where the "..." can be:
		//  RegRM, RegR						== rm, r
		//  RegRM, int, RegR				== [rm+int], r
		//  RegRD, RegRI, intM, intD, RegR	== [rd+ ri*intM + intD], r
		// Where RegRM/RD/RI are just Reg64 or Reg32 or even Reg8
		//
		// Note there are constructors for ModRMSIB where RegR is skipped.
		// This is usually used by instructions that only need one register operand, and often have an immediate
		//   So they actually will set RegR for us when we create the instruction. An example is:
		// _asm.add( new Mov_rmi(new ModRMSIB(Reg64.RDX,true), 3) ); // mov rdx,3
		//   In that last example, we had to pass in a "true" to indicate whether the passed register
		//    is the operand RM or R, in this case, true means RM
		//  Similarly:
		// _asm.add( new Push(new ModRMSIB(Reg64.RBP,16)) );
		//   This one doesn't specify RegR because it is: push [rbp+16] and there is no second operand register needed
		
		// Patching example:
		// Instruction someJump = new Jmp((int)0); // 32-bit offset jump to nowhere
		// _asm.add( someJump ); // populate listIdx and startAddress for the instruction
		// ...
		// ... visit some code that probably uses _asm.add
		// ...
		// patch method 1: calculate the offset yourself
		//     _asm.patch( someJump.listIdx, new Jmp(asm.size() - someJump.startAddress - 5) );
		// -=-=-=-
		// patch method 2: let the jmp calculate the offset
		//  Note the false means that it is a 32-bit immediate for jumping (an int)
		//     _asm.patch( someJump.listIdx, new Jmp(asm.size(), someJump.startAddress, false) );
		
		prog.visit(this,null);
		
		// Output the file "a.out" if no errors
		if( !_errors.hasErrors() )
			makeElf("a.out");
	}

	@Override
	public Object visitPackage(Package prog, Object arg) {
		// TODO: visit relevant parts of our AST
		return null;
	}
	
	public void makeElf(String fname) {
		ELFMaker elf = new ELFMaker(_errors, _asm.getSize(), 8); // bss ignored until PA5, set to 8
		elf.outputELF(fname, _asm.getBytes(), ??); // TODO: set the location of the main method
	}
	
	private int makeMalloc() {
		int idxStart = _asm.add( new Mov_rmi(new ModRMSIB(Reg64.RAX,true),0x09) ); // mmap
		
		_asm.add( new Xor(		new ModRMSIB(Reg64.RDI,Reg64.RDI)) 	); // addr=0
		_asm.add( new Mov_rmi(	new ModRMSIB(Reg64.RSI,true),0x1000) ); // 4kb alloc
		_asm.add( new Mov_rmi(	new ModRMSIB(Reg64.RDX,true),0x03) 	); // prot read|write
		_asm.add( new Mov_rmi(	new ModRMSIB(Reg64.R10,true),0x22) 	); // flags= private, anonymous
		_asm.add( new Mov_rmi(	new ModRMSIB(Reg64.R8, true),-1) 	); // fd= -1
		_asm.add( new Xor(		new ModRMSIB(Reg64.R9,Reg64.R9)) 	); // offset=0
		_asm.add( new Syscall() );
		
		// pointer to newly allocated memory is in RAX
		// return the index of the first instruction in this method, if needed
		return idxStart;
	}
	
	private int makePrintln() {
		// TODO: how can we generate the assembly to println?
		return -1;
	}
}
