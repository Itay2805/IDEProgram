package me.itay.idemodthingy.programs.bluej.api;

import java.util.List;

import me.itay.idemodthingy.programs.bluej.Project;
import me.itay.idemodthingy.programs.bluej.ProjectFile;
import me.itay.idemodthingy.programs.bluej.api.tokens.Token;

public interface SyntaxHighlighter {
	
	String getName();
	List<Token> parse(Project project, ProjectFile currentFile);
	List<Problem> getProblems(ProjectFile file);
	void reset();
	
}
