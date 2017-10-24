package me.itay.idemodthingy.opengl;

import static me.itay.idemodthingy.opengl.GL.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * 
 * Software OpenGL 1.1 context.
 * 
 * @author Itay Almog
 *
 */
public class GLContext {
	
	private int w;
	private int h;
	private Vector4f[] bufColor;
	private float[] bufDepth;
	
	private Matrix4f matModelView;
	private Matrix4f matProj;
	private Matrix4f curMatrix;
	
	private int err;
	
	private Vector4f bufColorClear;
	private float bufDepthClear;
	
	private int beginMode;
	private Vector4f beginColor;
	private Vector2f beginTexCoord;
	
	private List<Texture> textures;
	private int curTexture;
	
	private boolean depthEnabled;
	private boolean cullingEnabled;
	private boolean textureEnabled;
	
	private List<Vertex> beginVertices = new ArrayList<>();
	
	/**
	 * Create a new instance of software OpenGL 1.1 context
	 * @param w Width of the frame buffer.
	 * @param h Height of the frame buffer.
	 */
	public GLContext(int w, int h) {
		
		// Initialize buffers
		this.w = w;
		this.h = h;
		this.bufColor = new Vector4f[w * h];
		this.bufDepth = new float[w * h];
		
		// Initialize matrix stack
		this.matModelView = new Matrix4f();
		this.matProj = new Matrix4f();
		this.curMatrix = this.matModelView;
		
		// Set error
		this.err = NO_ERROR;
		
		// Initialize rest of the state
		this.bufColorClear = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
		this.bufDepthClear = 1.0f;
		
		this.beginMode = -1;
		this.beginColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.beginTexCoord = new Vector2f(0.0f, 0.0f);
		
		this.textures = new ArrayList<Texture>();
		this.curTexture = 0;
		
		this.depthEnabled = false;
		this.cullingEnabled = false;
		this.textureEnabled = false;
		
		for(int i = 0; i < w * h; i++) {
			bufColor[i] = new Vector4f(bufColorClear);
		}
	}
	
	public String getString(int string) {
		switch(string) {
			case VENDOR: 		return "Itay Almog";
			case RENDERER: 		return "Software based (Java)";
			case VERSION: 		return "OpenGL 1.1 JVM";
			case EXTENSIONS: 	return "EXT_BGR";
			default:
				this.err = INVALID_ENUM;
				return "";
		}
	}
	
