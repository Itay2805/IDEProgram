package com.example.examplemod;

import java.awt.Color;

public class JSLanguage implements IDELanguage {

	@Override
	public int getKeywordColor(String text) {
		if(text.equals("var")) {
			return Color.BLUE.getRGB();
		}else if(text.equals("function")) {
			return Color.BLUE.getRGB();
		}else {			
			return Color.WHITE.getRGB();
		}
	}

}
