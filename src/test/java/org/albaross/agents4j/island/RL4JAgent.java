package org.albaross.agents4j.island;

import static org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense.Configuration.builder;

import java.io.IOException;
import java.util.Arrays;

import org.albaross.agents4j.learning.MDPWrapper;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.learning.ILearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.Constants;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.factory.Nd4j;

public class RL4JAgent {

	public static final int NET_IN = 6;
	public static final int NET_OUT = 8;

	public static QLearning.QLConfiguration GRID_QL = new QLearning.QLConfiguration(12345,   //Random seed
			2000000,//Max step By epoch
			4000000, //Max step
			2000000, //Max size of experience replay
			32,    //size of batches
			10,   //target update (hard)
			0,     //num step noop warmup
			0.01,  //reward scaling
			0.9,  //gamma
			10.0,  //td-error clipping
			0.1f,  //min epsilon
			4000,  //num step for eps greedy anneal
			false   //double DQN
	);

	public static void main(String[] args) throws IOException {

		DataManager manager = new DataManager();

		//Initialize the user interface backend
		UIServer uiServer = UIServer.getInstance();

		//Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
		StatsStorage statsStorage = new InMemoryStatsStorage();

		//Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
		uiServer.attach(statsStorage);

		DQNFactoryStdDense.Configuration conf = builder() //
				.l2(0.001) //
				.learningRate(0.001) //
				.numLayer(3) //
				.numHiddenNodes(16) //
				.listeners(new IterationListener[] { new StatsListener(statsStorage),
						new ScoreIterationListener(Constants.NEURAL_NET_ITERATION_LISTENER) }) //
				.build();

		DQNFactoryStdDense fac = new DQNFactoryStdDense(conf);
		@SuppressWarnings("rawtypes")
		final DQN net = fac.buildDQN(new int[] { NET_IN }, NET_OUT);

		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList((p) -> {
			return IslandAction.decode(Nd4j.argMax(net.output(Nd4j.create(p.toArray())), 1).getInt(0));
		}));

		MDPWrapper<IslandPerception, IslandAction> mdp = new MDPWrapper<>(env, NET_IN, NET_OUT, IslandAction::decode);

		ILearning<IslandPerception, Integer, DiscreteSpace> dql = new QLearningDiscreteDense<>(mdp, net, GRID_QL, manager);

		dql.train();

		net.save("C:/Net/island" + System.currentTimeMillis() + ".net");
	}

}
