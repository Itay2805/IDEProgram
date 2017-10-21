package me.itay.idemodthingy.opengl;
/**
 * 
 * Constants
 * 
 * @author Itay Almog
 *
 */
public class GL {
	
	// Begin modes
	public static final int POINTS				= 0x0000;
	public static final int LINES				= 0x0001;
	public static final int TRIANGLES			= 0x0004;
	public static final int QUADS				= 0x0007;
	
	// Errors
	public static final int NO_ERROR			= 0x0000;
	public static final int INVALID_ENUM		= 0x0500;
	public static final int INVALID_VALUE		= 0x0501;
	public static final int INVALID_OPERATION	= 0x0502;
	
	// Features
	public static final int DEPTH_TEST			= 0x0B71;
	public static final int CULL_FACE			= 0x0B44;
	public static final int TEXTURE_2D			= 0x0DE1;
	
	// Types
	public static final int BYTE				= 0x1400;
	public static final int FLOAT				= 0x1406;
	
	// Matrix modes
	public static final int MODELVIEW			= 0x1700;
	public static final int PROJECTION 		= 0x1707;
	
	// Pixel formats
	public static final int DEPTH_COMPONENT 	= 0x1902;
	public static final int RGB 				= 0x1907;
	public static final int RGBA 				= 0x1908;
	// Pixel formats extensions
	public static final int EXT_BGR				= 0x2000;
	
	// Buffers
	public static final int DEPTH_BUFFER_BIT 	= 0x0100;
	public static final int COLOR_BUFFER_BIT 	= 0x4000;
	
	// Strings
	public static final int VENDOR 				= 0;
	public static final int RENDERER 			= 1;
	public static final int VERSION 			= 2;
	public static final int EXTENSIONS 			= 3;
	
}