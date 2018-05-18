package ch.idsia.tools;

import Evolution.generator.Chromosome;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.mario.simulation.BasicSimulator;
import ch.idsia.mario.simulation.Simulation;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.ToolsConfigurator;
import competition.cig.robinbaumgarten.AStarAgent;
import competition.cig.robinbaumgarten.LimitedJumpAgent;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.mario.engine.level.Level;
import java.util.Random;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;


public class RunGivenLevel {


    public Agent perfect;
    public Agent disabled;

    public String level;

    public Random rnd;

    public RunGivenLevel(Random rnd) {
       this.rnd = rnd;
    }
//    public static void main(String[] args) {
//
//        CmdLineOptions options = new CmdLineOptions(args);
//
//        // construct and set up agents
//        RunGivenLevel eL = new RunGivenLevel(new Random());
//
//    }

    public void agentSetup() {
        perfect = new AStarAgent();
        disabled = new LimitedJumpAgent();
    }

    public void setLevel(String chromosome) {
        level = chromosome;
    }
    public double runLevel(CmdLineOptions options) {
//        System.out.println(level);
        Level lvl = Level.initializeLevel(rnd, level);


        this.agentSetup();
        options = optionSetup(options);

//        this.runLevel(options);
//        Task task = new ProgressTask(options);

//        double[] perf = task.evaluate(perfect);
//        double[] disa = task.evaluate(disabled);

        // perfect first
        options.setAgent(perfect);


        Simulation simulator = new BasicSimulator(options.getSimulationOptionsCopy());
        EvaluationInfo perf;
        perf = ((BasicSimulator)simulator).simulateOneLevel(lvl);

        options.setAgent(disabled);
        simulator = new BasicSimulator(options.getSimulationOptionsCopy());
        EvaluationInfo disa;
        disa = ((BasicSimulator)simulator).simulateOneLevel(lvl);

       double perfectPerformance =  perf.computeDistancePassed();
       double disabledPerformance = disa.computeDistancePassed();

       double result = (perfectPerformance - disabledPerformance) / options.getLevelLength();
       System.out.println(result);
       return result;
//        System.out.println(perf);
//        System.out.println(disa);

    }

    public CmdLineOptions optionSetup(CmdLineOptions options) {

        if(options == null) {


            options = new CmdLineOptions(new String[0]);
            // basic options stuff
//            options.setMaxFPS(false);
            options.setVisualization(true);
            options.setNumberOfTrials(1);
            options.setMaxFPS(true);
            ToolsConfigurator.CreateMarioComponentFrame(
                    options);

            options.setTimeLimit(10);
        }
        // flag that this is going to work differently. We are going to insert our own level here
//        options.setCustomLevel(true);
        return options;
    }
}
