package me.itay.idemodthingy.programs.bluej.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

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
	
	private int cursorX, cursorY, cursorColor = Color.WHITE.getRGB();
	private int backgroundColor = 0x1E1E1E;

	private SyntaxHighlighter highlighter;
	private Project project;
	private ProjectFile currentFile;
	private List<Token> parsed;
	private List<Problem> errors;
	private boolean textChanged = false;
	private Timer errorTimer = new Timer();
	
	private boolean editable;
	
	public BlueJCodeEditor(int left, int top, int width, int height, SyntaxHighlighter highlighter) {
		super(left, top);
		
		this.width = width;
		this.height = height;
		this.highlighter = highlighter;
		
		errorTimer.reset();
		
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		
		maxLines = (int) Math.floor((height - 4) / font.FONT_HEIGHT + 1);
	}
	
	
	@Override
	protected void handleTick() {
		if(highlighter != null && project != null && currentFile != null) {
			// get errors half a second after finished typing
			if(errorTimer.elapsed() >= 0.5) {
				errors = highlighter.getProblems(currentFile);
				errorTimer.reset();
			}
			
			if(textChanged) {
				parsed = highlighter.parse(project, currentFile);
				textChanged = false;
			}
		}
	}
	
	@Override
	protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
		if(!visible) return;
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, new Color(0x1E1E1E).getRGB());
		
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		
		if(highlighter == null) {
			for(int i = from; i < from + maxLines; i++) {
				if(i >= lines.size()) {
					break;
				}
				int Y = i * (font.FONT_HEIGHT + 1);
				font.drawString(lines.get(i), 2 + xPosition, 2 + yPosition + Y, Color.WHITE.getRGB());
			}
		}else {
//			font.drawString(getCurrentLine(), xPosition + 2, yPosition + 2, text)
		}
	}
	
	@Override
	protected void handleKeyTyped(char character, int code) {
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
		case Keyboard.KEY_TAB: write("\t"); break;
		case Keyboard.KEY_RETURN: write("\n"); break;
		default: write(character + ""); break;
		}
	}
	
	public BlueJCodeEditor write(String text) {
		if(!editable) return this;
		
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

		cursorX = getCurrentLine().length();

		return this;
	}
	
	private String getCurrentLine() {
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
