package me.itay.idemodthingy.opengl;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Vertex {
	
	public Vector3f coord;
	public Vector2f texCoord;
	public Vector4f color;
	public boolean cull;

	public Vertex(float x, float y, float z, Vector4f color, Vector2f texCoord) {
		this.coord = new Vector3f(x, y, z);
		this.texCoord = new Vector2f(texCoord);
		this.color = new Vector4f(color);
		this.cull = false;
	}

	@Override
	public String toString() {
		return "Vertex [coord=" + coord + ", texCoord=" + texCoord + ", color=" + color + ", render=" + cull + "]";
	}
	
}