package org.albaross.agents4j.island;

import java.io.IOException;
import java.util.Arrays;

import org.albaross.agents4j.core.Agent;
import org.albaross.agents4j.core.BasicBuilder;
import org.albaross.agents4j.learning.QNeg;
import org.albaross.agents4j.learning.components.QNetComponent;
import org.albaross.agents4j.learning.operators.EpsilonGreedyOperator;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.util.Constants;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class QNetExample {

	public static final int NET_IN = 17;
	public static final int NET_OUT = 8;

	public static void main(String[] args) throws IOException {
		//Initialize the user interface backend
		UIServer uiServer = UIServer.getInstance();

		//Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
		StatsStorage statsStorage = new InMemoryStatsStorage();

		//Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
		uiServer.attach(statsStorage);

		MultiLayerConfiguration mlnconf = new NeuralNetConfiguration.Builder() //
				.seed(Constants.NEURAL_NET_SEED) //
				.iterations(1).optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT) //
				.learningRate(0.001) //
				.updater(Updater.ADAM) //
				.weightInit(WeightInit.XAVIER) //
				.regularization(true) //
				.l2(0.001) //
				.list() //
				.layer(0, new DenseLayer.Builder().nIn(NET_IN).nOut(16).activation(Activation.RELU).build()) //
				.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.IDENTITY).nIn(16).nOut(NET_OUT).build()) //
				.pretrain(false) //
				.backprop(true) //
				.build();

		MultiLayerNetwork net = new MultiLayerNetwork(mlnconf);
		net.init();
		net.setListeners(new StatsListener(statsStorage)/*, new ScoreIterationListener(Constants.NEURAL_NET_ITERATION_LISTENER)*/);

		EncoderIslandPerception encoder = new EncoderIslandPerception(4);
		BasicBuilder<IslandPerception, IslandAction> builder = new BasicBuilder<>();
		builder.add(new EpsilonGreedyOperator<>(IslandAction::randomAction, 0.1));
		builder.add(new QNetComponent<IslandPerception, IslandAction>(
				new QNeg<IslandPerception, IslandAction>(net, encoder, IslandAction::encode, IslandAction::decode), 100000, 32));
		Agent<IslandPerception, IslandAction> agent = builder.getAgent();
		IslandLabEnvironment env = new IslandLabEnvironment(Arrays.asList(agent));
		env.setMaxSteps(50000);

		for (int r = 0; r < 150000; r++) {
			env.run();
			System.out.println("round: " + r + ", rewards: " + env.getCumulative(0));
		}

		//Finally: open your browser and go to http://localhost:9000/train
	}

}
