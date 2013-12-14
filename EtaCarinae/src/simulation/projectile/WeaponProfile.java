package simulation.projectile;

import simulation.enums.ZDepth;
import util.math.MathBox;

public class WeaponProfile {
	public static final WeaponProfile DEFAULT = new WeaponProfile(new String[] {"square_box_2.png"}, new int[] {10}, new float[] {ZDepth.SUB_SYSTEM.getDepth()}, 10, 300, 3, MathBox.PI/4);
	
	private final String[] texturePaths;
	private final int[] textureLengths;
	
	private final float[] zDepths;
	
	private int diameter;
	
	private final int weaponRange;
	private final int fireInterval;
	private final float arcLength;
	
	private WeaponProfile(String[] texturePaths, int[] textureLengths, float[] zDepths, int diameter, int weaponRange, int fireInterval, float arcLength) {
		this.texturePaths = texturePaths;
		this.textureLengths = textureLengths;
		this.zDepths = zDepths;
		this.diameter = diameter;
		this.weaponRange = weaponRange;
		this.fireInterval = fireInterval;
		this.arcLength = arcLength;
	}
	
	public String[] getTexturePaths() {
		return texturePaths;
	}

	public int[] getTextureLengths() {
		return textureLengths;
	}

	public float[] getZDepths() {
		return zDepths;
	}
	
	public int getDiameter() {
		return diameter;
	}
	
	public int getWeaponRange() {
		return weaponRange;
	}
	
	public int getFireInterval() {
		return fireInterval;
	}
	
	public float getArcLength() {
		return arcLength;
	}
}
