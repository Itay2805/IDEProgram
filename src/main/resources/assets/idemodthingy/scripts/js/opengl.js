function OpenGL(x, y, width, height) {
	this.handle = runtime.createGLCanvas(x, y, width, height);
	var glcontext = this.handle.getGl();
	this.getString = function(string) { return glcontext.getString(string); }
	this.enable = function(capability) { return glcontext.enable(capability); }
	this.disable = function(capability) { return glcontext.disable(capability); }
	this.getError = function() { return glcontext.getError(); }
	this.clearColor = function(r, g, b, a) { return glcontext.clearColor(r, g, b, a); }
	this.clearDepth = function(depth) { return glcontext.clearDepth(depth); }
	this.clear = function(mask) { return glcontext.clear(mask); }
	this.begin = function(mode) { return glcontext.begin(mode); }
	this.end = function() { return glcontext.end(); }
	this.color4f = function(r, g, b, a) { return glcontext.color4f(r, g, b, a); }
	this.color3f = function(r, g, b) { return glcontext.color3f(r, g, b); }
	this.texCoord2f = function(u, v) { return glcontext.texCoord2f(u, v); }
	this.vertex2f = function(x, y) { return glcontext.vertex2f(x, y); }
	this.vertex3f = function(x, y, z) { return glcontext.vertex3f(x, y, z); }
	this.matrixMode = function(mode) { return glcontext.matrixMode(mode); }
	this.loadIdentity = function() { return glcontext.loadIdentity(); }
	this.translatef = function(x, y, z) { return glcontext.translatef(x, y, z); }
	this.scalef = function(x, y, z) { return glcontext.scalef(x, y, z); }
	this.rotatef = function(angle, x, y, z) { return glcontext.rotatef(angle, x, y, z); }
	this.perspective = function(fovy, aspect, near, far) { return glcontext.getString(fovy, aspect, near, far); }
	this.lookAt = function(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ) { return glcontext.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ); }
	// texture support will be added soon...
//	this.genTextures = function() { return glcontext.genTextures(); }
//	this.bindTexture = function(target, id) { return glcontext.bindTexture(target, id); }
//	 this.texImage2D = function(target, width, height, type, data) { return runtime.gl_texImage2D(this.handle, target, width, height, type, data); }
}

var GL = {
	POINTS: 0x0000,
	LINES: 0x0001,
	TRIANGLES: 0x0004,
	QUADS: 0x0007,
	
	NO_ERROR: 0x0000,
	INVALID_ENUM: 0x0500,
	INVALID_VALUE: 0x0501,
	INVALID_OPERATION: 0x0502,
	
	DEPTH_TEST: 0x0B71,
	CULL_FACE: 0x0B44,
	TEXTURE_2D: 0x0DE1,
	
	BYTE: 0x1400,
	FLOAT: 0x1406,
	
	MODELVIEW: 0x1700,
	PROJECTION: 0x1707,
	
	DEPTH_COMPONENT: 0x1902,
	RGB: 0x1907,
	RGBA: 0x1908,
	EXT_BGR: 0x2000,
	
	DEPTH_BUFFER_BIT: 0x0100,
	COLOR_BUFFER_BIT: 0x4000,
	
	VENDOR: 0,
	RENDERER: 1,
	VERSION: 2,
	EXTENSIONS: 3,
	
};