package simulation.ships;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3bc;

import renderer.objdata.OBJData;
import renderer.scene.SceneNode;
import renderer.scene.VBOSceneNode;
import renderer.shader.ShaderProgram;
import renderer.vbo.VBOManager;
import simulation.ai.agent.PhysicalAgent;
import simulation.ai.command.AgentCommand;
import simulation.ai.command.AgentCommand.CommandType;
import simulation.ai.command.AttackCommand;
import simulation.ai.command.MoveToCommand;
import simulation.enums.ZDepth;
import simulation.object.GameObject;
import simulation.object.PhysicalObject;
import simulation.object.PhysicalProfile;
import simulation.projectile.Weapon;
import util.collision.Circle;
import util.collision.Shape;
import util.math.MathBox;

public abstract class Ship extends PhysicalObject implements PhysicalAgent {
	private ArrayList<GameObject> generatedObjects = new ArrayList<GameObject>();
	
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	
	private AgentCommand agentCommand;
	
	protected ShipProfile shipSpecification;
	
	public Ship(float[] position, float[] rotation, float[] zDepths, int diameter, String[] texturePaths, int[] textureSizes, float[] velocity, PhysicalProfile physicalSpecification, ShipProfile shipSpecification) {
		super(null, position, false, rotation, zDepths, diameter, texturePaths, textureSizes, velocity, physicalSpecification);

		this.shipSpecification = shipSpecification;
	}

	public ShipProfile getShipSpecification() {
		return shipSpecification;
	}
	
	public void addWeapon(Weapon weapon) {
		weapons.add(weapon);
	}
	
	@Override
	public void update() {
		if (agentCommand != null) {
			if (agentCommand.getCommandType() == CommandType.MOVE_TO_COMMAND) {
				moveTo(((MoveToCommand) agentCommand).getDestination(), ((MoveToCommand) agentCommand).getOrientation());
			}
			else if (agentCommand.getCommandType() == CommandType.ATTACK_COMMAND) {
				PhysicalObject target = ((AttackCommand) agentCommand).getTarget();
				
				float[] destinationVector = MathBox.subtractVectors(target.getPosition(), getPosition());
				float destinationMagnitude = MathBox.getVectorMagnitude(destinationVector);
				
				float weaponRange = Float.MAX_VALUE;
				for (Weapon weapon : weapons) {
					if (weapon.getWeaponProfile().getWeaponRange() < weaponRange) {
						weaponRange = weapon.getWeaponProfile().getWeaponRange(); 
					}
				}
				
				float[] attackDestination = null;
				if (destinationMagnitude > weaponRange) {
					attackDestination = MathBox.scalarVector(MathBox.normaliseVector(destinationVector), weaponRange);
				}
				else {
					attackDestination = getPosition();
				}
				
				moveTo(attackDestination, destinationVector);
				
				for (Weapon weapon : weapons) {
					weapon.fire(target.getPosition());
					generatedObjects.addAll(weapon.getProjectiles());
					weapon.getProjectiles().clear();
				}
			}
		}
		
		for (Weapon weapon : weapons) {
			weapon.setRotation(rotation);
		}
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

				VBOSceneNode shipSceneNode = new VBOSceneNode(shaderProgram, objData, getPosition(), getRotationAngle(), zDepths[i], GL3bc.GL_TRIANGLES);

				shipSceneNode.setVisible(true);
				
				sceneNodes.put(this + texturePaths[i] + "_ShipSceneNode", shipSceneNode);
			}
			
			FloatBuffer selectionBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
			OBJData selectionObjData = new OBJData(selectionBuffer, "selection_ring_green.png", new float[] {diameter * 0.75f, diameter * 0.75f, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
			sceneNodes.put("selectionNode", new VBOSceneNode(shaderProgram, selectionObjData, getPosition(), 0.0f, ZDepth.SELECTION.getDepth(), GL3bc.GL_TRIANGLES));

			
			FloatBuffer planBuffer = VBOManager.getFloatBuffer("DefaultTexturedQuad");
			OBJData plannedObjData = new OBJData(planBuffer, texturePaths[0], new float[] {textureLengths[0]/2, textureLengths[0]/2, 1.0f}, 2, 5, new int[] {3, 2}, new int[] {0, 3}, false, true);
			sceneNodes.put("planNode", new VBOSceneNode(shaderProgram, plannedObjData, plannedDestination, 0.0f, ZDepth.SHIP.getDepth(), GL3bc.GL_TRIANGLES));
			
			for (int i = 0; i < weapons.size(); i++) {
				weapons.get(i).updateView();
				ArrayList<SceneNode> weaponSceneNodes = weapons.get(i).getView();
				for (int j = 0; j < weaponSceneNodes.size(); j++) {
					sceneNodes.put("weaponNode" + i + " " + j, weaponSceneNodes.get(j));
				}
			}
			
		}
		else {
			for (int i = 0; i < texturePaths.length; i++) {
				sceneNodes.get(this + texturePaths[i] + "_ShipSceneNode").setNodePosition(getPosition());
				((VBOSceneNode) sceneNodes.get(this + texturePaths[i] + "_ShipSceneNode")).setNodeRotation(getRotationAngle());
			}
			
			sceneNodes.get("selectionNode").setNodePosition(getPosition());
			((VBOSceneNode) sceneNodes.get("selectionNode")).setNodeRotation(getRotationAngle());
			
			for (int i = 0; i < weapons.size(); i++) {
				weapons.get(i).updateView();
			}
			
			if (selected || highlighted) {
				sceneNodes.get("selectionNode").setVisible(true);
			}
			else {
				sceneNodes.get("selectionNode").setVisible(false);
			}
			
			if (planned) {
				sceneNodes.get("planNode").setVisible(true);				
				sceneNodes.get("planNode").setNodePosition(plannedDestination);
				((VBOSceneNode) sceneNodes.get("planNode")).setNodeRotation(MathBox.vectorToAngle(plannedOrientation));
			}
			else {
				sceneNodes.get("planNode").setVisible(false);
			}
		}
	}
	
	@Override
	public ArrayList<GameObject> getGeneratedObjects() {
		return generatedObjects;
	}
	
	@Override
	public void clearGeneratedObjects() {
		generatedObjects.clear();
	}
	
	@Override
	public Shape getBounds() {
		return new Circle(getPosition(), diameter/2);
	}
	
	@Override
	public void addInput(AgentCommand agentCommand) {
		this.agentCommand = agentCommand;
	}
}
