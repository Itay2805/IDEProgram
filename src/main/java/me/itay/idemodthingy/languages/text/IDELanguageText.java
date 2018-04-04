package me.itay.idemodthingy.languages.text;

import java.util.List;

import me.itay.idemodthingy.programs.bluej.Project;
import me.itay.idemodthingy.programs.bluej.ProjectFile;
import me.itay.idemodthingy.programs.bluej.api.Problem;
import me.itay.idemodthingy.programs.bluej.api.SyntaxHighlighter;
import me.itay.idemodthingy.programs.bluej.api.tokens.Token;

public class IDELanguageText implements SyntaxHighlighter {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public List<Token> parse(Project project, ProjectFile currentFile) {
		return null;
	}

	@Override
	public List<Problem> getProblems(ProjectFile file) {
		return null;
	}

	@Override
	public void reset() {
		
	}
	
}
