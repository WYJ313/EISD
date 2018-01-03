package gui.astViewer;

import org.eclipse.jdt.core.dom.*;

public class SimpleASTVisitor extends ASTVisitor {
	protected StringBuffer buffer = null;
	protected int indent = 0;
	protected CompilationUnit root = null;
	
	public SimpleASTVisitor(StringBuffer buffer, CompilationUnit root) {
		this.buffer = buffer;
		this.root = root;
	}
	
	public void preVisit(ASTNode node) {
		if (isIgnoreNode(node)) return;
		buffer.append(generatePrefixString(node));
		buffer.append(stateDependentMessage(node));
		indent = indent + 1;
	}
	
	public void postVisit(ASTNode node) {
		if (isIgnoreNode(node)) return;
		indent = indent - 1;
	}
	
	public String stateDependentMessage(ASTNode node) {
		return "";
	}

	private boolean isIgnoreNode(ASTNode node) {
		if ((node instanceof EmptyStatement) || (node instanceof Modifier) || 
				(node instanceof Javadoc) || (node instanceof LineComment)) return true;
		return false;
	}
		
	/**
	 * �򵥷��� Annotation�����ӽڵ�
	 */
	public boolean visit(AnnotationTypeDeclaration node) {
		buffer.append("\n");
		return true;
	}
	
