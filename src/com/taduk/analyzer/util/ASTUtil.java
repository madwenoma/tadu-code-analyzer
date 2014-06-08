package com.taduk.analyzer.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;

class CompilationUnitWithSource {
	
	private CompilationUnit compilationUnit;
	private String sourceContent;
	public CompilationUnitWithSource(CompilationUnit compilationUnit, String sourceContent) {
		super();
		this.compilationUnit = compilationUnit;
		this.sourceContent = sourceContent;
	}
	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}
	public String getSourceContent() {
		return sourceContent;
	}
	
}

class MethodDeclarationMapper {
	String classNameWithPackage;
	MethodDeclaration methodDec;
}

class MethodInvocationMapper {
	String classNameWithPackage;
	MethodDeclaration methodDec;
	MethodInvocation methodInvocation;

	@Override
	public String toString() {
		return "MethodInvocationMapper [classNameWithPackage="
				+ classNameWithPackage + ", methodDec=" + methodDec.getName()
				+ ", methodInvocation=" + methodInvocation + "]";
	}

}

public class ASTUtil {
	
	private static HashMap<String, CompilationUnit> bindingUnitCache = new HashMap<String, CompilationUnit>();
	private static HashMap<String, CompilationUnitWithSource> unbindingUnitCache = new HashMap<String, CompilationUnitWithSource>();
	
	
	@SuppressWarnings("unused")
	private static CompilationUnit getBindingCompilationUnit(String projectName, String wholeClassName) throws JavaModelException {
		CompilationUnit unit = bindingUnitCache.get(wholeClassName);
		if(unit != null) {
			return unit;
		}
		IJavaProject javaProject = TaduJavaProject.WEB_JAVAPROJECT;
		ICompilationUnit cUnit = javaProject.findType(wholeClassName).getCompilationUnit();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
		parser.setResolveBindings(true);
		parser.setSource(cUnit);
		unit = (CompilationUnit) parser.createAST(null);
		bindingUnitCache.put(wholeClassName, unit);
		return unit;
	}
	
