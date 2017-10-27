package me.itay.idemodthingy.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;

import me.itay.idemodthingy.api.IDELanguageHighlight;
import me.itay.idemodthingy.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
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
	private int from = 0;
	private int lineCount;
	private Timer timer;
	
	private List<Character> newScopeChars = new ArrayList<>();
	private List<Character> closeScopeChars = new ArrayList<>();
	
	private List<ErrorHighlight> errors = new ArrayList<>();
	private int errorLength;
	
	private boolean editable = true;
	
	private int backgroundColour = Color.DARK_GRAY.getRGB();
	private int borderColour = Color.BLACK.getRGB();
	private IDELanguageHighlight language;
	private FontRenderer font;
	
	public class ErrorHighlight {
		private int color = Color.RED.getRGB();
		private int line = 0, column = 0, length = 0;
		private String error = "Error";
	}
	
	public IDETextArea(int left, int top, int width, int height, IDELanguageHighlight language) {
		super(left, top);
		timer = new Timer();
		
		newScopeChars.add('{');
		closeScopeChars.add('}');
		
		this.width = width;
		this.height = height;
		this.language = language;
		
		lines.add("");
		
		font = Minecraft.getMinecraft().fontRendererObj;
		
		lineCount = height / font.FONT_HEIGHT;
		
		errorLength = Minecraft.getMinecraft().fontRendererObj.getStringWidth("~");
	}
	
	public void addError(ErrorHighlight highlight) {
		errors.add(highlight);
	}
	
	public void clearErrors() {
		errors.clear();
	}
	
	@Override
	public void handleTick() {
		if(timer.elapsed() >= 0.5) {
			timer.reset();
			language.errorCheck(this, getText());
		}
		updateCounter++;
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
		GlStateManager.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, borderColour);
		Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, backgroundColour);
		
		int to = from + lineCount;
		if(to >= lines.size()) {
			to = lines.size();
		}
		
		int currentY = 0;
		for(int i = from; i < to; i++) {
			String text = lines.get(i);
			String[] textToRender = language.tokenize(text.replace("\t", "    "));
			int currentX = 0;
			for(String word : textToRender) {
				if(word.length() == 0) continue;
//				if(currentX + font.getStringWidth(word) >= width) {
//					currentX = 0;
//					currentY += font.FONT_HEIGHT;
//				}

				font.drawSplitString(word + " ", xPosition + PADDING + 1 + currentX, yPosition + PADDING + 2 + currentY, width - PADDING * 2 - 2, language.getKeywordColor(word));
				currentX += font.getStringWidth(word);
			}
			currentY += font.FONT_HEIGHT;
		}
		
		if(editable) {
			if(updateCounter / 2 % 6 == 0 || lastX != cursorX || lastY != cursorY) {
				int toX = this.cursorX;
				if(toX > getCurrentLine().length()) {
					toX = getCurrentLine().length();
				}
				int cursorX = font.getStringWidth(getCurrentLine().substring(0, toX));
				int cursorY = (this.cursorY - from) * font.FONT_HEIGHT;
				font.drawSplitString("|", xPosition + PADDING + 1 + cursorX, yPosition + PADDING + 2 + cursorY, width - PADDING * 2 - 2, Color.WHITE.getRGB());
			}
			lastX = cursorX;
			lastY = cursorY;
		}
		
		for(ErrorHighlight err : errors) {
			int X = font.getStringWidth(getCurrentLine().replace("\t", "    ").substring(0, err.column));
			int Y = (err.line - from) * font.FONT_HEIGHT + font.FONT_HEIGHT;
			String finale = "";
			int length = font.getStringWidth(getCurrentLine().substring(err.column, err.column + err.length));
			for(int i = 0; i <= (length / errorLength); i++) {
				finale += "~";
			}
			font.drawSplitString(finale, xPosition + PADDING + 1 + X, yPosition + PADDING + 2 + Y, width - PADDING * 2 - 2, err.color);
			if(RenderUtil.isMouseInside(mouseX - xPosition, mouseY - yPosition, X, Y - font.FONT_HEIGHT / 2, X + length, Y + font.FONT_HEIGHT / 2)) {
				Gui.drawRect(mouseX, mouseY - (font.FONT_HEIGHT + 4), mouseX + font.getStringWidth(err.error) + 4, mouseY, borderColour);
				Gui.drawRect(mouseX + 1, mouseY - (font.FONT_HEIGHT + 4) + 1, mouseX + font.getStringWidth(err.error) + 4 - 1, mouseY - 1, backgroundColour);
				font.drawString(err.error, mouseX + 2, mouseY - (font.FONT_HEIGHT + 4) + 2, Color.WHITE.getRGB(), true);
			}
		}
		
		language.reset();
	}
	
	private int scope = 0;
	
	@Override
	public void handleKeyTyped(char character, int code) {
		if(!editable) return;
		
		if(GuiScreen.isKeyComboCtrlV(code)) {
			String[] clipLines = GuiScreen.getClipboardString().replace("\r", "").replace("\r", "    ").split("\n");
			for(String line : clipLines) {
				lines.add(cursorY, line);
				cursorY++;
			}
			
			checkDown();
			return;
		}
		
		switch(code)  {
			case Keyboard.KEY_BACK:
				if(cursorY == 0 && cursorX == 0) break;
				if(getCurrentLine().isEmpty()) {
					lines.remove(cursorY);
					cursorY--;
					cursorX = getCurrentLine().length();
				}else {
					if(cursorX == 0) {
						String oldLine = getCurrentLine();
						lines.remove(cursorY);
						cursorY--;
						cursorX = getCurrentLine().length();
						lines.set(cursorY, getCurrentLine() + oldLine);
					}else {
						String newLine = getCurrentLine().substring(0, cursorX - 1) + getCurrentLine().substring(cursorX);
						lines.set(cursorY, newLine);
						cursorX--;						
					}
				}
				checkUp();
				break;				
			case Keyboard.KEY_NUMPADENTER:
			case Keyboard.KEY_RETURN:
				String oldLine = getCurrentLine().substring(0, cursorX);
				String newLine = getCurrentLine().substring(cursorX);
				cursorX = 0;
				for(int i = 0; i < scope; i++) {
					newLine = "    " + newLine;
					cursorX += 4;
				}
				lines.set(cursorY, oldLine);
				cursorY++;
				lines.add(cursorY, newLine);
				checkDown();
				break;
			case Keyboard.KEY_TAB:
				lines.set(cursorY, getCurrentLine().substring(0, cursorX) + "\t" + getCurrentLine().substring(cursorX));
				cursorX += 4;
				break;
			case Keyboard.KEY_UP: 
				{
					if(cursorY - 1 >= 0) {
						cursorY--;
						if(getCurrentLine().length() < cursorX) {							
							cursorX = getCurrentLine().length();
						}
					}
					
					checkUp();
				}
				break;
			case Keyboard.KEY_DOWN: 
				{
					if(cursorY + 1 < lines.size()) {
						cursorY++;
						if(getCurrentLine().length() < cursorX) {							
							cursorX = getCurrentLine().length();
						}
					}
					
					checkDown();
				}
				break;
			case Keyboard.KEY_RIGHT:
				{
					boolean hasNewLine = cursorY + 1 < lines.size();
					boolean hasMoreSpace = cursorX + 1 <= getCurrentLine().length();
					if(hasMoreSpace) {					
						cursorX++;
					}else if(hasNewLine) {
						cursorX = 0;
						cursorY++;
					}
				
					checkDown();
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
					
					checkUp();
					break;	
				}
			default:
				if(ChatAllowedCharacters.isAllowedCharacter(character)) {
					String toAdd = "" + character;
					for(Character c : newScopeChars) {						
						if(c.equals(character)) {
							scope++;
							break;
						}
					}
					for(Character c : closeScopeChars) {						
						if(c.equals(character)) {
							scope--;
							break;
						}
					}
					if(character == '{') {
						toAdd += "}";						
					}else if(character == '(') {
						toAdd += ")";
					}else if(character == '[') {
						toAdd += "]";
					}
					lines.set(cursorY, getCurrentLine().substring(0, cursorX) + toAdd + getCurrentLine().substring(cursorX));
					cursorX++;
				}
				break;
		}
	}
	
	private void checkDown() {
		if(cursorY >= lineCount + from) {
			from = cursorY;
		}
	}
	
	private void checkUp() {
		if(cursorY < from) {
			from = cursorY;
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
		
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
