package simulation.environment;

import simulation.object.GameObject;

public abstract class Celestial extends GameObject {
	protected String name;
	
	public Celestial(GameObject parent, float[] position, float[] rotation, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, String name) {
		super(parent, position, false, rotation, zDepths, diameter, texturePaths, textureSizes);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
