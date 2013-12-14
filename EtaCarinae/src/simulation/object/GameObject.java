package simulation.object;

import java.util.ArrayList;
import java.util.Hashtable;

import renderer.scene.ObjectView;
import renderer.scene.SceneNode;
import simulation.ui.UIObject;
import util.collision.Collidable;
import util.math.MathBox;

public abstract class GameObject implements UIObject, ObjectView, Interactable, Collidable {
	protected Hashtable<String, SceneNode> sceneNodes = new Hashtable<String, SceneNode>();
	
	protected GameObject parent;
	protected float[] zDepths;
	protected int diameter;
	protected String[] texturePaths; //TODO replace with hashtable so we can name this shit
	protected int[] textureLengths;
	
	private float[] position;
	private boolean positionRelative;
	
	protected float[] rotation;
	
	protected boolean alive = true;
	protected boolean highlighted;
	protected boolean selected;
	
	public GameObject(GameObject parent, float[] position, boolean positionRelative, float[] rotation, float[] zDepths, int diameter, String[] texturePaths, int[] textureLengths) {
		if (position.length != 2) {
			throw new IllegalArgumentException("Illegal position length.");
		}

		this.parent = parent;
		this.position = position;
		this.positionRelative = positionRelative;
		this.rotation = rotation;
		this.zDepths = zDepths;
		this.diameter = diameter;
		this.texturePaths = texturePaths;
		this.textureLengths = textureLengths;
	}

	public float[] getPosition() {
		if (parent != null) {
			if (positionRelative) {
				return MathBox.addVectors(parent.getPosition(), MathBox.rotatePoint(position, parent.getRotationAngle()));
			}
			return MathBox.addVectors(parent.getPosition(), position); 
		}
		return position;
	}
	
	public void adjustRelativePosition(float[] adjustment) {
		position = MathBox.addVectors(position, adjustment);
	}
	
	public float getRotationAngle() {
		return MathBox.vectorToAngle(rotation) - MathBox.vectorToAngle(new float[] {1.0f, 0.0f});
	}

	public float[] getRotation() {
		return rotation;
	}
	
	public void setRotation(float[] rotation) {
		this.rotation = rotation;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	@Override
	public ArrayList<SceneNode> getView() {
		return new ArrayList<SceneNode>(sceneNodes.values());
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	
	@Override
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}
	
	@Override
	public boolean isHighlighted() {
		return highlighted;
	}
	
	public int getDiameter() {
		return diameter;
	}
}