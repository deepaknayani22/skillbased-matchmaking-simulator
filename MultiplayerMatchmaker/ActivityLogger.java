package Component.MultiplayerMatchmaker;

import java.util.*;

import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class ActivityLogger extends ViewableAtomic {
	protected double observationPeriod;
	protected Player player;
	protected Map<Integer, Double> arrived; // all players that came in (including potential rejects), the double refers
											// to the joinTime
	protected ArrayList<Double> processed; // all players that have been processed, the double refers to the wait time
	protected ArrayList<Double> quit; // all players that have quit, the double refers to the wait time
	protected Map<String, Double> teamAvgTime; // teamID and the teams average time map;
	protected int matchedTeamsCount = 0; // number of matches that have been made
//	protected List<Player> currTeam;
	protected double clock;
	protected int avgSkillLevel;

	public ActivityLogger() {
		this("AL", 100.0);
	}

	public ActivityLogger(String name, Double ObservationPeriod) {
		super(name);
		addInport("arrived");
		addInport("processed");
		addInport("quit");
		addOutport("avgWtTime");
		addOutport("matchedTeamsCount");
		addOutport("quitPlayers");
		addOutport("rejectedPlayers");
//		addInport("matched2");
//		addInport("matched3");
		// player = Player.fromString("[id=2, skill=5, patience=44.82]");
		// [id=2, skill=5, patience=44.82]
		addTestInput("arrived", new entity("val"));

		observationPeriod = ObservationPeriod;

		arrived = new HashMap<Integer, Double>();
		processed = new ArrayList<Double>();
		quit = new ArrayList<Double>();
		// addTestInput("solved",new entity("val"));
		initialize();
	}

	public void initialize() {
		phase = "active";
		sigma = observationPeriod;
		clock = 0;
		arrived.clear();
		processed.clear();
		quit.clear();
		super.initialize();
	}

	public void deltext(double e, message x) {
		clock = clock + e;
		Continue(e);
		for (int i = 0; i < x.size(); i++) {
			if (messageOnPort(x, "arrived", i)) {
				// store in arrived map
				entity ent = x.getValOnPort("arrived", i);
				System.out.println("[" + getName() + "] arrived=" + ent.getName() + " clock=" + clock);
				Player player = Player.fromString(ent.getName());
				arrived.put(player.id, clock);
			}
			if (messageOnPort(x, "processed", i)) {
				matchedTeamsCount++;
				entity ent = x.getValOnPort("processed", i);
				ArrayList<Player> matchedPlayers = PlayerArrayUtil.fromString(ent.getName());
				for (Player player : matchedPlayers) {
					double arrivedTime = arrived.get(player.id);
					processed.add(clock - arrivedTime);
				}
				System.out.println("[" + getName() + "] processed=" + ent.getName() + " clock=" + clock);

			}
			if (messageOnPort(x, "quit", i)) {
				entity ent = x.getValOnPort("quit", i);
				ArrayList<Player> quitPlayers = PlayerArrayUtil.fromString(ent.getName());
				for (Player player : quitPlayers) {
					quit.add(clock - player.joinTime);
				}
				System.out.println("[" + getName() + "] quit=" + ent.toString() + " clock=" + clock);
			}
//			else if(messageOnPort(x,"processed", i)) {
//				entity ent = x.getValOnPort("processed", i); //have to comment this whole block
//				System.out.println("[" + getName() + "] solved=" + ent.getName() + " clock=" + clock);
//				if(arrived.containsKey(ent.getName())) {
//					double timeArrived = arrived.get(ent.getName());
//					double timeToSolve = clock - timeArrived;
//					processed.put(ent.getName(), timeToSolve);
//				}
//				
//			}

		}
	}

	public void deltint() {
		clock = clock + sigma;
		passivate();
	}

	public message out() {
		message m = new message();
		// calculate average player wait time - DONE
		double avgWaitTime = calcAvgWaitTime(processed);
		int totalPlayers = arrived.size();
		int quitPlayers = calcQuitPlayers(quit);
		int rejectedPlayers = calcRejectedPlayers();
		m.add(makeContent("avgWtTime", new entity(String.valueOf(avgWaitTime))));
		m.add(makeContent("matchedTeamsCount", new entity(Integer.toString(matchedTeamsCount))));
		m.add(makeContent("totalPlayers", new entity(Integer.toString(totalPlayers))));
		m.add(makeContent("quitPlayers", new entity(Integer.toString(quitPlayers))));
		m.add(makeContent("rejectedPlayers", new entity(Integer.toString(rejectedPlayers))));

		// calculate number of rejected(unplayed) players
		// calculate number of teams created - DONE
		System.out.println("[" + getName() + "] avgWaitTime=" + avgWaitTime + " matchedTeamsCount=" + matchedTeamsCount
				+ " totalPlayers=" + totalPlayers + " quitPlayers=" + quitPlayers + " rejectedPlayers=" + rejectedPlayers + " clock=" + clock);
		return m;
	}

	public double calcAvgWaitTime(ArrayList<Double> processed) {
		if (processed.isEmpty()) {
			return 0;
		}
		double sum = 0;
		for (double value : processed) {
			sum += value;
		}
		double avg = sum / processed.size();
		return avg;
	}

	public int calcQuitPlayers(ArrayList<Double> quit) {
		return quit.size();
	}

	public int calcRejectedPlayers() {
		return arrived.size() - (processed.size() + quit.size());
	}

}
