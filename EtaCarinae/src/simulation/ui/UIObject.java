package simulation.ui;

import java.util.ArrayList;

public interface UIObject {
	//TODO implementation of this method should not construct a new arraylist each tick, faster if they can just change what is required.
	public ArrayList<String> getUIStrings();
}
