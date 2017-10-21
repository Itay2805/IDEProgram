package me.itay.idemodthingy.components;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import me.itay.idemodthingy.opengl.GL;
import me.itay.idemodthingy.opengl.GLContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GLCanvas extends Component {
	
	private GLContext gl;
	private int width, height;
	private BufferedImage image;
	private ResourceLocation rc;
	
	public GLCanvas(int left, int top, int width, int height) {
		super(left, top);
		
		gl = new GLContext(width, height);
		
		this.width = width;
		this.height = height;
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	}
	
	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive,
			float partialTicks) {
		ByteBuffer buffer = ByteBuffer.allocate(width * height * 3);
		gl.readPixels(0, 0, width, height, GL.RGB, GL.BYTE, buffer);
		image.getRaster().setDataElements(0, 0, width, height, buffer.array());

		// BufferedImage rotated = rotate(image, 180);

		rc = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("opengl11_context_image", new DynamicTexture(image));
		
		mc.getRenderManager().renderEngine.bindTexture(rc);
		drawRectWithFullTexture(x, y, 0, 0, width, height);
	}
	
	public GLContext getGl() {
		return gl;
	}
	
	protected static void drawRectWithFullTexture(double x, double y, float u, float v, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, 0).tex(0, 1).endVertex();
		buffer.pos(x + width, y + height, 0).tex(1, 1).endVertex();
		buffer.pos(x + width, y, 0).tex(1, 0).endVertex();
		buffer.pos(x, y, 0).tex(0, 0).endVertex();
		tessellator.draw();
	}
	
}
