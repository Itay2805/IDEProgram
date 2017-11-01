package me.itay.idemodthingy.programs.bluej.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;

import me.itay.idemodthingy.programs.bluej.Project;
import me.itay.idemodthingy.programs.bluej.ProjectFile;
import me.itay.idemodthingy.programs.bluej.api.Problem;
import me.itay.idemodthingy.programs.bluej.api.SyntaxHighlighter;
import me.itay.idemodthingy.programs.bluej.api.Token;
import me.itay.idemodthingy.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class BlueJCodeEditor extends Component {

	private int width, height;
	private List<String> lines = new ArrayList<>();
	private int from;
	private int maxLines;
	
	private int cursorX, cursorY, cursorColor = Color.LIGHT_GRAY.getRGB();
	private int backgroundColor = new Color(0x1E1E1E).getRGB();
	private Timer cursorTimer = new Timer();
	private boolean cursorState = false;

	private SyntaxHighlighter highlighter;
	private Project project;
	private ProjectFile currentFile;
	private List<List<Token>> parsed;
	private List<Problem> problems;
	private int errorLength;
	private boolean textChanged = false;
	private Timer errorTimer = new Timer();
	
	private boolean editable;
	
	public BlueJCodeEditor(int left, int top, int width, int height, SyntaxHighlighter highlighter) {
		super(left, top);
		
		this.width = width;
		this.height = height;
		this.highlighter = highlighter;
		
		errorTimer.reset();
		cursorTimer.reset();
		
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		
		maxLines = (int) Math.floor((height - 4) / font.FONT_HEIGHT + 1);
		errorLength = (int)(font.getCharWidth('~'));
	}
	
	
	@Override
	protected void handleTick() {
		if(highlighter != null && project != null && currentFile != null) {
			// get errors half a second after finished typing
			if(errorTimer.elapsed() >= 0.5) {
				problems = highlighter.getProblems(currentFile);
				errorTimer.reset();
			}
			
			if(textChanged) {
				parsed = highlighter.parse(project, currentFile);
				textChanged = false;
			}
		}
		
		if(cursorTimer.elapsed() > 0.25) {
			cursorTimer.reset();
			cursorState = !cursorState;
		}
	}
	
	@Override
	protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
		if(!visible) return;
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, backgroundColor);
		
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		
		// render text
		if(highlighter == null) {
			// no highlighter
			for(int i = from; i < from + maxLines; i++) {
				if(i >= lines.size()) {
					break;
				}
				int Y = i * font.FONT_HEIGHT;
				font.drawString(lines.get(i).replace("\t", "    "), 2 + xPosition, 2 + yPosition + Y, Color.WHITE.getRGB());
			}
		}else {
			// with highlight
			for(int i = from; i < from + maxLines; i++) {
				if(i >= lines.size()) {
					break;
				}
				List<Token> tokens = parsed.get(i);
				int Y = i * font.FONT_HEIGHT;
				int X = 0;
				for(Token token : tokens) {
					String text = token.getToken().replace("\t", "    ");
					font.drawString(text, 2 + xPosition + X, 2 + yPosition + Y, token.getColor());
					X += font.getStringWidth(text);
				}
			}
		}
		
		if(editable) {
			// render cursor
			if(cursorState) {
				int X = font.getStringWidth(getCurrentLine().substring(0, cursorX).replace("\t", "   "));
				int Y = (cursorY - from) * font.FONT_HEIGHT;
				font.drawString("|", 2 + xPosition + X, 2 + yPosition + Y, cursorColor);
			}
		}
		
		// render errors
		if(highlighter != null) {
			for(Problem problem : problems) {
				if(from >= problem.getLine() || from + maxLines < problem.getLine()) {
					continue;
				}
				int X = font.getStringWidth(lines.get(problem.getLine()).substring(0, problem.getColumn()));
				int Y = problem.getLine() * font.FONT_HEIGHT * 2;
				int length = problem.getLength();
				while((length--) > 0) {
					font.drawString("~", 2 + xPosition + X, 2 + yPosition + Y, problem.getColor());
					X += errorLength;
				}
			}
		}
	}
	
	@Override
	protected void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if(!GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + height)) return;
		
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		int X = mouseX - xPosition;
		int Y = (mouseY - yPosition) / font.FONT_HEIGHT;
		if(Y >= lines.size()) Y = lines.size();
		if(Y < 0) Y = 0;
		if(Y >= lines.size()) return;
		cursorY = Y;
		
		String line = getCurrentLine();
		int i = 0;
		for(char c : line.toCharArray()) {
			X -= font.getStringWidth((c + "").replace("\t", "    "));
			if(X <= 0) break;
			i++;
		}
		cursorX = i;
		
		System.out.println(cursorX);
		System.out.println(cursorY);
	}
	
	@Override
	protected void handleKeyTyped(char character, int code) {
		cursorTimer.reset();
		cursorState = true;
		
		switch(code) {
		case Keyboard.KEY_BACK:
			{
				if(cursorY == 0 && cursorX == 0) return;
				String line = getCurrentLine();
				String before = "", after = "";
				if(cursorX < line.length()) after = line.substring(cursorX);
				if(cursorX > 0) before = line.substring(0, cursorX - 1);
				lines.set(cursorY, before + after);
				cursorX--;
				if(cursorX < 0) {
					lines.remove(cursorY);
					cursorY--;
					cursorX = getCurrentLine().length();
				}
			}
			break;
		case Keyboard.KEY_LEFT:
			{
				cursorX--;
				if(cursorX < 0) {
					cursorY--;
					if(cursorY < 0) {
						cursorY = 0;
						cursorX = 0;
					}else {
						cursorX = getCurrentLine().length();
					}
				}
			}
			break;
		case Keyboard.KEY_RIGHT:
			{
				cursorX++;
				if(cursorX > getCurrentLine().length()) {
					cursorY++;
					if(cursorY >= lines.size()) {
						cursorY = lines.size() - 1;
						if(cursorY < 0) cursorY = 0;
					}
					cursorX = getCurrentLine().length();
					if(cursorX < 0) cursorX = 0;
				}
			}
			break;
		case Keyboard.KEY_TAB: write("\t"); break;
		case Keyboard.KEY_RETURN: write("\n"); break;
		default: write(character + ""); break;
		}
	}
	
	public BlueJCodeEditor write(String text) {
		if(!editable) return this;
		if(!text.matches("[\\n\\t\\x20-\\x7E]")) return this;
		
		errorTimer.reset();
		textChanged = true;
		
		// process
		String[] lines = text.replace("\r", "").split("\n", -1);
		String line = getCurrentLine();
		String before = "", after = "";
		if(cursorX < line.length()) after = line.substring(cursorX);
		if(cursorX > 0) before = line.substring(0, cursorX);
		
		// set first line
		String firstLine = before + lines[0];
		this.lines.set(cursorY, firstLine);
		
		if(lines.length > 1) {
			cursorY++;

			// add all lines
			for(int i = 1; i < lines.length - 1; i++) {
				line = lines[i];
				this.lines.add(cursorY, line);
				cursorY++;
			}
			
			// set last line
			this.lines.add(cursorY, lines[lines.length - 1] + after);
		}

		cursorX = getCurrentLine().length() - after.length();
		if(cursorX < 0) cursorX = 0;

		return this;
	}
	
	private String getCurrentLine() {
		if(cursorY >= lines.size()) {
			return "";
		}
		return lines.get(cursorY);
	}
	
	public String getText() {
		StringJoiner joiner = new StringJoiner("\n");
		for(String line : lines) {
			joiner.add(line);
		}
		return joiner.toString();
	}
	
	public BlueJCodeEditor setText(String text) {
		lines.clear();
		for(String line : text.replace("\r", "").split("\n")) {
			lines.add(line);
		}
		this.cursorX = 0;
		this.cursorY = 0;
		return this;
	}
	
	public void setIDEState(Project project, String currentFile) {
		this.project = project;
		this.currentFile = project.getFile(currentFile);
	}
	
	public SyntaxHighlighter getHighlighter() {
		return highlighter;
	}
	
	public void setHighlighter(SyntaxHighlighter highlighter) {
		this.highlighter = highlighter;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getTop() {
		return top;
	}
	
	public int getLeft() {
		return left;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public BlueJCodeEditor setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}
	
	public BlueJCodeEditor setHeight(int height) {
		this.height = height;
		return this;
	}
	
	public BlueJCodeEditor setTop(int top) {
		this.top = top;
		return this;
	}
	
	public BlueJCodeEditor setLeft(int left) {
		this.left = left;
		return this;
	}
	
	public BlueJCodeEditor setWidth(int width) {
		this.width = width;
		return this;
	}
	
}
