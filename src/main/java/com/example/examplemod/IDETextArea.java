package com.example.examplemod;

import java.awt.Color;
import java.awt.event.KeyEvent;

import org.lwjgl.input.Keyboard;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import scala.collection.parallel.ParIterableLike.Min;

public class IDETextArea extends Component {
	
	private static final int PADDING = 2;

	private int width, height;
	private String text = "var i = 4;\nvar b = 0;";
	private int updateCounter = 0;
	private int cursorIndex = 0;
	
	private int backgroundColour = Color.DARK_GRAY.getRGB();
	private int borderColour = Color.BLACK.getRGB();
	private IDELanguage language;
	private FontRenderer font;
	
	public IDETextArea(int left, int top, int width, int height, IDELanguage language) {
		super(left, top);
		this.width = width;
		this.height = height;
		this.language = language;
		
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
		
		char[] arr = text.toCharArray();
		String[] textToRender = new String(arr).split(" ");
		int currentX = 0;
		for(String word : textToRender) {
			font.drawSplitString(word + " ", xPosition + PADDING + 1 + currentX, yPosition + PADDING + 2, width - PADDING * 2 - 2, language.getKeywordColor(word));
			currentX += font.getStringWidth(word + " ");
		}
		
		if(updateCounter / 2 % 6 == 0) {			
			font.drawSplitString("_", xPosition + PADDING + 1 + cursorIndex, yPosition + PADDING + 2, width - PADDING * 2 - 2, Color.WHITE.getRGB());
		}
	}
	
	@Override
	public void handleKeyTyped(char character, int code) {
		switch(code)  {
			case Keyboard.KEY_BACK:
				if(cursorIndex <= 0 || cursorIndex > text.length()) {
					return;
				}
				text = text.substring(0, cursorIndex - 1) + text.substring(cursorIndex);
				cursorIndex--;
				break;
			case Keyboard.KEY_DELETE:
				System.out.println("DELETE");
				if(cursorIndex < 0 || cursorIndex >= text.length()) {
					return;
				}
				text = text.substring(0, cursorIndex) + text.substring(cursorIndex + 1);
				cursorIndex--;
				break;					
			case Keyboard.KEY_NUMPADENTER:
				text += "\n";
				break;
			default:
				if(ChatAllowedCharacters.isAllowedCharacter(character)) {
					text = text.substring(0, cursorIndex) + character + text.substring(cursorIndex);					
					cursorIndex++;
				}
				break;
		}
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

}
