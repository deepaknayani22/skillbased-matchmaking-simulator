package Component.MultiplayerMatchmaker;

import java.util.*;

public class PlayerArrayUtil {
	protected static final String separator = "|";
	
	public static String toString(ArrayList<Player> list) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < list.size(); ++i) {
			builder.append(list.get(i).toString());
			if (i < list.size() - 1) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}
	
	public static ArrayList<Player> fromString(String str) {
		ArrayList<Player> list = new ArrayList<Player>();
		StringTokenizer multiTokenizer = new StringTokenizer(str, separator);
        while (multiTokenizer.hasMoreTokens()) {
        	String s = multiTokenizer.nextToken();
            list.add(Player.fromString(s));
        }
		return list;
	}
}
