package Component.MultiplayerMatchmaker;

import java.awt.Color;

import java.util.*;

import GenCol.entity;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class Queue1 extends ViewableAtomic {
	protected int queueCapacity;
	protected double clock;
	ArrayList<Player> outGameMatch;
	ArrayList<Player> outPatienceExpired;

	protected ArrayDeque<Player> Q1 = new ArrayDeque<Player>();
	protected ArrayDeque<Player> Q2 = new ArrayDeque<Player>();
	protected ArrayDeque<Player> Q3 = new ArrayDeque<Player>();

	private final double responseTime = 5;
	private final double crossTierThresholdTime = 400;

	public Queue1(int queueCapacity, ArrayDeque<Player> Q1, ArrayDeque<Player> Q2, ArrayDeque<Player> Q3) {
		this("Q1", queueCapacity, Q1, Q2, Q3);
	}

	public Queue1(String name, int queueCapacity, ArrayDeque<Player> Q1, ArrayDeque<Player> Q2, ArrayDeque<Player> Q3) {
		super(name);
		this.queueCapacity = queueCapacity;
		this.Q1 = Q1;
		this.Q2 = Q2;
		this.Q3 = Q3;

		addInport("in");
		addOutport("match");
		addOutport("quit");
		addTestInput("in", new entity((new Player(123, 2, 123)).toString()));
		addTestInput("in", new entity((new Player(456, 5, 123)).toString()));
		addTestInput("in", new entity((new Player(789, 7, 123)).toString()));
		setBackgroundColor(Color.yellow);
	}

	public void initialize() {
		phase = "active";
		clock = 0;
		outGameMatch = new ArrayList<Player>();
		outPatienceExpired = new ArrayList<Player>();
		super.initialize();
	}

	public void deltext(double e, message x) {
		clock = clock + e;
		Continue(e);
		outPatienceExpired = getPlayersPatienceExpired(clock);
		for (Player player : outPatienceExpired) {
			Q1.remove(player);
		}
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "in", i)) {
				entity ent = x.getValOnPort("in", i);
				Player inputPlayer = Player.fromString(ent.toString());
				if (phaseIs("active")) {
					inputPlayer.joinTime = clock;

					Q1.addLast(inputPlayer);
					outGameMatch = makeMatch(clock);
					for (Player player : outGameMatch) {
						Q1.remove(player);
						Q2.remove(player);
						Q3.remove(player);
					}

					if (!outGameMatch.isEmpty() || !outPatienceExpired.isEmpty()) {
						holdIn("respond", responseTime);
					} else {
						if (Q1.size() >= queueCapacity) {
							holdIn("full", INFINITY);
						} else {
							holdIn("active", INFINITY);
						}
					}
					System.out.println("[" + getName() + "] input=" + inputPlayer);
				} else {
					System.out.println("[" + getName() + "] RejectedInput=" + inputPlayer);
				}
			}
		}
	}

	public ArrayList<Player> makeMatch(double timeNow) {
//		ArrayList<Player> skill1 = new ArrayList<Player>();
//		ArrayList<Player> skill2 = new ArrayList<Player>();
//		ArrayList<Player> skill3 = new ArrayList<Player>();
//		for (Player player : Q1) {
//			if (player.skill == 1) {
//				skill1.add(player);
//			} else if (player.skill == 2) {
//				skill2.add(player);
//			} else if (player.skill == 3) {
//				skill3.add(player);
//			}
//		}
//		if (skill1.size() >= 10) {
//			return new ArrayList<Player>(skill1.subList(0, 10));
//		}
//		if (skill2.size() >= 10) {
//			return new ArrayList<Player>(skill2.subList(0, 10));
//		}
//		if (skill3.size() >= 10) {
//			return new ArrayList<Player>(skill3.subList(0, 10));
//		}

		if (Q1.size() < 10) {
			double avgWaitTime = 0;
			int count = 0;
			for (Player p : Q1) {
				avgWaitTime += timeNow - p.joinTime;
				count += 1;
			}
			avgWaitTime /= count;

			if (avgWaitTime < crossTierThresholdTime)
				return new ArrayList<Player>();

			ArrayList<Player> pool = new ArrayList<Player>();
			pool.addAll(Q1);
			pool.addAll(Q2);
			if (pool.size() < 10) {
				return new ArrayList<Player>();
			}
			System.out.println("[" + getName() + "] attempting cross-tier");
			return algo(pool);
		}

		System.out.println("[" + getName() + "] attempting same-tier");
		return algo(new ArrayList<Player>(Q1));
	}

	public ArrayList<Player> algo(ArrayList<Player> pool) {
		double minJoinTime = INFINITY, maxJoinTime = -INFINITY;
		for (Player player : pool) {
			minJoinTime = Math.min(minJoinTime, player.joinTime);
			maxJoinTime = Math.max(maxJoinTime, player.joinTime);
		}
		final double minJT = minJoinTime;
		final double maxJT = maxJoinTime;

		double minSkill = INFINITY, maxSkill = -INFINITY;
		for (Player player : pool) {
			minSkill = Math.min(minSkill, player.skill);
			maxSkill = Math.max(maxSkill, player.skill);
		}
		final double minSK = minSkill;
		final double maxSK = maxSkill;

		Comparator<Player> score = Comparator
				.comparingDouble(p -> (2 * ((p.joinTime - minJT) / maxJT) + ((p.skill - minSK) / maxSK)));

		ArrayList<Player> sorted = new ArrayList<Player>(pool);
		Collections.sort(sorted, score);
		System.out.println("[" + getName() + "] pool=" + pool.toString());
//		System.out.println("[" + getName() + "] Q1(sorted)=" + sorted.toString());

		System.out.print("[" + getName() + "] pool(sorted)=");
		for (Player p : sorted) {
			StringBuilder builder = new StringBuilder();
			builder.append("Player [id=").append(p.id).append(", skill=").append(p.skill).append(", score=")
					.append(String.format("%.2f", 2 * ((p.joinTime - minJT) / maxJT) + (p.skill / 3.0))).append("]  ");
			System.out.print(builder.toString());
		}
		System.out.println();

		ArrayList<Player> gameMatch = new ArrayList<Player>();
		gameMatch.add(sorted.get(0));
		gameMatch.add(sorted.get(2));
		gameMatch.add(sorted.get(4));
		gameMatch.add(sorted.get(6));
		gameMatch.add(sorted.get(8));
		gameMatch.add(sorted.get(1));
		gameMatch.add(sorted.get(3));
		gameMatch.add(sorted.get(5));
		gameMatch.add(sorted.get(7));
		gameMatch.add(sorted.get(9));

		int team1Skill = gameMatch.subList(0, 5).stream().mapToInt(p -> p.skill).sum();
		int team2Skill = gameMatch.subList(5, 10).stream().mapToInt(p -> p.skill).sum();
		System.out.println("[" + getName() + "] team1Skill=" + team1Skill + " team2Skill=" + team2Skill);

		return gameMatch;
	}

	public ArrayList<Player> getPlayersPatienceExpired(double timeNow) {
		ArrayList<Player> list = new ArrayList<Player>();
		for (Player player : Q1) {
			if (timeNow >= player.patience + player.joinTime) {
				list.add(player);
			}
		}
		return list;
	}

	public void deltint() {
		clock = clock + sigma;

		if (Q1.size() >= queueCapacity) {
			holdIn("full", INFINITY);
		} else {
			holdIn("active", INFINITY);
		}
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();
		if (phaseIs("respond")) {
			if (!outGameMatch.isEmpty()) {
				String s = PlayerArrayUtil.toString(outGameMatch);
				entity ent = new entity(s);
				m.add(makeContent("match", ent));
				System.out.println("[" + getName() + "] match=" + ent.toString());
			}

			if (!outPatienceExpired.isEmpty()) {
				String s = PlayerArrayUtil.toString(outPatienceExpired);
				entity ent = new entity(s);
				m.add(makeContent("quit", ent));
				System.out.println("[" + getName() + "] quit=" + ent.toString());
			}

			outGameMatch.clear();
			outPatienceExpired.clear();
		}
		return m;
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "Q1: " + Q1.toString();
	}
}
