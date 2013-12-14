package simulation.projectile;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3bc;

import renderer.objdata.OBJData;
import renderer.scene.VBOSceneNode;
import renderer.shader.ShaderProgram;
import renderer.vbo.VBOManager;
import simulation.object.GameObject;
import simulation.object.PhysicalProfile;
import simulation.ships.Ship;
import util.collision.Circle;
import util.collision.Shape;
import util.math.MathBox;
import util.timer.TimerManager;

public class Weapon extends GameObject {
	private WeaponProfile weaponProfile;
	private ProjectileProfile projectileProfile;
	
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	private int lastFireTime;

	public Weapon(GameObject parent, float[] position, float[] rotation, WeaponProfile weaponProfile, ProjectileProfile projectileProfile) {
		super(parent, position, true, rotation, weaponProfile.getZDepths(), weaponProfile.getDiameter(), weaponProfile.getTexturePaths(), weaponProfile.getTextureLengths());

		this.weaponProfile = weaponProfile;
		this.projectileProfile = projectileProfile;
	}

	public void fire(float[] targetPosition) {
		int currentTime = TimerManager.getTimer("mainTimer").getTime();
		if (currentTime >= lastFireTime + weaponProfile.getFireInterval()) {
			//TODO determine if it is possible to hit targetPosition given weapon position, targetPosition, arcStart, arcLength and weaponRange
			float targetAngle = MathBox.vectorToAngle(MathBox.subtractVectors(targetPosition, getPosition()));
			float weaponAngle = getRotationAngle();
			
			if (targetAngle >= weaponAngle - weaponProfile.getArcLength()/2 && targetAngle <= weaponAngle + weaponProfile.getArcLength()/2) {
				float[] velocity = MathBox.addVectors(((Ship) parent).getVelocity(), MathBox.scalarVector(MathBox.normaliseVector(((Ship) parent).getRotation()), projectileProfile.getMuzzleVelocity()));
				projectiles.add(new Projectile(getPosition(), velocity, ((Ship) parent).getRotation(), PhysicalProfile.DUMB_PROJECTILE, projectileProfile, (int) (weaponProfile.getWeaponRange())));
				
				lastFireTime = currentTime;
			}
		}
	}
	
	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}

	public WeaponProfile getWeaponProfile() {
		return weaponProfile;
	}
	
	@Override
	public ArrayList<String> getUIStrings() {
		ArrayList<String> uiStrings = new ArrayList<String>();
		
		uiStrings.add("Range: " + weaponProfile.getWeaponRange());
		uiStrings.add("Arc Length: " + weaponProfile.getArcLength());
		
		return uiStrings;
	}

	@Override
	public void updateView() {
		if (sceneNodes.isEmpty()) {
			ShaderProgram shaderProgram = new ShaderProgram(
					"defaultTextured", 
					"textured.vertex", 
					"textured.fragment", 
					new String[] {"attribute_Position", "attribute_TexCoord"}
			);
			
			for (int i = 0; i < texturePaths.length; i++) {
				FloatBuffer vertexBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
				OBJData objData = new OBJData(vertexBuffer, texturePaths[i], new float[] {textureLengths[i]/2, textureLengths[i]/2, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
				VBOSceneNode weaponSceneNode = new VBOSceneNode(shaderProgram, objData, getPosition(), 0.0f, zDepths[i], GL3bc.GL_TRIANGLES);
				weaponSceneNode.setVisible(true);
				sceneNodes.put(this + texturePaths[i] + "weaponNode", weaponSceneNode);
			}

			FloatBuffer arcBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");			
			OBJData arcObjData = new OBJData(arcBuffer, "less_shit_arc.png", new float[] {(2 * MathBox.sin(weaponProfile.getArcLength()/2)) * weaponProfile.getWeaponRange(), weaponProfile.getWeaponRange()/2, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
			
			float[] arcNodePosition =  MathBox.addVectors(getPosition(), MathBox.scalarVector(rotation, weaponProfile.getWeaponRange()/2));
			VBOSceneNode arcSceneNode = new VBOSceneNode(shaderProgram, arcObjData, arcNodePosition, getRotationAngle(), zDepths[0], GL3bc.GL_TRIANGLES);
			sceneNodes.put("arcNode", arcSceneNode);
		}
		else {
			for (int i = 0; i < texturePaths.length; i++) {
				sceneNodes.get(this + texturePaths[i] + "weaponNode").setNodePosition(getPosition());
				((VBOSceneNode) sceneNodes.get(this + texturePaths[i] + "weaponNode")).setNodeRotation(getRotationAngle());
			}
			
			sceneNodes.get("arcNode").setNodePosition(MathBox.addVectors(getPosition(), MathBox.scalarVector(rotation, weaponProfile.getWeaponRange()/2)));
			((VBOSceneNode) sceneNodes.get("arcNode")).setNodeRotation(getRotationAngle());
			
			if (parent.isSelected() || parent.isHighlighted()) {
				sceneNodes.get("arcNode").setVisible(true);
			}
			else {
				sceneNodes.get("arcNode").setVisible(false);
			}
		}
	}

	@Override
	public Shape getBounds() {
		return new Circle(getPosition(), diameter/2);
	}
}