	private static CompilationUnitWithSource getCompilationUnitBySource(String sourcePath) {
		CompilationUnitWithSource unit = unbindingUnitCache.get(sourcePath);
		if(unit != null) {
			return unit;
		}
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		String sourceContent;
		try {
			sourceContent = FileUtils.readFileToString(new File(sourcePath));
		} catch (IOException e) {
			throw new RuntimeException("getCompilationUnitBySource: can't read file");
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
		return Modifier.isPublic(modifiers);
	}

	@SuppressWarnings("unchecked")
	public static ClassLineNumberMapper parserJavaFile(String sourcePath)  {
		final CompilationUnitWithSource sourceUnit = getCompilationUnitBySource(sourcePath);
		final CompilationUnit unit = sourceUnit.getCompilationUnit();
		// List<LineComment> comments = unit.getCommentList();
		List<TypeLine> commentLines = new ArrayList<TypeLine>();
		List<TypeLine> importLines = new ArrayList<TypeLine>();
		 
		CommentVisitor commentVisitor = new CommentVisitor(unit, sourceUnit.getSourceContent().split("\n"));
		for (Comment comment : (List<Comment>) unit.getCommentList()) {
			comment.accept(commentVisitor);
			commentLines.add(commentVisitor.getCommentLine());
		}
//		String packageName = unit.getPackage() != null ? unit.getPackage().getName().toString() : "";
//		String className = "";
		
		List<ImportDeclaration> imports = unit.imports();
		
		TypeLine importLine = null;
		for (ImportDeclaration importDeclaration : imports) {
			int startLineNumber = unit.getLineNumber(importDeclaration.getStartPosition()) - 1;
			importLine = new TypeLine(importDeclaration.toString(), startLineNumber, startLineNumber);
			importLines.add(importLine);
		}
		
		List<AbstractTypeDeclaration> types = unit.types();
		
		ClassLineNumberMapper clnm = new ClassLineNumberMapper();
		List<TypeLine> fieldLines = new ArrayList<TypeLine>();
		List<TypeLine> methodLines = new ArrayList<TypeLine>();
		
		for (AbstractTypeDeclaration type : types) {
//			className = type.getName().toString();
			if(type.getNodeType() == ASTNode.IMPORT_DECLARATION){
			} else if (type.getNodeType() == ASTNode.TYPE_DECLARATION) {
				List<BodyDeclaration> bodies = type.bodyDeclarations();
				for (BodyDeclaration body : bodies) {
//					String classNameWithPackage = packageName + "."	+ className;
					if (body.getNodeType() == ASTNode.FIELD_DECLARATION) {
						FieldDeclaration fieldDeclaration = (FieldDeclaration) body;
						
						int startLineNumber = unit.getLineNumber(fieldDeclaration.getStartPosition()); // 
						int endLineNumber = unit.getLineNumber(fieldDeclaration.getStartPosition() + fieldDeclaration.getLength());
						fieldLines.add(new TypeLine(fieldDeclaration.toString(), startLineNumber, endLineNumber));
						/*
						VariableDeclarationFragment vdFragment = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
									if (simpleType != null && simpleType.equals("String")) {
							//TODO safe?
							VariableDeclarationFragment vdFragment = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
							String fieldValue = vdFragment.getInitializer().toString();
							if (fieldValue.endsWith(".shtml\"")) {
								System.out.println(fieldValue);
							}
						}*/
					} else if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
						MethodDeclaration methodDec = (MethodDeclaration) body;
						int startLine = unit.getLineNumber(methodDec.getStartPosition()); // 
						int endLine = unit.getLineNumber(methodDec.getStartPosition() + methodDec.getLength());
						System.out.println(1);
						methodLines.add(new TypeLine(methodDec.getName().toString(),startLine, endLine));
					}
				}
				clnm.fieldLineNumbers = fieldLines;
				clnm.methodLineNumbers = methodLines;
			}
		}
		clnm.importLineNumbers = importLines;
		clnm.commentLineNumbers = commentLines;
		


/*		unit.accept(new ASTVisitor() {

			private MethodDeclaration activeMethod;
			private MethodInvocationMapper miMapper = null;

			//
			@Override
			public void endVisit(SimpleType node) {
				// TODO Auto-generated method stub
				super.endVisit(node);
			}

			@Override
			public boolean visit(SimpleType node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			// 如 BookBO b = getBookNoImgIgnoreStatusById(id);
			@Override
			public boolean visit(VariableDeclarationStatement node) {
				// TODO 假如shtml模板出现在代码中，在此处做处理，找出来。
				return super.visit(node);
			}

			@Override
			public void endVisit(FieldDeclaration node) {
				
				System.out.println("[field declare]" + node.getType());
				node.accept(new ASTVisitor() {
					@Override
					public void endVisit(VariableDeclarationFragment node) {
						System.out.println("   [variable declare]"
								+ node.getName());
						super.endVisit(node);
					}
				});
				super.endVisit(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				if (invocationsForMethods.get(activeMethod) == null) {
					invocationsForMethods.put(activeMethod,
							new ArrayList<MethodInvocation>());
				}
				invocationsForMethods.get(activeMethod).add(node);

				
				Expression exp = node.getExpression();
				if(exp !=null) {
					ITypeBinding typeBinding = exp.resolveTypeBinding();
					if (typeBinding != null) {
						System.out.println("Type: " + typeBinding.toString());
					}
				}

				
				 * String methodName = node.getName().toString(); ITypeBinding
				 * typeBinding = node.getExpression() .resolveTypeBinding();
				 * IType type = (IType) typeBinding.getJavaElement();
				 * 
				 * System.out.printf("Type %s (method %s) calls %s\n",
				 * type.getTypeQualifiedName(), methodName,
				 * type.getFullyQualifiedName());
				 
				if (activeMethod != null) {
					miMapper = new MethodInvocationMapper();
					miMapper.methodDec = activeMethod;
					miMapper.classNameWithPackage = classWholeName;
					miMapper.methodInvocation = node;
					int lineNumber = unit.getLineNumber(node.getStartPosition()); // 获取行号
																					// from：http://stackoverflow.com/questions/11126857/eclipse-astnode-to-source-code-line-number
					methodInvocationMappers.add(miMapper);
				}

				return super.visit(node);
			}

			@Override
			public void endVisit(MethodInvocation node) {

				System.out.println("  [method invocation]" + node);
				System.out.println("      [expression]" + node.getExpression());
				System.out.println("      [methodName]" + node.getName());
				super.endVisit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				activeMethod = node;
				System.out.println("[method declare start]" + node.getName());
				return super.visit(node);
			}

			@Override
			public void endVisit(MethodDeclaration node) {
				System.out.println("[method declare end]" + node.getName());
				super.endVisit(node);
			}

		});
*/
		return clnm;
	}
	
	public static void main(String[] args) {
		String s = "+   /* sdfsdf";
		System.out.println(StringUtils.remove(s, "+").trim());
		
	}
}
