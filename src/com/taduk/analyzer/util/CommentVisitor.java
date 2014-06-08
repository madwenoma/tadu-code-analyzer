package com.taduk.analyzer.util;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
/**
 * 
 * @author LIFE
 *
 */
public class CommentVisitor extends ASTVisitor {

	CompilationUnit compilationUnit;

	private String[] source;

	private TypeLine commentLine;
	
	public CommentVisitor(CompilationUnit compilationUnit, String[] source) {

		super();
		this.compilationUnit = compilationUnit;
		this.source = source;
	}

	public boolean visit(LineComment node) {
		int startLineNumber = compilationUnit.getLineNumber(node.getStartPosition()) - 1;
		String lineComment = source[startLineNumber].trim();
		System.out.println(lineComment);
		commentLine = new TypeLine(lineComment, startLineNumber, startLineNumber);
		return true;
	}

	@Override
	public boolean visit(Javadoc node) {
		int startLineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		int endLineNumber = compilationUnit.getLineNumber(node.getStartPosition() + node.getLength());
		commentLine = new TypeLine(node.toString(), startLineNumber, endLineNumber);
		return super.visit(node);
	}

	public boolean visit(BlockComment node) {
		int startLineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		int endLineNumber = compilationUnit.getLineNumber(node.getStartPosition() + node.getLength());

		StringBuffer blockComment = new StringBuffer();
		for (int lineCount = startLineNumber; lineCount <= endLineNumber; lineCount++) {
			String blockCommentLine = source[lineCount].trim();
			blockComment.append(blockCommentLine);
			if (lineCount != endLineNumber) {
				blockComment.append("\n");
			}
		}
		commentLine = new TypeLine(blockComment.toString(), startLineNumber, endLineNumber);
		System.out.println(blockComment.toString());
		return true;
	}

	public void preVisit(ASTNode node) {

	}

	public TypeLine getCommentLine() {
		return commentLine;
	}
	
}