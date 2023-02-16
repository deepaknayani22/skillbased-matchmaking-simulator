package Component.MultiplayerMatchmaker;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

import GenCol.entity;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class PlayerEntityGenerator extends ViewableAtomic {
	protected double beta = 1; // mean inter-arrival time (= 1 / lambda)
	protected double interval;
	protected Player player = new Player(0, 0, 0);
	
	protected int idxFixedOutput = 0;

	/**** Set `forceFixedOutput` to `false` for actual random behaviour ****/
	protected static boolean forceFixedOutput = false;

	protected static double[] arrivalTimeFixedOutput = { 7.0, 16.1, 7.5, 15.4, 17.5, 17.5, 3.0, 6.2, 3.8, INFINITY };
	protected static int[] skillFixedOutput = { 3, 4, 2, 1, 9, 9, 3, 6, 7, -1 };
	protected static double[] patienceFixedOutput = { 7.0, 16.1, 7.5, 15.4, 17.5, 17.5, INFINITY, 6.2, 3.8, -1 };

	public PlayerEntityGenerator() {
		this("PEG", 30);
	}

	public PlayerEntityGenerator(String name, double beta) {
		super(name);
		addInport("start");
		addInport("stop");
		addOutport("player");
		addTestInput("start", new entity());
		addTestInput("stop", new entity());
		this.beta = beta;
		setBackgroundColor(Color.green);
	}

	public void initialize() {
		if (arrivalTimeFixedOutput.length != skillFixedOutput.length) {
			throw new java.lang.RuntimeException("Fixed output data invalid");
		}
		if (arrivalTimeFixedOutput.length != patienceFixedOutput.length) {
			throw new java.lang.RuntimeException("Fixed output data invalid");
		}
		if (arrivalTimeFixedOutput[arrivalTimeFixedOutput.length - 1] != INFINITY) {
			throw new java.lang.RuntimeException("Fixed output data invalid");
		}
		if (skillFixedOutput[skillFixedOutput.length - 1] != -1) {
			throw new java.lang.RuntimeException("Fixed output data invalid");
		}
		if (patienceFixedOutput[patienceFixedOutput.length - 1] != -1) {
			throw new java.lang.RuntimeException("Fixed output data invalid");
		}

		idxFixedOutput = 0;
		interval = generateRandomInterval();
		player.id = 1;
		player.skill = generateRandomSkill();
		player.patience = generateRandomPatience();
		if (forceFixedOutput)
			++idxFixedOutput;
		System.out.println("[" + getName() + "] nextOutputDelay=" + interval + " nextOutput="
				+ player.toString());
		phase = "active";
		sigma = interval;
		super.initialize();
	}

	public void deltext(double e, message x) {
		Continue(e);
		if (phaseIs("passive")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "start", i)) {
					interval = generateRandomInterval();
					++player.id;
					player.skill = generateRandomSkill();
					player.patience = generateRandomPatience();
					if (forceFixedOutput)
						++idxFixedOutput;
					System.out.println("[" + getName() + "] start");
					System.out.println("[" + getName() + "] nextOutputDelay=" + interval + " nextOutput="
							+ player.toString());
					holdIn("active", interval);
				}
			}
		}
		if (phaseIs("active")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "stop", i)) {
					phase = "finishing";
					System.out.println("[" + getName() + "] stop");
				}
			}
		}
	}

	public void deltint() {
		if (phaseIs("active")) {
			interval = generateRandomInterval();
			++player.id;
			player.skill = generateRandomSkill();
			player.patience = generateRandomPatience();
			if (forceFixedOutput)
				++idxFixedOutput;
			System.out.println("[" + getName() + "] nextOutputDelay=" + interval + " nextOutput="
					+ player.toString());
			holdIn("active", interval);
		} else {
			passivate();
		}
	}

	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();
		entity ent = new entity("INVALID");
		ent = new entity(player.toString());
		System.out.println("[" + getName() + "] output=" + ent.toString());
		content con = makeContent("player", ent);
		m.add(con);
		return m;
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + "idxFixedOutput: " + idxFixedOutput + "\n" + "interval: " + interval + "\n"
				+ "player: " + player.toString();
	}

	public double generateRandomInterval() {
		if (forceFixedOutput) {
			return arrivalTimeFixedOutput[idxFixedOutput];
		}
		// https://stackoverflow.com/questions/29020652/java-exponential-distribution
		// https://en.wikipedia.org/wiki/Exponential_distribution#Random_variate_generation
		double u = ThreadLocalRandom.current().nextDouble();
		return Math.log(1-u)*(-beta);
	}

	public int generateRandomSkill() {
		if (forceFixedOutput) {
			return skillFixedOutput[idxFixedOutput];
		}
		return ThreadLocalRandom.current().nextInt(1, 9 + 1);
	}

	public double generateRandomPatience() {
		if (forceFixedOutput) {
			return patienceFixedOutput[idxFixedOutput];
		}
		// https://studyfinds.org/customer-service-survey-millennials-most-patient-generation/
		// https://www.fiercehealthcare.com/sponsored/how-patient-wait-times-affect-customer-satisfaction
		// https://www.callcentrehelper.com/acceptable-waiting-time-133760.htm
		return ThreadLocalRandom.current().nextDouble(600, 800);
	}
}
