package me.itay.idemodthingy.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import me.itay.idemodthingy.api.IDELanguageHighlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;

public class IDETextArea extends Component {
	
	private static final int PADDING = 2;

	private int width, height;
	private List<String> lines = new ArrayList<>();
	private int updateCounter = 0;
	private int cursorX = 0;
	private int cursorY = 0;
	private int lastX = 0;
	private int lastY = 0;
	
	private int backgroundColour = Color.DARK_GRAY.getRGB();
	private int borderColour = Color.BLACK.getRGB();
	private IDELanguageHighlight language;
	private FontRenderer font;
	
	public IDETextArea(int left, int top, int width, int height, IDELanguageHighlight language) {
		super(left, top);
		this.width = width;
		this.height = height;
		this.language = language;
		
		lines.add("");
		
		font = Minecraft.getMinecraft().fontRendererObj;
	}
	
	@Override
	public void handleTick() {
		updateCounter++;
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
		GlStateManager.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, borderColour);
		Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, backgroundColour);
		
		int currentY = 0;
		int from = cursorY - (height / font.FONT_HEIGHT);
		if(from < 0) {
			from = 0;
		}
		int to = from + (height / font.FONT_HEIGHT) + 2;
		if(to > lines.size()) {
			to = lines.size();
		}
		for(int i = from; i < to; i++) {
			String text = lines.get(i);
			String[] textToRender = language.tokenize(text);
			int currentX = 0;
			for(String word : textToRender) {
				if(word.length() == 0) continue;
				if(currentX + font.getStringWidth(word) >= width) {
					currentX = 0;
					currentY += font.FONT_HEIGHT;
				}
				font.drawSplitString(word + " ", xPosition + PADDING + 1 + currentX, yPosition + PADDING + 2 + currentY, width - PADDING * 2 - 2, language.getKeywordColor(word));
				currentX += font.getStringWidth(word);
			}
			currentY += font.FONT_HEIGHT;
		}
		
		
		if(updateCounter / 2 % 6 == 0 || lastX != cursorX || lastY != cursorY) {
			int cursorX = font.getStringWidth(getCurrentLine().substring(0, this.cursorX));
			int cursorY = (this.cursorY - from) * font.FONT_HEIGHT;
			font.drawSplitString("_", xPosition + PADDING + 1 + cursorX, yPosition + PADDING + 2 + cursorY, width - PADDING * 2 - 2, Color.WHITE.getRGB());
		}
		lastX = cursorX;
		lastY = cursorY;
	}
	
	@Override
	public void handleKeyTyped(char character, int code) {
		int index = cursorX + cursorY * (width / font.getCharWidth('_'));
		switch(code)  {
			case Keyboard.KEY_BACK:
				if(cursorY == 0 && getCurrentLine().isEmpty()) break;
				if(getCurrentLine().isEmpty()) {
					lines.remove(cursorY);
					cursorY--;
					cursorX = getCurrentLine().length();
				}else {
					String newLine = getCurrentLine().substring(0, cursorX - 1) + getCurrentLine().substring(cursorX);
					lines.set(cursorY, newLine);
					cursorX--;
				}
				break;				
			case Keyboard.KEY_NUMPADENTER:
			case Keyboard.KEY_RETURN:
				if(lines.size() - 1 > cursorY) {
					cursorY++;
					lines.add(cursorY, "");
				}else {
					lines.add("");
				}
				cursorY++;
				cursorX = 0;
				break;
			case Keyboard.KEY_TAB:
				lines.set(cursorY, getCurrentLine().substring(0, cursorX) + "    " + getCurrentLine().substring(cursorX));
				cursorX += 4;
				break;
			case Keyboard.KEY_RIGHT:
				{
					boolean hasNewLine = cursorY + 1 < lines.size();
					boolean hasMoreSpace = cursorX + 1 < getCurrentLine().length();
					if(hasMoreSpace) {					
						cursorX++;
					}else if(hasNewLine) {
						cursorX = 0;
						cursorY++;
					}
					break;	
				}					
			case Keyboard.KEY_LEFT:
				{
					boolean hasNewLine = cursorY - 1 >= 0;
					boolean hasMoreSpace = cursorX - 1 >= 0;
					if(hasMoreSpace) {					
						cursorX--;
					}else if(hasNewLine) {
						cursorY--;
						cursorX = getCurrentLine().length();
					}
					break;					
				}
			default:
				if(ChatAllowedCharacters.isAllowedCharacter(character)) {
					lines.set(cursorY, getCurrentLine().substring(0, cursorX) + character + getCurrentLine().substring(cursorX));
					cursorX++;
				}
				break;
		}
	}
	
	public void setLanguage(IDELanguageHighlight language) {
		this.language = language;
	}
	
	private String getCurrentLine() {
		return lines.get(cursorY);
	}
	
	public List<String> getLines() {
		return lines;
	}
	
	public String getText() {
		StringJoiner joiner = new StringJoiner("\n");
		for(String line : lines) {
			joiner.add(line);
		}
		return joiner.toString();
	}
	
	public void setText(String text) {
		String[] lines = text.split("\n");
		this.lines = new ArrayList<>();
		for(String line : lines) {
			this.lines.add(line);
		}
		cursorX = 0;
		cursorY = 0;
	}
	
	public int getCursorX() {
		return cursorX;
	}
	
	public int getCursorY() {
		return cursorY;
	}
	
	public void setCursorX(int cursorX) {
		this.cursorX = cursorX;
	}
	
	public void setCursorY(int cursorY) {
		this.cursorY = cursorY;
	}

}
