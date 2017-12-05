package dependency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import change.CMethod;
import change.ChangeEntity;

import utils.JavaASTUtil;

public class DependencyVisitor extends ASTVisitor {
	public static final String PROPERTY_SCOPE_VARS = "sv", PROPERTY_RETURN_TYPE = "return", PROPERTY_OBJ_TYPE = "TypeBinding";
	
	private ChangeEntity entity;
	private ASTNode root;
	private HashMap<String, String> allFieldTypes;
	private String className, superClassName;
	private HashMap<String, String> localVariables = new HashMap<String, String>();
	private HashMap<String, SimpleName> mapName = new HashMap<String, SimpleName>();
	
	public DependencyVisitor(ChangeEntity entity, ASTNode root, String className, String superClassName, HashMap<String, String> allFieldTypes) {
		this.entity = entity;
		this.root = root;
		this.className = className;
		this.superClassName = superClassName;
		this.allFieldTypes = allFieldTypes;
	}
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		if (node.bodyDeclarations() != null) {
			for (int i = 0; i < node.bodyDeclarations().size(); i++)
				((ASTNode)(node.bodyDeclarations().get(i))).accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return true;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		if (node.bodyDeclarations() != null) {
			for (int i = 0; i < node.bodyDeclarations().size(); i++)
				((ASTNode)(node.bodyDeclarations().get(i))).accept(this);
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayAccess node) {
		return true;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		for (int i = 0; i < node.dimensions().size(); i++)
			((ASTNode)(node.dimensions().get(i))).accept(this);
		if (node.getInitializer() != null) {
			node.getInitializer().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		return true;
	}

	@Override
	public boolean visit(ArrayType node) {
		return false;
	}

	@Override
	public boolean visit(AssertStatement node) {
		return true;
	}

	@Override
	public boolean visit(Assignment node) {
		/*if (node.getOperator() == Assignment.Operator.ASSIGN)
			this.assignments.put(node.getLeftHandSide().toString(), node.getRightHandSide());*/
		//this.assignments.put(node.getLeftHandSide().toString(), getRightHandSide(node));
		return true;
	}

	@SuppressWarnings("unused")
	private Expression getRightHandSide(Assignment node) {
		if (node.getOperator() == Assignment.Operator.ASSIGN)
			return node.getRightHandSide();
		InfixExpression infix = node.getAST().newInfixExpression();
		infix.setLeftOperand((Expression) ASTNode.copySubtree(infix.getAST(), node.getLeftHandSide()));
		try {
			infix.setRightOperand((Expression) ASTNode.copySubtree(infix.getAST(), node.getRightHandSide()));
		} catch (Exception e) {
			return node.getRightHandSide();
		}
		setOperator(infix, node.getOperator());
		return infix;
	}

	private void setOperator(InfixExpression infix, Assignment.Operator op) {
		if (op == Assignment.Operator.BIT_AND_ASSIGN)
			infix.setOperator(Operator.AND);
		else if (op == Assignment.Operator.BIT_OR_ASSIGN)
			infix.setOperator(Operator.OR);
		else if (op == Assignment.Operator.BIT_XOR_ASSIGN)
			infix.setOperator(Operator.XOR);
		else if (op == Assignment.Operator.DIVIDE_ASSIGN)
			infix.setOperator(Operator.DIVIDE);
		else if (op == Assignment.Operator.LEFT_SHIFT_ASSIGN)
			infix.setOperator(Operator.LEFT_SHIFT);
		else if (op == Assignment.Operator.MINUS_ASSIGN)
			infix.setOperator(Operator.MINUS);
		else if (op == Assignment.Operator.PLUS_ASSIGN)
			infix.setOperator(Operator.PLUS);
		else if (op == Assignment.Operator.REMAINDER_ASSIGN)
			infix.setOperator(Operator.REMAINDER);
		else if (op == Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN)
			infix.setOperator(Operator.RIGHT_SHIFT_SIGNED);
		else if (op == Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN)
			infix.setOperator(Operator.RIGHT_SHIFT_UNSIGNED);
		else if (op == Assignment.Operator.TIMES_ASSIGN)
			infix.setOperator(Operator.TIMES);
		else {
			System.err.println("ERROR: unknown assignment operator!!!");
			System.exit(1);
		}
	}

	@Override
	public boolean visit(Block node) {
		HashSet<SimpleName> vars = new HashSet<SimpleName>();
		for (int i = 0; i < node.statements().size(); i++) {
			if (node.statements().get(i) instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement dec = (VariableDeclarationStatement) node.statements().get(i);
				String type = JavaASTUtil.getType(dec.getType());
				for (int j = 0; j < dec.fragments().size(); j++) {
					VariableDeclarationFragment frag = (VariableDeclarationFragment) dec.fragments().get(j);
					String var = frag.getName().getIdentifier();
					vars.add(frag.getName());
					this.localVariables.put(var, type);
					HashSet<SimpleName> names = new HashSet<SimpleName>();
					names.add(frag.getName());
					this.mapName.put(var, frag.getName());
				}
			}
		}
		node.setProperty(PROPERTY_SCOPE_VARS, vars);
		return true;
	}

	@Override
	public boolean visit(BlockComment node) {
		return false;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		return false;
	}

	@Override
	public boolean visit(BreakStatement node) {
		return false;
	}

	@Override
	public boolean visit(CastExpression node) {
		String type = JavaASTUtil.getType(node.getType());
		node.setProperty(PROPERTY_RETURN_TYPE, type);
		node.getExpression().accept(this);
		return false;
	}

	@Override
	public boolean visit(CatchClause node) {
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		return false;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		String type = JavaASTUtil.getType(node.getType());
		/*if (this.entity.getCFile().getcSystem().getDataGraph().getTypeNames().contains(type)) {
			String methodName = type + "(" + node.arguments().size() + ")";
			HashSet<CMethod> methods = this.entity.getCFile().getcSystem().getDataGraph().getInheritedMethods(type, methodName, new HashSet<CClass>());
			if (methods.isEmpty())
				this.calleeNames.add(methodName);
			else
				this.callees.addAll(methods);
		}*/
		node.setProperty(PROPERTY_RETURN_TYPE, type);
		node.setProperty(PROPERTY_OBJ_TYPE, type);
		if (node.getExpression() != null)
			node.getExpression().accept(this);
		for (int i = 0; i < node.arguments().size(); i++)
			((ASTNode) (node.arguments().get(i))).accept(this);
		if (node.getAnonymousClassDeclaration() != null)
			node.getAnonymousClassDeclaration().accept(this);
		return false;
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		return true;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		node.setProperty(PROPERTY_OBJ_TYPE, className);
		node.setProperty(PROPERTY_RETURN_TYPE, className);
		for (int i = 0; i < node.arguments().size(); i++)
			((ASTNode) (node.arguments().get(i))).accept(this);
		return false;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		return false;
	}

	@Override
	public boolean visit(DoStatement node) {
		return true;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		return false;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		SingleVariableDeclaration dec = node.getParameter();
		String var = dec.getName().getIdentifier(), type = JavaASTUtil.getType(dec.getType());
		this.localVariables.put(var, type);
		HashSet<SimpleName> names = new HashSet<SimpleName>();
		names.add(dec.getName());
		this.mapName.put(var, dec.getName());
		return true;
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		printError(node);
		return false;
	}

	private void printError(ASTNode node) {
		System.err.println("Error: " + node.getClass().getSimpleName() + " should not be in the body of method or field. In DependencyVisitor!");
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		printError(node);
		return false;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		return true;
	}

	@Override
	public boolean visit(FieldAccess node) {
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		for (int i = 0; i < node.fragments().size(); i++)
			((ASTNode)(node.fragments().get(i))).accept(this);
		return false;
	}

	@Override
	public boolean visit(ForStatement node) {
		for (int i = 0; i < node.initializers().size() && node.initializers().get(i) instanceof VariableDeclarationExpression; i++) {
			VariableDeclarationExpression decEx = (VariableDeclarationExpression) node.initializers().get(i);
			for (int j = 0; j < decEx.fragments().size(); j++) {
				VariableDeclarationFragment decFrag = (VariableDeclarationFragment) decEx.fragments().get(j);
				String var = decFrag.getName().getIdentifier(), type = JavaASTUtil.getType(decEx.getType());
				this.localVariables.put(var, type);
				HashSet<SimpleName> names = new HashSet<SimpleName>();
				names.add(decFrag.getName());
				this.mapName.put(var, decFrag.getName());
			}
		}
		for (Iterator it = node.initializers().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			e.accept(this);
		}
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		return true;
	}

	@Override
	public boolean visit(InfixExpression node) {
		return true;
	}

	@Override
	public boolean visit(Initializer node) {
		return true;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		node.setProperty(PROPERTY_RETURN_TYPE, "int");
		node.getLeftOperand().accept(this);
		return false;
	}

	@Override
	public boolean visit(Javadoc node) {
		return false;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		return true;
	}

	@Override
	public boolean visit(LineComment node) {
		return false;
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		return false;
	}

	@Override
	public boolean visit(MemberRef node) {
		return false;
	}

	@Override
	public boolean visit(MemberValuePair node) {
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (node.getReturnType2() != null)
		for (int i = 0; i < node.parameters().size(); i++) {
			SingleVariableDeclaration dec = (SingleVariableDeclaration) node.parameters().get(i);
			String var = dec.getName().getIdentifier(), type = JavaASTUtil.getType(dec.getType());
			this.localVariables.put(var, type);
			HashSet<SimpleName> names = new HashSet<SimpleName>();
			names.add(dec.getName());
			this.mapName.put(var, dec.getName());
		}
		if (node.getBody() != null)
			node.getBody().accept(this);
		return false;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		if (node.getExpression() != null)
			node.getExpression().accept(this);
		for (int i = 0; i < node.arguments().size(); i++)
			((ASTNode) (node.arguments().get(i))).accept(this);
		return false;
	}

	@Override
	public boolean visit(MethodRef node) {
		return false;
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		return false;
	}

	@Override
	public boolean visit(Modifier node) {
		return false;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		return false;
	}

	@Override
	public boolean visit(NullLiteral node) {
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		return false;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(ParameterizedType node) {
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		return true;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		return true;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		return true;
	}

	@Override
	public boolean visit(PrimitiveType node) {
		return false;
	}

	@Override
	public boolean visit(QualifiedType node) {
		String type = JavaASTUtil.getType(node);
		node.setProperty(PROPERTY_OBJ_TYPE, type);
		node.setProperty(PROPERTY_RETURN_TYPE, type);
		return false;
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		/*String expr = node.toString();
		if (expr.equals("System.out") || expr.equals("System.err")) {
			return false;
		}*/
		return true;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		String name = node.getIdentifier();
		if (!node.isDeclaration() && !this.localVariables.containsKey(node.getIdentifier())) {
			if (this.allFieldTypes.containsKey(name)) {
				String type = this.allFieldTypes.get(name);
				node.setProperty(PROPERTY_RETURN_TYPE, type);
			}
			else //if (Character.isUpperCase(name.charAt(0))) 
				node.setProperty(PROPERTY_RETURN_TYPE, name);
		}
		else {
			String type = this.localVariables.get(name);
			node.setProperty(PROPERTY_RETURN_TYPE, type);
		}
		return false;
	}

	@Override
	public boolean visit(SimpleType node) {
		String type = node.getName().toString();
		node.getName().setProperty(PROPERTY_RETURN_TYPE, type);
		return false;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		return false;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		return true;
	}

	@Override
	public boolean visit(StringLiteral node) {
		return false;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		if (superClassName != null)
			node.setProperty(PROPERTY_OBJ_TYPE, superClassName);
		else
			node.setProperty(PROPERTY_OBJ_TYPE, "Object");
		for (int i = 0; i < node.arguments().size(); i++)
			((ASTNode) (node.arguments().get(i))).accept(this);
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		String name = node.getName().getIdentifier();
		node.setProperty(PROPERTY_OBJ_TYPE, superClassName);
		node.setProperty(PROPERTY_RETURN_TYPE, superClassName + "." + name);
		return false;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		String name = node.getName().getIdentifier() + "(" + node.arguments().size() + ")";
		if (superClassName == null) {
			node.setProperty(PROPERTY_OBJ_TYPE, "#Unknown#");
			node.setProperty(PROPERTY_RETURN_TYPE, name);
		}
		else {
			node.setProperty(PROPERTY_OBJ_TYPE, superClassName);
			node.setProperty(PROPERTY_RETURN_TYPE, superClassName + "." + name);
		}
		for (int i = 0; i < node.arguments().size(); i++)
			((ASTNode) (node.arguments().get(i))).accept(this);
		return false;
	}

	@Override
	public boolean visit(SwitchCase node) {
		return true;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		return true;
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		return true;
	}

	@Override
	public boolean visit(TagElement node) {
		// within doc
		return false;
	}

	@Override
	public boolean visit(TextElement node) {
		// within comment
		return false;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		return true;
	}
	
	@Override
	public boolean visit(ThisExpression node) {
		node.setProperty(PROPERTY_RETURN_TYPE, this.className);
		return false;
	}

	@Override
	public boolean visit(TryStatement node) {
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		return true;
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		return true;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		return false;
	}

	@Override
	public boolean visit(TypeParameter node) {
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		return true;
	}

	@Override
	public boolean visit(WhileStatement node) {
		return true;
	}

	@Override
	public boolean visit(WildcardType node) {
		return false;
	}
	
	@Override
	public void endVisit(ArrayAccess node) {
		if (node.getArray().getProperty(PROPERTY_RETURN_TYPE) != null) {
			String rt = (String) node.getArray().getProperty(PROPERTY_RETURN_TYPE);
			if (rt.endsWith("[]"))
				rt = rt.substring(0, rt.length() - 2);
			node.setProperty(PROPERTY_RETURN_TYPE, rt);
		}
	}
	
	@Override
	public void endVisit(Assignment node) {
		/*if (node.getOperator() == Assignment.Operator.ASSIGN)
			this.assignments.put(node.getLeftHandSide().toString(), node.getRightHandSide());*/
	}
	
	@Override
	public void endVisit(Block node) {
		HashSet<SimpleName> vars = (HashSet<SimpleName>) node.getProperty(PROPERTY_SCOPE_VARS);
		for (SimpleName name : vars) {
			String var = name.getIdentifier();
			this.localVariables.remove(var);
			this.mapName.remove(var);
		}
		node.setProperty(PROPERTY_SCOPE_VARS, null);
	}

	@Override
	public void endVisit(EnhancedForStatement node) {
		SingleVariableDeclaration dec = node.getParameter();
		String var = dec.getName().getIdentifier();
		this.localVariables.remove(var);
		this.mapName.remove(var);
	}

	@Override
	public void endVisit(ForStatement node) {
		for (int i = 0; i < node.initializers().size() && node.initializers().get(i) instanceof VariableDeclarationExpression; i++) {
			VariableDeclarationExpression decEx = (VariableDeclarationExpression) node.initializers().get(i);
			for (int j = 0; j < decEx.fragments().size(); j++) {
				VariableDeclarationFragment decFrag = (VariableDeclarationFragment) decEx.fragments().get(j);
				String var = decFrag.getName().getIdentifier();
				this.localVariables.remove(var);
				this.mapName.remove(var);
			}
		}
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		for (int i = 0; i < node.parameters().size(); i++) {
			SingleVariableDeclaration dec = (SingleVariableDeclaration) node.parameters().get(i);
			String var = dec.getName().getIdentifier();
			this.localVariables.remove(var);
			this.mapName.remove(var);
		}
	}
	
	@Override
	public void endVisit(ParenthesizedExpression node) {
		if (node.getExpression().getProperty(PROPERTY_RETURN_TYPE) == null)
			node.setProperty(PROPERTY_RETURN_TYPE, "#Uknown#");
		else
			node.setProperty(PROPERTY_RETURN_TYPE, node.getExpression().getProperty(PROPERTY_RETURN_TYPE));
	}
	
	@Override
	public void endVisit(FieldAccess node) {
		String name = node.getName().getIdentifier();
		if (node.getExpression().getNodeType() == ASTNode.THIS_EXPRESSION) {
			node.setProperty(PROPERTY_RETURN_TYPE, this.className + "." + name);
//			node.setProperty(PROPERTY_OBJ_TYPE, this.className);
		}
		else {
			node.setProperty(PROPERTY_RETURN_TYPE, node.toString());
//			node.setProperty(PROPERTY_OBJ_TYPE, node.getExpression().toString());
		}
	}
	
	@Override
	public void endVisit(QualifiedName node) {
		node.setProperty(PROPERTY_RETURN_TYPE, node.toString());
		node.setProperty(PROPERTY_OBJ_TYPE, node.getQualifier().toString());
	}
	
	@Override
	public void endVisit(MethodInvocation node) {
		String type = null;
		String methodName = node.getName().getIdentifier() + "(" + node.arguments().size() + ")";
		if (node.getExpression() != null && node.getExpression().getNodeType() != ASTNode.THIS_EXPRESSION) {
			if (node.getExpression().getProperty(PROPERTY_RETURN_TYPE) != null) {
				type = (String) node.getExpression().getProperty(PROPERTY_RETURN_TYPE);
			}
		}
		else {
			type = this.className;
		}
		if (type != null) {
			node.setProperty(PROPERTY_OBJ_TYPE, type);
		}
		if (methodName.equals("toString(0)"))
			node.setProperty(PROPERTY_RETURN_TYPE, "String");
		if (node.getProperty(PROPERTY_RETURN_TYPE) == null)
			node.setProperty(PROPERTY_RETURN_TYPE, node.toString());
		if (node.getProperty(PROPERTY_OBJ_TYPE) == null) {
			if (node.getExpression() == null)
				node.setProperty(PROPERTY_OBJ_TYPE, "#Unknown#");
			else
				node.setProperty(PROPERTY_OBJ_TYPE, node.getExpression().toString());
		}
	}
	
}