	/**
	 * Enables OpenGL capabilities
	 */
	public void enable(int capability) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		switch(capability) {
			case DEPTH_TEST: this.depthEnabled = true; break;
			case CULL_FACE: this.cullingEnabled = true; break;
			case TEXTURE_2D: this.textureEnabled = true; break;
			default:
				this.err = INVALID_ENUM;
		}
	}
	
	/**
	 * Disabled OpenGL capabilities
	 */
	public void disable(int capability) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		switch(capability) {
			case DEPTH_TEST: this.depthEnabled = false; break;
			case CULL_FACE: this.cullingEnabled = false; break;
			case TEXTURE_2D: this.textureEnabled = false; break;
			default:
				this.err = INVALID_ENUM;
		}
	}
	
	/**
	 * Returns any errors caused by the last function call.
	 */
	public int getError() {
		if(beginMode != -1) return 0;
		return this.err;
	}
	
	/**
	 * Sets the color buffer clear color
	 * 
	 * @param r Red color component
	 * @param g Green color component
	 * @param b Blue color component
	 * @param a Alpha component
	 */
	public void clearColor(float r, float g, float b, float a) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		this.bufColorClear.set(fclamp(r), fclamp(g), fclamp(b), fclamp(a));
	}
	
	/**
	 * Sets the depth buffer clear color
	 */
	public void clearDepth(float depth) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		this.bufDepthClear = depth;
	}
	
	/**
	 * Clears one or more buffers.
	 */
	public void clear(int mask) {
		if(mask - COLOR_BUFFER_BIT - DEPTH_BUFFER_BIT > 0) {
			this.err = INVALID_VALUE;
			return;
		}
		
		if((mask & COLOR_BUFFER_BIT) != 0) {
			int size = w * h;
			for(int i = 0; i < size; i++) {
				this.bufColor[i].set(bufColorClear);
			}
		}
		if((mask & DEPTH_BUFFER_BIT) != 0) {
			int size = w * h;
			for(int i = 0; i < size; i++) {
				this.bufDepth[i] = bufDepthClear;
			}
		}
	}
	
	/**
	 * Start specification of vertices belonging to a group of primitives.
	 */
	public void begin(int mode) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		switch(mode) {
			case POINTS:
			case LINES:
			case TRIANGLES:
			case QUADS:
				this.beginMode = mode;
				this.beginVertices.clear();
				break;
			default:
				this.err = INVALID_ENUM;
				return;
		}
	}
	
	/**
	 * Finish specification of vertices;
	 */
	public void end() {
		if(this.beginMode == -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		// Transform vertices and map them to window coordinates
		for(int i = 0; i < beginVertices.size(); i++) {
			Vertex vertex = beginVertices.get(i);

			// Transform vertex into eye space
			Vector4f vec = new Vector4f(vertex.coord.x, vertex.coord.y, vertex.coord.z, 1.0f);
			vec.mul(matModelView);
			
			// Perform backface culling - if enabled
			if ( this.cullingEnabled && ( ( this.beginMode == GL.QUADS && i % 4 == 0 ) || ( this.beginMode == GL.TRIANGLES && i % 3 == 0 ) ) ) {
				Vector3f side1 = new Vector3f();
				beginVertices.get(i).coord.sub(beginVertices.get(i + 1).coord, side1);
				Vector3f side2 = new Vector3f();
				beginVertices.get(i).coord.sub(beginVertices.get(i + 2).coord, side2);
				Vector3f normal = new Vector3f();
				side1.cross(side2, normal);
				float dot = normal.dot(beginVertices.get(i).coord);
				vertex.render = dot <= 0; // If dot > 0, then the face is facing away from the camera
			}
			
			// Transform vertex into clip space
			vec.mul(matProj);
			
			// Calculate normalized device coordinates
			Vector3f norm = new Vector3f();
			norm.x = vec.x / vec.w;
			norm.y = vec.y / vec.w;
			norm.z = vec.z / vec.w;
			
			vertex.coord.x = (norm.x + 1) / 2 * this.w;
			vertex.coord.y = (1 - norm.y) / 2 * this.h;
			vertex.coord.z = norm.z;
			
			// beginVertices.set(i, vertex);
		}
		
		// Assemble primitives
		if(beginMode == POINTS) {
			
		}else if(beginMode == LINES && beginVertices.size() >= 2) {
			
		}else if(beginMode == TRIANGLES && beginVertices.size() >= 3) {
			for(int i = 0; i < this.beginVertices.size(); i += 3) {
				drawTriangle(new Vertex[]{
						beginVertices.get(i + 0),
						beginVertices.get(i + 1),
						beginVertices.get(i + 2),
				}, this.bufColor, this.bufDepth, this);
			}
		}else if(beginMode == QUADS && beginVertices.size() >= 4) {
			for(int i = 0; i < this.beginVertices.size(); i += 4) {
				drawQuad(new Vertex[]{
						beginVertices.get(i + 0),
						beginVertices.get(i + 1),
						beginVertices.get(i + 2),
						beginVertices.get(i + 3),
				}, this.bufColor, this.bufDepth, this);
			}
		}
		
		this.beginMode = -1;
		this.beginVertices.clear();
	}
	
	/**
	 * Set the current color
	 */
	public void color4f(float r, float g, float b, float a) {
		beginColor.set(fclamp(r), fclamp(g), fclamp(b), fclamp(a));
	}
	

	/**
	 * Set the current color
	 */
	public void color3f(float r, float g, float b) {
		beginColor.set(fclamp(r), fclamp(g), fclamp(b), 1.0f);
	}
	
	/**
	 * Specify a 2D texture coordinate
	 */
	public void texCoord2f(float u, float v) {
		this.beginTexCoord.set(u, v);
	}
	
	/**
	 * Specify a vertex
	 */
	public void vertex3f(float x, float y, float z) {
		if(this.beginMode == -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		beginVertices.add(new Vertex(x, y, z, beginColor, beginTexCoord));
	}
	
	/**
	 * Specify a vertex
	 */
	public void vertex2f(float x, float y) {
		if(this.beginMode == -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		beginVertices.add(new Vertex(x, y, 0, beginColor, beginTexCoord));
	}
	
	/**
	 * Specify the matrix to apply operations on
	 */
	public void matrixMode(int mode) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		switch(mode) {
			case MODELVIEW:
				this.curMatrix = this.matModelView;
				break;
			case PROJECTION:
				this.curMatrix = this.matProj;
				break;
			default:
				this.err = INVALID_ENUM;
				return;
		}
	}
	
	/**
	 * Replaces the current matrix with the identity matrix.
	 */
	public void loadIdentity() {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		curMatrix.identity();
	}
	
	/**
	 * Multiplies the current matrix by the translation matrix
	 */
	public void translatef(float x, float y, float z) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		this.curMatrix.translate(x, y, z);
	}
	
	/**
	 * Multiplies the current matrix by a scale matrix
	 */
	public void scalef(float x, float y, float z) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}

		this.curMatrix.scale(x, y, z);
	}
	
	/**
	 * Multiplies the current matrix by a rotation matrix
	 */
	public void rotatef(float angle, float x, float y, float z) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		this.curMatrix.rotate(angle, x, y, z);
	}
	
	/**
	 * Sets up a perspective projection matrix.
	 */
	public void perspective(float fovy, float aspect, float near, float far) {
		this.curMatrix.perspective(fovy, aspect, near, far);
	}
	
	/**
	 * Defines a viewing transformation.
	 */
	public void lookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
		this.curMatrix.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
	}
	
	private final int[] buf = new int[1];
	
	/**
	 * Generate texture object
	 */
	public int genTextures() {
		genTextures(1, buf);
		return buf[0];
	}
	
	/**
	 * Generate texture object
	 */
	public void genTextures(int count, int[] buf) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		if(count < 0) {
			this.err = INVALID_VALUE;
			return;
		}
		
		for(int i = 0; i < count; i++) {
			int j = this.textures.size() + 1;
			buf[i] = j;
			this.textures.add(new Texture());
		}
	}
	
	/**
	 * Make a texture current
	 */
	public void bindTexture(int target, int id) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		switch(target) {
			case TEXTURE_2D:
				this.curTexture = id - 1;
				break;
			default:
				this.err = INVALID_ENUM;
				return;
		}
	}
	
	/**
	 * Specify the pixels for a texture image
	 */
	public void texImage2D(int target, int width, int height, int type, ByteBuffer data) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		if(target != TEXTURE_2D) {
			this.err = INVALID_ENUM;
			return;
		}
		
		if(type != BYTE && type != FLOAT) {
			this.err = INVALID_ENUM;
			return;
		}
		
		Texture texture = textures.get(curTexture);
		texture.pixels = new Vector4f[width * height];
		texture.w = width;
		texture.h = height;
		
		int size = width * height;
		for(int i = 0; i < size; i++) {
			texture.pixels[i] = new Vector4f();
			texture.pixels[i].x = (type == BYTE ? data.get(i * 4 + 0) / 255 : data.getFloat((i * 4 + 0) * 4) / 1.0f);
			texture.pixels[i].y = (type == BYTE ? data.get(i * 4 + 1) / 255 : data.getFloat((i * 4 + 1) * 4) / 1.0f);
			texture.pixels[i].z = (type == BYTE ? data.get(i * 4 + 2) / 255 : data.getFloat((i * 4 + 2) * 4) / 1.0f);
			texture.pixels[i].w = (type == BYTE ? data.get(i * 4 + 3) / 255 : data.getFloat((i * 4 + 3) * 4) / 1.0f);
		}
	}
	
	/**
	 * 
	 * Read pixels from a buffer and return them as an array
	 * 
	 * @param x Window coordinates of the area you want to capture.
	 * @param y Window coordinates of the area you want to capture.
	 * @param w Size of the area you want to capture.
	 * @param h Size of the area you want to capture.
	 */
	public void readPixels(int x, int y, int w, int h, int format, int type, ByteBuffer data) {
		if(this.beginMode != -1) {
			this.err = INVALID_OPERATION;
			return;
		}
		
		if ( ( format != EXT_BGR && format != RGBA && format != RGB && format != DEPTH_COMPONENT ) || ( type != BYTE && type != FLOAT ) ) {
			this.err = INVALID_ENUM;
			return;
		}
		
		if(w < 0 || h < 0) {
			this.err = INVALID_VALUE;
			return;
		}
		
		if(format == RGBA) {
			int size = this.w * this.h;
			for(int i = 0; i < size; i++) {
				if(type == BYTE) {
					data.put(i * 4 + 0, (byte)(bufColor[i].x * 255));
					data.put(i * 4 + 1, (byte)(bufColor[i].y * 255));
					data.put(i * 4 + 2, (byte)(bufColor[i].z * 255));
					data.put(i * 4 + 3, (byte)(bufColor[i].w * 255));
				}else if(type == FLOAT) {
					data.putFloat((i * 4 + 0) * 4, bufColor[i].x);
					data.putFloat((i * 4 + 1) * 4, bufColor[i].y);
					data.putFloat((i * 4 + 2) * 4, bufColor[i].z);
					data.putFloat((i * 4 + 3) * 4, bufColor[i].w);
				}
			}
		}else if(format == RGB) {
			int size = this.w * this.h;
			for(int i = 0; i < size; i++) {
				if(type == BYTE) {
					data.put(i * 3 + 0, (byte)(bufColor[i].x * 255));
					data.put(i * 3 + 1, (byte)(bufColor[i].y * 255));
					data.put(i * 3 + 2, (byte)(bufColor[i].z * 255));
				}else if(type == FLOAT) {
					data.putFloat((i * 3 + 0) * 4, bufColor[i].x);
					data.putFloat((i * 3 + 1) * 4, bufColor[i].y);
					data.putFloat((i * 3 + 2) * 4, bufColor[i].z);
				}
			}
		}else if(format == EXT_BGR) {
			int size = this.w * this.h;
			for(int i = 0; i < size; i++) {
				if(type == BYTE) {
					data.put(i * 3 + 0, (byte)(bufColor[i].z * 255));
					data.put(i * 3 + 1, (byte)(bufColor[i].y * 255));
					data.put(i * 3 + 2, (byte)(bufColor[i].x * 255));
				}else if(type == FLOAT) {
					data.putFloat((i * 3 + 0) * 4, bufColor[i].z);
					data.putFloat((i * 3 + 1) * 4, bufColor[i].y);
					data.putFloat((i * 3 + 2) * 4, bufColor[i].x);
				}
			}
		}else if(format == DEPTH_COMPONENT) {
			int size = this.w * this.h;
			for(int i = 0; i < size; i++) {
				if(type == BYTE) {
					data.put(i + 0, (byte)(bufDepth[i] * 255));
				}else if(type == FLOAT) {
					data.putFloat(i + 0, bufDepth[i]);
				}
			}
		}
	}
	
	public static void drawTriangle(Vertex[] p, Vector4f[] color, float[] depth, GLContext gl) {
		if(gl.cullingEnabled && !p[0].render) return;
		
		int x1 = (int)Math.floor(p[0].coord.x);
		int x2 = (int)Math.floor(p[1].coord.x);
		int x3 = (int)Math.floor(p[2].coord.x);
		int y1 = (int)Math.floor(p[0].coord.y);
		int y2 = (int)Math.floor(p[1].coord.y);
		int y3 = (int)Math.floor(p[2].coord.y);
		
		int minX = (int) min(x1, x2, x3);
		int minY = (int) min(y1, y2, y3);
		int maxX = (int) max(x1, x2, x3);
		int maxY = (int) max(y1, y2, y3);
		
		float factor = 1.0f / ( (y2-y3)*(x1-x3) + (x3-x2)*(y1-y3) );
		
		int o = 0;
		for(int x = minX; x <= maxX; x++) {
			for(int y = minY; y <= maxY; y++) {
				float ic0 = ( (y2-y3)*(x-x3)+(x3-x2)*(y-y3) ) * factor;
				if ( ic0 < 0 || ic0 > 1 ) continue;
				float ic1 = ( (y3-y1)*(x-x3)+(x1-x3)*(y-y3) ) * factor;
				if ( ic1 < 0 || ic1 > 1 ) continue;
				float ic2 = 1.0f - ic0 - ic1;
				if ( ic2 < 0 || ic2 > 1 ) continue;
				
				o = (x + y * gl.w);
				if(o >= color.length || o < 0) {
					continue;
				}
				
				float z = 1 / ( ic0*1/p[0].coord.z + ic1*1/p[1].coord.z + ic2*1/p[2].coord.z );
				if(gl.depthEnabled) {
					if(z > depth[o]) continue;
					else depth[o] = z;
				}
				
				// Vertex color
				Vector4f fragColor = new Vector4f();
//				fragColor.x = ( ic0*p[0].color.x/p[0].coord.z + ic1*p[1].color.x/p[1].coord.z + ic2*p[2].color.x/p[2].coord.z ) * z;
//				fragColor.y = ( ic0*p[0].color.y/p[0].coord.z + ic1*p[1].color.y/p[1].coord.z + ic2*p[2].color.y/p[2].coord.z ) * z;
//				fragColor.z = ( ic0*p[0].color.z/p[0].coord.z + ic1*p[1].color.z/p[1].coord.z + ic2*p[2].color.z/p[2].coord.z ) * z;
//				fragColor.w = ( ic0*p[0].color.w/p[0].coord.z + ic1*p[1].color.w/p[1].coord.z + ic2*p[2].color.w/p[2].coord.z ) * z;
				fragColor.x = ( ic0*p[0].color.x + ic1*p[1].color.x + ic2*p[2].color.x );
				fragColor.y = ( ic0*p[0].color.y + ic1*p[1].color.y + ic2*p[2].color.y );
				fragColor.z = ( ic0*p[0].color.z + ic1*p[1].color.z + ic2*p[2].color.z );
				fragColor.w = ( ic0*p[0].color.w + ic1*p[1].color.w + ic2*p[2].color.w );
				
				
				// Texture sample
				if(gl.textureEnabled && gl.curTexture < gl.textures.size()) {
					Texture tex = gl.textures.get(gl.curTexture);
					float u = ( ic0*p[0].texCoord.x/p[0].coord.z + ic1*p[1].texCoord.x/p[1].coord.z + ic2*p[2].texCoord.x/p[2].coord.z ) * z;
					float v = ( ic0*p[0].texCoord.y/p[0].coord.z + ic1*p[1].texCoord.y/p[1].coord.z + ic2*p[2].texCoord.y/p[2].coord.z ) * z;
					u = (float) (Math.floor(u * tex.w) % tex.w); // This behaviour should later depend on GL_TEXTURE_WRAP_S
					v = (float) (Math.floor(v * tex.h) % tex.h);
					
					int to = (int)(u + v * tex.w);
					fragColor.x *= tex.pixels[to].x;
					fragColor.y *= tex.pixels[to].y;
					fragColor.z *= tex.pixels[to].z;
					fragColor.w *= tex.pixels[to].w;
				}
				
				color[o].set(fragColor);
			}
		}
	}
	
	private static void drawQuad(Vertex[] p, Vector4f[] color, float[] depth, GLContext gl) {
		if(gl.cullingEnabled) {
			if(!p[0].render) return;
			p[2].render = true; // Or drawTriangle will think the second triangle making the quad is culled
		}
		
		drawTriangle(new Vertex[] { p[0], p[1], p[2] }, color, depth, gl);
		drawTriangle(new Vertex[] { p[2], p[3], p[0] }, color, depth, gl);
	}
	
	private static float max(float a, float b, float c) {
		if(a >= b && a >= c) return a;
		else if(b >= a && b >= c) return b;
		else if(c >= a && c >= b) return c;
		return -1;
	}
	
	private static float min(float a, float b, float c) {
		if(a <= b && a <= c) return a;
		else if(b <= a && b <= c) return b;
		else if(c <= a && c <= b) return c;
		return -1;
	}
	
	/**
	 * Clamps a value between =n 0.0 and 1.0
	 */
	private static float fclamp(float val) {
		return val < 0.0f ? 0.0f : (val > 1.0f ? 1.0f : val);
	}
	
}