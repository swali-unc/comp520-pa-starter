package miniJava.ContextualAnalysis;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

public class TypeChecking implements Visitor<Object, TypeDenoter> {
	private ErrorReporter _errors;
	
	public TypeChecking(ErrorReporter errors) {
		this._errors = errors;
	}
	
	public void parse(Package prog) {
		prog.visit(this, null);
	}

	private void reportTypeError(AST ast, String errMsg) {
		_errors.reportError( ast.posn == null
				? "*** " + errMsg
				: "*** " + ast.posn.toString() + ": " + errMsg );
	}
}
