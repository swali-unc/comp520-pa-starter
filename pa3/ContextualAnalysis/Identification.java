package miniJava.ContextualAnalysis;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.AbstractSyntaxTrees.*;

public class Identification implements Visitor<Object,Object> {
	private ErrorReporter _errors;
	
	public Identification(ErrorReporter errors) {
		this._errors = errors;
		// TODO: predefined names
	}
	
	public void parse( Package prog ) {
		try {
			visitPackage(prog,null);
		} catch( IdentificationError e ) {
			_errors.reportError(e.toString());
		}
	}
	
	public Object visitPackage(Package prog, Object arg) throws IdentificationError {
		throw new IdentificationError("Not yet implemented!");
	}
	
	class IdentificationError extends Error {
		private static final long serialVersionUID = -441346906191470192L;
		private String _errMsg;
		
		public IdentificationError(AST ast, String errMsg) {
			super();
			this._errMsg = ast.posn == null
				? "*** " + errMsg
				: "*** " + ast.posn.toString() + ": " + errMsg;
		}
		
		@Override
		public String toString() {
			return _errMsg;
		}
	}
}