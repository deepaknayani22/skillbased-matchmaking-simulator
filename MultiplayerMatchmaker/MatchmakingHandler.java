package Component.MultiplayerMatchmaker;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

import GenCol.entity;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class MatchmakingHandler extends ViewableAtomic {
	protected ArrayDeque<Player> Q1 = new ArrayDeque<Player>();
	protected ArrayDeque<Player> Q2 = new ArrayDeque<Player>();
	protected ArrayDeque<Player> Q3 = new ArrayDeque<Player>();
	
	protected Player player = new Player(0,0,0);
	protected String outPort;

	public MatchmakingHandler(ArrayDeque<Player> Q1, ArrayDeque<Player> Q2, ArrayDeque<Player> Q3) {
		this("MMH", Q1, Q2, Q3);
	}

	public MatchmakingHandler(String name, ArrayDeque<Player> Q1, ArrayDeque<Player> Q2, ArrayDeque<Player> Q3) {
		super(name);
		this.Q1 = Q1;
		this.Q2 = Q2;
		this.Q3 = Q3;
		
		addInport("in");
		addOutport("queue1");
		addOutport("queue2");
		addOutport("queue3");
		addTestInput("in", new entity((new Player(123, 2, 123)).toString()));
		addTestInput("in", new entity((new Player(456, 5, 123)).toString()));
		addTestInput("in", new entity((new Player(789, 7, 123)).toString()));
		setBackgroundColor(Color.cyan);
	}

	public void initialize() {
		passivate();
		super.initialize();
	}

	public void deltext(double e, message x) {
		Continue(e);
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "in", i)) {
				entity ent = x.getValOnPort("in", i);
				Player inputPlayer = Player.fromString(ent.toString());
				if (phaseIs("passive")) {
					player = inputPlayer;
					outPort = "queue" + skillToTier(player.skill);
					holdIn("active", 0);
					System.out.println("[" + getName() + "] input=" + inputPlayer);
				} else {
					System.out.println("[" + getName() + "] RejectedInput=" + inputPlayer);
				}
			}
		}
	}

	public void deltint() {
		passivate();
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();
		if (phaseIs("active")) {
			entity ent = new entity(player.toString());
			m.add(makeContent(outPort, ent));
			System.out.println("[" + getName() + "] output=" + ent.toString());
		}
		return m;
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "player: " + player.toString();
	}
	
	public int skillToTier(int skill) {
		if (skill >= 1 && skill <= 3)
			return 1;
		else if (skill >= 4 && skill <= 6)
			return 2;
		else if (skill >= 7 && skill <= 9)
			return 3;
		else
			return -1;
	}
}
