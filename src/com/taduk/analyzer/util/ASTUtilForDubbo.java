package com.taduk.analyzer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

public class ASTUtilForDubbo {

	// final static HashMap<MethodDeclaration, ArrayList<MethodInvocation>>
	// invocationsForMethods = new HashMap<MethodDeclaration,
	// ArrayList<MethodInvocation>>();

	// public static ArrayList<MethodDeclarationMapper> methodDeclarationMappers
	// = new ArrayList<MethodDeclarationMapper>();
	// public static ArrayList<MethodInvocationMapper> methodInvocationMappers =
	// new ArrayList<MethodInvocationMapper>();

	private static HashMap<String, CompilationUnit> bindingUnitCache = new HashMap<String, CompilationUnit>();
	private static HashMap<String, CompilationUnitWithSource> unbindingUnitCache = new HashMap<String, CompilationUnitWithSource>();

	@SuppressWarnings("unused")
	private static CompilationUnit getBindingCompilationUnit(
			String projectName, String wholeClassName)
			throws JavaModelException {
		CompilationUnit unit = bindingUnitCache.get(wholeClassName);
		http: // wiki.kaiqi.com/pages/viewpage.action?pageId=38439974
		if (unit != null) {
			return unit;
		}
		IJavaProject javaProject = TaduJavaProject.WEB_JAVAPROJECT;
		ICompilationUnit cUnit = javaProject.findType(wholeClassName)
				.getCompilationUnit();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
		parser.setResolveBindings(true);
		parser.setSource(cUnit);
		unit = (CompilationUnit) parser.createAST(null);
		bindingUnitCache.put(wholeClassName, unit);
		return unit;
	}

	private static CompilationUnitWithSource getCompilationUnitBySource(
			String sourcePath) {
		CompilationUnitWithSource unit = unbindingUnitCache.get(sourcePath);
		if (unit != null) {
			return unit;
		}

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		String sourceContent;
		try {
			sourceContent = FileUtils.readFileToString(new File(sourcePath));
		} catch (IOException e) {
			// TODO
			return null;
		}
		parser.setSource(sourceContent.toCharArray());
		CompilationUnit cUnit = (CompilationUnit) parser.createAST(null);
		unit = new CompilationUnitWithSource(cUnit, sourceContent);
		unbindingUnitCache.put(sourcePath, unit);
		return unit;
	}

	@SuppressWarnings("unchecked")
	public static MethodDeclaration getMethodDeclare(IMethod method) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(method.getCompilationUnit());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit compiUnit = (CompilationUnit) parser.createAST(null);
		List<AbstractTypeDeclaration> types = compiUnit.types();

