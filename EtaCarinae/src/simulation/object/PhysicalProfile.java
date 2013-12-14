package simulation.object;

import util.math.MathBox;

public class PhysicalProfile {
	public static final PhysicalProfile DEFAULT = new PhysicalProfile(50.0f, 2.0f, 25.0f, 0.25f, 5.25f, 50.0f);
	public static final PhysicalProfile DUMB_PROJECTILE = new PhysicalProfile(Float.MAX_VALUE, 0.0f, 1.0f, 0.0f, 0.0f, Float.MAX_VALUE);
	
	private final float maxVelocity;
	private final float stoppingDistance;
	private final float mass;
	private final float engineForce;
	private final float engineTurningForce;
	private final float maxTurningVelocity;
	
	private PhysicalProfile(float maxVelocity, float stoppingDistance, float mass, float engineForce, float engineTurningForce, float maxTurningVelocity) {
		this.maxVelocity = maxVelocity;
		this.stoppingDistance = stoppingDistance;
		this.mass = MathBox.max(1.0f, mass); //must have at least 1.0f mass
		this.engineForce = engineForce;
		this.engineTurningForce = engineTurningForce;
		this.maxTurningVelocity = maxTurningVelocity;
	}
	
	public float getMaxVelocity() {
		return maxVelocity;
	}
	
	public float getStoppingDistance() {
		return stoppingDistance;
	}
	
	public float getMass() {
		return mass;
	}
	
	public float getEngineForce() {
		return engineForce;
	}
	
	public float getEngineTurningForce() {
		return engineTurningForce;
	}
	
	public float getMaxTurningVelocity() {
		return maxTurningVelocity;
	}
}