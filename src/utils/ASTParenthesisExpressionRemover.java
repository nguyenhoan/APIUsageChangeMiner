package utils;

import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.internal.core.dom.NaiveASTFlattener;

public class ASTParenthesisExpressionRemover extends NaiveASTFlattener {
	@Override
	public boolean visit(ParenthesizedExpression node) {
		if ((node.getParent() instanceof PrefixExpression) && (node.getExpression() instanceof InstanceofExpression)) {
			this.buffer.append("(");
			node.getExpression().accept(this);
			this.buffer.append(")");
		}
		else
			node.getExpression().accept(this);
		return false;
	}
}