		for (AbstractTypeDeclaration type : types) {
			if (type.getNodeType() == ASTNode.TYPE_DECLARATION) {
				List<BodyDeclaration> bodies = type.bodyDeclarations();
				for (BodyDeclaration body : bodies) {
					// if (body.getNodeType() == ASTNode.FIELD_DECLARATION) {
					// FieldDeclaration fieldDeclaration = (FieldDeclaration)
					// body;
					// System.out.println("name: " + fieldDeclaration);
					// } else
					if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
						MethodDeclaration methodDec = (MethodDeclaration) body;
						if (methodDec.getName().toString()
								.equals(method.getElementName())) {
							return methodDec;
						}
					}
				}
			}
		}

		return null;
	}

	public static boolean isPublicMethod(IMethod method) {
		MethodDeclaration md = getMethodDeclare(method);
		int modifiers = md.getModifiers();
		return modifiers == Modifier.PUBLIC;
	}

	
	
	
	
	public static void generateInterface(String sourcePath) {
		List<String> interfaceContent = new ArrayList<String>();
		final CompilationUnitWithSource sourceUnit = getCompilationUnitBySource(sourcePath);
		final CompilationUnit unit = sourceUnit.getCompilationUnit();
		List<AbstractTypeDeclaration> types = unit.types();
		for (AbstractTypeDeclaration type : types) {
			interfaceContent.add("package com.tadu.common.iservices;");
			interfaceContent.add("");
			interfaceContent.add("import java.util.Date;");
			interfaceContent.add("import java.util.List;");
			interfaceContent.add("import com.tadu.common.pojo.Book;");
			interfaceContent.add("");
			
			String className = type.getName().toString();
			interfaceContent.add("public interface I" + className + " {");
			List<BodyDeclaration> bodies = type.bodyDeclarations();
			for (BodyDeclaration body : bodies) {
				if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
					MethodDeclaration methodDec = (MethodDeclaration) body;
					if (Modifier.isPublic(methodDec.getModifiers())
							&& Modifier.isStatic(methodDec.getModifiers())) {
						String params = StringUtils.substringBetween(methodDec
								.parameters().toString(), "[", "]");
						params = StringUtils.remove(params, "final");
						interfaceContent.add("    " + methodDec.getReturnType2()+ " " + methodDec.getName() + "(" +params +");");
					}
				}
			}
			interfaceContent.add("}");
			try {
				IOUtils.writeLines(interfaceContent, null, new FileOutputStream(new File("c://interfaceSource//I"  + className + ".java")), "utf-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int[] countMethod(String sourcePath) {
		final CompilationUnitWithSource sourceUnit = getCompilationUnitBySource(sourcePath);
		final CompilationUnit unit = sourceUnit.getCompilationUnit();
		List<AbstractTypeDeclaration> types = unit.types();
		int methodCount = 0;
		int classCount = 0;
		for (AbstractTypeDeclaration type : types) {
			classCount++;
//			String className = type.getName().toString();
			List<BodyDeclaration> bodies = type.bodyDeclarations();
			for (BodyDeclaration body : bodies) {
				if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
//					MethodDeclaration methodDec = (MethodDeclaration) body;
//					if(Modifier.isPublic(methodDec.getModifiers()) && Modifier.isStatic(methodDec.getModifiers())) {
						methodCount++;
//					}
				}
			}
//			System.out.println(className + " : "+ methodCount);
		}
		return new int[] { classCount, methodCount };
	}

	@SuppressWarnings("unchecked")
	public static ClassLineNumberMapper parserJavaFile(String sourcePath) {
		// final CompilationUnit unit =
		// getBindingCompilationUnit("tadu-android",
		// "com.tywire.tadu.cios.action.BookAction");
		final CompilationUnitWithSource sourceUnit = getCompilationUnitBySource(sourcePath);
		final CompilationUnit unit = sourceUnit.getCompilationUnit();
		// List<LineComment> comments = unit.getCommentList();
		List<TypeLine> commentLines = new ArrayList<TypeLine>();
		List<TypeLine> importLines = new ArrayList<TypeLine>();

		CommentVisitor commentV = new CommentVisitor(unit, sourceUnit
				.getSourceContent().split("\n"));
		for (Comment comment : (List<Comment>) unit.getCommentList()) {
			comment.accept(commentV);
			commentLines.add(commentV.getCommentLine());
		}
		// String packageName = unit.getPackage() != null ?
		// unit.getPackage().getName().toString() : "";
		// String className = "";

		List<ImportDeclaration> imports = unit.imports();

		TypeLine importLine = null;
		for (ImportDeclaration importDeclaration : imports) {
			int startLineNumber = unit.getLineNumber(importDeclaration
					.getStartPosition()) - 1;
			importLine = new TypeLine(importDeclaration.toString(),
					startLineNumber, startLineNumber);
			importLines.add(importLine);
		}

		List<AbstractTypeDeclaration> types = unit.types();

		ClassLineNumberMapper clnm = new ClassLineNumberMapper();
		List<TypeLine> fieldLines = new ArrayList<TypeLine>();
		List<TypeLine> methodLines = new ArrayList<TypeLine>();

		for (AbstractTypeDeclaration type : types) {
			// className = type.getName().toString();
			if (type.getNodeType() == ASTNode.IMPORT_DECLARATION) {
				System.out.println(1);
			} else if (type.getNodeType() == ASTNode.TYPE_DECLARATION) {
				List<BodyDeclaration> bodies = type.bodyDeclarations();
				for (BodyDeclaration body : bodies) {
					// String classNameWithPackage = packageName + "." +
					// className;
					if (body.getNodeType() == ASTNode.FIELD_DECLARATION) {
						FieldDeclaration fieldDeclaration = (FieldDeclaration) body;

						int startLineNumber = unit
								.getLineNumber(fieldDeclaration
										.getStartPosition()); //
						int endLineNumber = unit.getLineNumber(fieldDeclaration
								.getStartPosition()
								+ fieldDeclaration.getLength());
						fieldLines.add(new TypeLine(
								fieldDeclaration.toString(), startLineNumber,
								endLineNumber));
						/*
						 * VariableDeclarationFragment vdFragment =
						 * (VariableDeclarationFragment)
						 * fieldDeclaration.fragments().get(0); if (simpleType
						 * != null && simpleType.equals("String")) { //TODO
						 * safe? VariableDeclarationFragment vdFragment =
						 * (VariableDeclarationFragment)
						 * fieldDeclaration.fragments().get(0); String
						 * fieldValue = vdFragment.getInitializer().toString();
						 * if (fieldValue.endsWith(".shtml\"")) {
						 * System.out.println(fieldValue); } }
						 */
						System.out.println("name: " + fieldDeclaration);
					} else if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
						MethodDeclaration methodDec = (MethodDeclaration) body;
						int startLine = unit.getLineNumber(methodDec
								.getStartPosition()); //
						int endLine = unit.getLineNumber(methodDec
								.getStartPosition() + methodDec.getLength());
						System.out.println(1);
						methodLines.add(new TypeLine(methodDec.getName()
								.toString(), startLine, endLine));
					}
				}
				clnm.fieldLineNumbers = fieldLines;
				clnm.methodLineNumbers = methodLines;
			}
		}
		clnm.importLineNumbers = importLines;
		clnm.commentLineNumbers = commentLines;

		/*
		 * unit.accept(new ASTVisitor() {
		 * 
		 * private MethodDeclaration activeMethod; private
		 * MethodInvocationMapper miMapper = null;
		 * 
		 * //
		 * 
		 * @Override public void endVisit(SimpleType node) { // TODO
		 * Auto-generated method stub super.endVisit(node); }
		 * 
		 * @Override public boolean visit(SimpleType node) { // TODO
		 * Auto-generated method stub return super.visit(node); }
		 * 
		 * // 如 BookBO b = getBookNoImgIgnoreStatusById(id);
		 * 
		 * @Override public boolean visit(VariableDeclarationStatement node) {
		 * // TODO 假如shtml模板出现在代码中，在此处做处理，找出来。 return super.visit(node); }
		 * 
		 * @Override public void endVisit(FieldDeclaration node) {
		 * 
		 * System.out.println("[field declare]" + node.getType());
		 * node.accept(new ASTVisitor() {
		 * 
		 * @Override public void endVisit(VariableDeclarationFragment node) {
		 * System.out.println("   [variable declare]" + node.getName());
		 * super.endVisit(node); } }); super.endVisit(node); }
		 * 
		 * @Override public boolean visit(MethodInvocation node) { if
		 * (invocationsForMethods.get(activeMethod) == null) {
		 * invocationsForMethods.put(activeMethod, new
		 * ArrayList<MethodInvocation>()); }
		 * invocationsForMethods.get(activeMethod).add(node);
		 * 
		 * 
		 * Expression exp = node.getExpression(); if(exp !=null) { ITypeBinding
		 * typeBinding = exp.resolveTypeBinding(); if (typeBinding != null) {
		 * System.out.println("Type: " + typeBinding.toString()); } }
		 * 
		 * 
		 * String methodName = node.getName().toString(); ITypeBinding
		 * typeBinding = node.getExpression() .resolveTypeBinding(); IType type
		 * = (IType) typeBinding.getJavaElement();
		 * 
		 * System.out.printf("Type %s (method %s) calls %s\n",
		 * type.getTypeQualifiedName(), methodName,
		 * type.getFullyQualifiedName());
		 * 
		 * if (activeMethod != null) { miMapper = new MethodInvocationMapper();
		 * miMapper.methodDec = activeMethod; miMapper.classNameWithPackage =
		 * classWholeName; miMapper.methodInvocation = node; int lineNumber =
		 * unit.getLineNumber(node.getStartPosition()); // 获取行号 //
		 * from：http://stackoverflow
		 * .com/questions/11126857/eclipse-astnode-to-source-code-line-number
		 * methodInvocationMappers.add(miMapper); }
		 * 
		 * return super.visit(node); }
		 * 
		 * @Override public void endVisit(MethodInvocation node) {
		 * 
		 * System.out.println("  [method invocation]" + node);
		 * System.out.println("      [expression]" + node.getExpression());
		 * System.out.println("      [methodName]" + node.getName());
		 * super.endVisit(node); }
		 * 
		 * @Override public boolean visit(MethodDeclaration node) { activeMethod
		 * = node; System.out.println("[method declare start]" +
		 * node.getName()); return super.visit(node); }
		 * 
		 * @Override public void endVisit(MethodDeclaration node) {
		 * System.out.println("[method declare end]" + node.getName());
		 * super.endVisit(node); }
		 * 
		 * });
		 */
		System.out.println(1);
		return clnm;
	}

	public static void main(String[] args) {
		String s = "+   /* sdfsdf";
		System.out.println(StringUtils.remove(s, "+").trim());

	}
}
