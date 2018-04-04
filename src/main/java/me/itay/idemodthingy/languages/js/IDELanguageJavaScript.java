package me.itay.idemodthingy.languages.js;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import me.itay.idemodthingy.languages.js.tokens.JSDefault;
import me.itay.idemodthingy.languages.js.tokens.JSKeywordGroup;
import me.itay.idemodthingy.programs.bluej.Project;
import me.itay.idemodthingy.programs.bluej.ProjectFile;
import me.itay.idemodthingy.programs.bluej.api.Problem;
import me.itay.idemodthingy.programs.bluej.api.SyntaxHighlighter;
import me.itay.idemodthingy.programs.bluej.api.tokens.DynamicToken;
import me.itay.idemodthingy.programs.bluej.api.tokens.Token;

import static me.itay.idemodthingy.api.IDELanguageHighlight.*;

public class IDELanguageJavaScript implements SyntaxHighlighter {

	private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
	private static final String[] DELIMITERS = { "\\s", "\\p{Punct}", "\\p{Digit}+"  };
	
	private static final String DELIMITER;
	static {
		StringJoiner joiner = new StringJoiner("|");
		for(String delim : DELIMITERS) {
			joiner.add(delim);
		}
		DELIMITER = joiner.toString();
	}

	@Override
	public String getName() {
		return "javascript";
	}

    private String lastToken = "";
    private boolean quote = false;

	@Override
	public List<Token> parse(Project project, ProjectFile currentFile) {
	    List<Token> ret = new ArrayList<>();
		String text = currentFile.getCode();
		if(text.length() == 0) {
			return ret;
		}
		String[] tokens = text.split(DELIMITER);
		StringBuilder sb = new StringBuilder();
		for(String token : tokens) {
            if(text.contains("\"")) {
                if(quote) {
                    quote = false;
                    ret.add(new DynamicToken(sb.toString()));
                    continue;
                }
                quote = true;
            }
            if(quote){
                sb.append(token);
                continue;
            }
            if(JSKeywordGroup.getTokens().contains(token)){
                ret.add(new JSKeywordGroup());
                continue;
            }
        
            if(lastToken.equals("function")){
                ret.add(new DynamicToken(text));
            }
            lastToken = text;
        }
		return ret;
	}

	@Override
	public List<Problem> getProblems(ProjectFile file) {
		return null;
	}

	@Override
	public void reset() {
		quote = false;
	}
	
}
