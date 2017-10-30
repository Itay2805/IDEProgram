package me.itay.idemodthingy.programs.bluej.api;

import java.util.List;

import me.itay.idemodthingy.programs.bluej.Project;
import me.itay.idemodthingy.programs.bluej.ProjectFile;

public interface SyntaxHighlighter {
	
	public String getName();
	public List<List<Token>> parse(Project project, ProjectFile currentFile);
	public List<Problem> getProblems(ProjectFile file);
	public void reset();
	
}
