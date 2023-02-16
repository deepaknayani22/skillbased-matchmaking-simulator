package Component.MultiplayerMatchmaker;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {
	protected int id;
	protected int skill; // Between 1 and 9 (including 1 and 9)
	protected double patience; // in units of simulation time
	protected double joinTime; // in units of simulation time

	public Player(int id, int skill, double patience) {
		super();
		this.id = id;
		this.skill = skill;
		this.patience = patience;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Player [id=").append(id).append(", skill=").append(skill).append(", patience=")
				.append(String.format("%.2f", patience)).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	public static Player fromString(String str) {
	    Pattern pattern = Pattern.compile("^Player \\[id=(.+), skill=(.+), patience=(.+)\\]$");
	    Matcher matcher = pattern.matcher(str);
	    if(matcher.find()) {
	    	Player player = new Player(0,0,0);
	    	player.id = Integer.parseInt(matcher.group(1));
	    	player.skill = Integer.parseInt(matcher.group(2));
	    	player.patience = Double.parseDouble(matcher.group(3));
	    	return player;
	    } else {
	    	System.out.println("Unable to parse Player information from entity string \"" + str + "\"");
	    	return null;
	    }
	}
}