	/**
	 * �򵥷���  Annotation�����ӽڵ�
	 */
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		buffer.append("\n");
		return true;
	}
	
	/**
	 * ��ʱ�����������༰���ӽڵ�
	 */
	public boolean visit(AnonymousClassDeclaration node) {
		buffer.append("\n");
		return true;
	}
	
	/**
	 * ����������ʽڵ�
	 */
	public boolean visit(ArrayAccess node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * �������鴴���ڵ�
	 */
	public boolean visit(ArrayCreation node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���������ʼ���ڵ�
	 */
	public boolean visit(ArrayInitializer node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * �����������ͽڵ�
	 */
	public boolean visit(ArrayType node) {
		buffer.append("\n");
		return true;
	}

	/** 
	 * ���ʶ������ڵ�
	 */
	public boolean visit(AssertStatement node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���ʸ�ֵ���ڵ�
	 */
	public boolean visit(Assignment node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���ʿ����ڵ�
	 */ 
	public boolean visit(Block node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ����ע�Ϳ�ڵ�
	 */
	public boolean visit(BlockComment node) {
		this.buffer.append("/* Comments */\n");
		return false;
	}

	/**
	 * ���ʲ��������ڵ�
	 */
	public boolean visit(BooleanLiteral node) {
		if (node.booleanValue() == true) this.buffer.append("{true}\n");
		else this.buffer.append("{false}\n");

		return true;
	}

	/**
	 * ���� break ���ڵ�
	 */
	public boolean visit(BreakStatement node) {
		buffer.append("{break}\n");
		return true;
	}

	/** 
	 * ��������ת�����ʽ�ڵ�
	 */
	public boolean visit(CastExpression node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���� catch ����
	 */
	public boolean visit(CatchClause node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * �����ַ������ڵ�
	 */
	public boolean visit(CharacterLiteral node) {
		this.buffer.append("{" + node.getEscapedValue()+"}\n");
		return false;
	}

	public boolean visit(ClassInstanceCreation node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���ʸ��ڵ�
	 */
	public boolean visit(CompilationUnit node) {
		buffer.append("\n");
		return true;
	}
	
	/**
	 * �����������ʽ�ڵ�
	 */
	public boolean visit(ConditionalExpression node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���ʹ��췽�����ýڵ�
	 */
	public boolean visit(ConstructorInvocation node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���� continue ���ڵ�
	 */
	public boolean visit(ContinueStatement node) {
		buffer.append("{continue}\n");
		return true;
	}

	/**
	 * ���� do ���ڵ�
	 */
	public boolean visit(DoStatement node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���ʿ����ڵ�
	 */
	public boolean visit(EmptyStatement node) {
		return false;
	}

	/**
	 * ������ǿ�� for ѭ�����ڵ�
	 */
	public boolean visit(EnhancedForStatement node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���ʳ���ö���������ڵ�
	 */
	public boolean visit(EnumConstantDeclaration node) {
		buffer.append("\n");
		return true;
	}

	/** 
	 * ����ö���������ڵ�
	 */
	public boolean visit(EnumDeclaration node) {
		buffer.append("\n");
		System.out.println("This is enum node");
		return true;
	}

	/**
	 * ���ʱ��ʽ���ڵ�
	 */
	public boolean visit(ExpressionStatement node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * �����ֶη������ڵ�
	 */
	public boolean visit(FieldAccess node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * �����ֶ��������ڵ� 
	 */
	public boolean visit(FieldDeclaration node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���� for ѭ�����ڵ�
	 */
	public boolean visit(ForStatement node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���� if ���ڵ�
	 */
	public boolean visit(IfStatement node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���� import ���ڵ�
	 */
	@SuppressWarnings("deprecation")
	public boolean visit(ImportDeclaration node) {
		buffer.append("{import ");
		if (node.getAST().apiLevel() >= AST.JLS4) {
			if (node.isStatic()) this.buffer.append("static ");
		}
		buffer.append(node.getName().getFullyQualifiedName());
		if (node.isOnDemand()) this.buffer.append(".*");
		buffer.append("}\n");
		return false;
	}

	/**
	 * ������׺���ʽ�ڵ�
	 */
	public boolean visit(InfixExpression node) {
		buffer.append("{" + node.getOperator().toString() + "}\n");
		return true;
	}

	/**
	 * ���� instanceof ���ʽ���ڵ�
	 */
	public boolean visit(InstanceofExpression node) {
		buffer.append("{ instanceof }\n");
		return true;
	}

	/**
	 * ���ʳ�ʼ�����ڵ�
	 */
	public boolean visit(Initializer node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(Javadoc node) {
		return false;
	}

	/**
	 * ���ʴ���ǩ���ڵ�
	 */
	public boolean visit(LabeledStatement node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ������ע�ͽڵ�
	 */
	public boolean visit(LineComment node) {
		return false;
	}


	/**
	 * ���ʱ�ǽڵ�
	 */
	public boolean visit(MarkerAnnotation node) {
		buffer.append("\n");
		return true;
	}


	public boolean visit(MemberRef node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(MemberValuePair node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(MethodRef node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(MethodRefParameter node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���ʷ��������ڵ�
	 */
	public boolean visit(MethodDeclaration node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ���ʷ������ýڵ�
	 */
	public boolean visit(MethodInvocation node) {
		buffer.append("\n");
		return true;
	}

	/**
	 * ǰ���Ѿ������� public, static ���������δʽڵ㣬���ﲻ�ٷ��ʣ�
	 */
	public boolean visit(Modifier node) {
		return false;
	}

	public boolean visit(NormalAnnotation node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(NullLiteral node) {
		buffer.append("{null}\n");
		return false;
	}

	public boolean visit(NumberLiteral node) {
		buffer.append("{" + node.getToken() + "}\n");
		return false;
	}

	public boolean visit(PackageDeclaration node) {
		buffer.append("{package ");
		buffer.append(node.getName().getFullyQualifiedName());
		buffer.append("}\n");
		return false;
	}


	public boolean visit(ParameterizedType node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(ParenthesizedExpression node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(PostfixExpression node) {
		buffer.append("{" + node.getOperator().toString() + "}\n");
		return true;
	}

	public boolean visit(PrefixExpression node) {
		buffer.append("{" + node.getOperator().toString() + "}\n");
		return true;
	}

	public boolean visit(PrimitiveType node) {
		buffer.append("{" + node.getPrimitiveTypeCode().toString() + "}\n");
		return false;
	}

	public boolean visit(QualifiedName node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(QualifiedType node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(ReturnStatement node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(SimpleName node) {
		buffer.append("{" + node.getIdentifier() + "}\n"); 
		return false;
	}

	public boolean visit(SimpleType node) {
		buffer.append("\n");
		return true;
	}


	public boolean visit(SingleMemberAnnotation node) {
		buffer.append("\n");
		return true;
	}


	public boolean visit(SingleVariableDeclaration node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(StringLiteral node) {
		buffer.append("{" + node.getEscapedValue() + "}\n");
		return false;
	}

	public boolean visit(SuperConstructorInvocation node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(SuperFieldAccess node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(SuperMethodInvocation node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(SwitchCase node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(SwitchStatement node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(SynchronizedStatement node) {
		buffer.append("\n");
		return true;
	}


	public boolean visit(TagElement node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(TextElement node) {
		buffer.append("{" + node.getText() + "}\n");
		return false;
	}

	public boolean visit(ThisExpression node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(ThrowStatement node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(TryStatement node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(TypeDeclaration node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(TypeDeclarationStatement node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(TypeLiteral node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(TypeParameter node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(VariableDeclarationExpression node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(VariableDeclarationStatement node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(VariableDeclarationFragment node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(WhileStatement node) {
		buffer.append("\n");
		return true;
	}

	public boolean visit(WildcardType node) {
		buffer.append("\n");
		return true;
	}
	

	/**
	 * ���ɷ��� AST �ڵ�ʱ���ڷ��ʽڵ���ӽڵ�֮ǰӦ�����ɵ��ַ���
	 */
	private String generatePrefixString(ASTNode node) {
		return getIndentString() + getLineString(node) + getSimpleClassName(node) + getModifierString(node);
	}
	
	/**
	 * ����ýڵ�Ӧ���������٣�
	 */
	private String getIndentString() {
		final String indentSpace = "    ";
		String result = "";
		for (int i = 0; i < indent; i++) result = result + indentSpace;
		
		return result;
	}

	/**
	 * ���ظýڵ����ڱ��뵥Ԫ��JavaԴ�ļ������ڵ��кţ�
	 */
	private String getLineString(ASTNode node) {
		return root.getLineNumber(node.getStartPosition()) + " ";
	}
	
	/**
	 * ���ظýڵ��һЩ���η�����public, static, final �ȵ�
	 */
	private String getModifierString(ASTNode node) {
		String modifiers = "";
		if (node instanceof BodyDeclaration) {
			int mod = ((BodyDeclaration)node).getModifiers();
			if (Modifier.isAbstract(mod)) modifiers += Modifier.ModifierKeyword.ABSTRACT_KEYWORD.toString() + " ";
			if (Modifier.isFinal(mod)) modifiers += Modifier.ModifierKeyword.FINAL_KEYWORD.toString() + " ";
			if (Modifier.isNative(mod)) modifiers += Modifier.ModifierKeyword.NATIVE_KEYWORD.toString() + " ";
			if (Modifier.isPrivate(mod)) modifiers += Modifier.ModifierKeyword.PRIVATE_KEYWORD.toString() + " ";
			if (Modifier.isProtected(mod)) modifiers += Modifier.ModifierKeyword.PROTECTED_KEYWORD.toString() + " ";
			if (Modifier.isPublic(mod)) modifiers += Modifier.ModifierKeyword.PUBLIC_KEYWORD.toString() + " ";
			if (Modifier.isStatic(mod)) modifiers += Modifier.ModifierKeyword.STATIC_KEYWORD.toString() + " ";
			if (Modifier.isStrictfp(mod)) modifiers += Modifier.ModifierKeyword.STRICTFP_KEYWORD.toString() + " ";
			if (Modifier.isSynchronized(mod)) modifiers += Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD.toString() + " ";
			if (Modifier.isTransient(mod)) modifiers += Modifier.ModifierKeyword.TRANSIENT_KEYWORD.toString() + " ";
			if (Modifier.isVolatile(mod)) modifiers += Modifier.ModifierKeyword.VOLATILE_KEYWORD.toString() + " ";	
			modifiers = "[" + modifiers.trim() + "]";
		}
		return modifiers;
	}
	
	/** 
	 * ���ؽڵ�����������
	 */
	private String getSimpleClassName(ASTNode node) {
		String className = node.getClass().getName();
		int index = className.lastIndexOf(".");
		if (index > 0) className = className.substring(index+1);
		return className;
	}
	
}
