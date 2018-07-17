package Evolution.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GeneticAlgorithm {
    private int _populationSize;
    private int _chromosomeLength;
    private int _appendingSize;
    private double _crossover;
    private double _mutation;
    private int _elitism;
    private int _selectionMethod;
    private Random _rnd;
    private SlicesLibrary _lib;
    private String popFolder;

    public GeneticAlgorithm(SlicesLibrary lib, int populationSize, int chromosomeLength, int appendingSize, double crossover,
							double mutation, int elitism) {
	this(lib, populationSize, chromosomeLength, appendingSize, crossover, mutation, elitism, 0);
    }

    public GeneticAlgorithm(SlicesLibrary lib, int populationSize, int chromosomeLength, int appendingSize, double crossover,
							double mutation, int elitism, int selectionMethod) {
	this._lib = lib;
	this._populationSize = populationSize;
	this._chromosomeLength = chromosomeLength;
	this._appendingSize = appendingSize;
	this._crossover = crossover;
	this._mutation = mutation;
	this._elitism = elitism;
	this._selectionMethod = selectionMethod;
	this._rnd = new Random(100);
    }

    public void initFileSystem(String f) {
        popFolder = f;
    }
    
    private Chromosome tournmentSelection(Chromosome[] pop){
	Chromosome[] tournment = new Chromosome[this._selectionMethod];
	for(int i=0; i<tournment.length; i++){
	    tournment[i] = pop[this._rnd.nextInt(pop.length)];
	}
	Arrays.sort(tournment);
	return tournment[0];
    }

    private Chromosome rankSelection(Chromosome[] pop) {
	double[] ranks = new double[pop.length];
	double total = 0;
	for (int i = 0; i < pop.length; i++) {
	    ranks[i] = pop.length - i;
	    total += ranks[i];
	}
	for (int i = 1; i < pop.length; i++) {
	    ranks[i] = ranks[i] + ranks[i - 1];
	}
	for (int i = 0; i < pop.length; i++) {
	    ranks[i] /= total;
	}
	double value = this._rnd.nextDouble();
	for (int i = 0; i < pop.length; i++) {
	    if (value < ranks[i]) {
		return pop[i];
	    }
	}
	return pop[pop.length - 1];
    }
    
    private Chromosome[][] getFeasibleInfeasible(Chromosome[] pop){
	ArrayList<Chromosome> feasible = new ArrayList<Chromosome>();
	ArrayList<Chromosome> infeasible = new ArrayList<Chromosome>();
	for(int i=0; i<pop.length; i++) {
	    if(pop[i].getConstraints() < 1) {
		infeasible.add(pop[i]);
	    }
	    else {
		feasible.add(pop[i]);
	    }
	}
	return new Chromosome[][] {feasible.toArray(new Chromosome[0]), infeasible.toArray(new Chromosome[0]) };
    }


    public double averageFitness(Chromosome[] pop) {
    	double fitAvg = 0;
		for (Chromosome c : pop) {
			fitAvg += c.getFitness();
		}

		return fitAvg / pop.length;
	}

	public double averageConstraint(Chromosome[] pop) {
		double constAvg = 0;
		for (Chromosome c : pop) {
			constAvg += c.getConstraints();
		}
		return constAvg / pop.length;
	}

	public int getMaxFitnessIndex(Chromosome[] pop) {
		double max = -100;
		int maxIndex = -1;
    	for (int i = 0; i < pop.length; i++) {
			double fit = pop[i].getFitness();
			double constraint = pop[i].getConstraints();
			if(constraint == 1 && fit > max) {
				max = fit;
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public double getMaxFitness(Chromosome[] pop) {
		double max = -100;
		for (Chromosome c : pop) {
			double fit = c.getFitness();
            double constraint = c.getConstraints();

            if(constraint == 1 && fit > max) {
				max = fit;
			}
		}
		return max;
	}
    private void calculateFitness(Chromosome[] pop) {
        for (Chromosome c : pop) {
            c.calculateFitness();
        }
    }

    public Chromosome[] evolve(double time) {
        Chromosome[] currentPopulation = new Chromosome[this._populationSize];
        for (int i = 0; i < currentPopulation.length; i++) {
            currentPopulation[i] = new Chromosome(this._rnd, this._lib, this._chromosomeLength, this._appendingSize);
            currentPopulation[i].randomInitialize();
        }
        long startTime = System.currentTimeMillis();
        Chromosome[] newPopulation = new Chromosome[currentPopulation.length];

        int count = 0;
        while (System.currentTimeMillis() - startTime < time) {
            this.calculateFitness(currentPopulation);


//            try {
//                File popFile = new File(popFolder + "/" + (count + 1) + ".txt");
//                FileWriter fWriter = new FileWriter(popFile, true);
//                PrintWriter pWriter = new PrintWriter(fWriter);
//
//                pWriter.println("Max Fitness Index: " + getMaxFitnessIndex(currentPopulation) + ", " + getMaxFitness(currentPopulation));
//                pWriter.println("Average Constraint: " + averageConstraint(currentPopulation));
//                pWriter.println("AverageFitness: " + averageFitness(currentPopulation));
//
//
//                for (int k = 0; k < currentPopulation.length; k++) {
//                    System.out.println("index " + k + " constraints " + currentPopulation[k].getConstraints() + " fitness " + currentPopulation[k].getFitness());
//                    System.out.println(currentPopulation[k]);
//
//                    pWriter.println("index " + k + " constraints " + currentPopulation[k].getConstraints() + " fitness " + currentPopulation[k].getFitness());
//                    pWriter.println(currentPopulation[k]);
//                    pWriter.println(" ");
//
//                }
//                pWriter.close();
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
            saveGenerationData("" + (count + 1), currentPopulation);

            Chromosome[][] twoPop = this.getFeasibleInfeasible(currentPopulation);
            Arrays.sort(twoPop[0]);
            Arrays.sort(twoPop[1]);
            for (int i = 0; i < newPopulation.length - this._elitism; i++)
            {
                Chromosome[] usedPopulation = twoPop[1];
                if(this._rnd.nextDouble() < (double)(twoPop[0].length) / currentPopulation.length) {
                    usedPopulation = twoPop[0];
                }
                Chromosome parent1 = this.rankSelection(usedPopulation);
                if(this._selectionMethod > 0){
                    parent1 = this.tournmentSelection(usedPopulation);
                }
                Chromosome child = parent1.clone();
                if (this._rnd.nextDouble() < this._crossover) {
                    Chromosome parent2 = this.rankSelection(usedPopulation);
                    if(this._selectionMethod > 0){
                        parent2 = this.tournmentSelection(usedPopulation);
                    }
                    child = parent1.crossover(parent2);
                }
                if (this._rnd.nextDouble() < this._mutation) {
                    child = child.mutate();
                }
                newPopulation[i] = child;
            }
            for(int i=0; i<this._elitism; i++){
                if(i < twoPop[0].length) {
                    newPopulation[newPopulation.length - 1 - i] = twoPop[0][i];
                }
                else {
                    newPopulation[newPopulation.length - 1 - i] = twoPop[1][i];
                }
            }
            currentPopulation = newPopulation;
            count++;
        }
        this.calculateFitness(currentPopulation);
        saveGenerationData("last", currentPopulation);
//        try {
//            File popFile = new File(popFolder + "/final.txt");
//            FileWriter fWriter = new FileWriter(popFile, true);
//            PrintWriter pWriter = new PrintWriter(fWriter);
//
//            pWriter.println("Max Fitness Index: " + getMaxFitnessIndex(currentPopulation) + ", " + getMaxFitness(currentPopulation));
//            pWriter.println("Average Constraint: " + averageConstraint(currentPopulation));
//            pWriter.println("AverageFitness: " + averageFitness(currentPopulation));
//
//
//            for (int i = 0; i < currentPopulation.length; i++) {
//                System.out.println("index " + i + " constraints " + currentPopulation[i].getConstraints() + " fitness " + currentPopulation[i].getFitness());
//                System.out.println(currentPopulation[i]);
//
//                pWriter.println("index " + i + " constraints " + currentPopulation[i].getConstraints() + " fitness " + currentPopulation[i].getFitness());
//                pWriter.println(currentPopulation[i]);
//                pWriter.println(" ");
//
//            }
//            pWriter.close();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
        return currentPopulation;
    }

    public Chromosome[] evolve(int generations) {
        Chromosome[] currentPopulation = new Chromosome[this._populationSize];
        for (int i = 0; i < currentPopulation.length; i++) {
            currentPopulation[i] = new Chromosome(this._rnd, this._lib, this._chromosomeLength, this._appendingSize);
            currentPopulation[i].randomInitialize();
        }
        long startTime = System.currentTimeMillis();
        Chromosome[] newPopulation = new Chromosome[currentPopulation.length];

        for(int j = 0; j < generations; j++) {
            this.calculateFitness(currentPopulation);
            saveGenerationData("" + (j + 1), currentPopulation);

//            try {
//                File popFile = new File(popFolder + "/" + (j + 1) + ".txt");
//                FileWriter fWriter = new FileWriter(popFile, true);
//                PrintWriter pWriter = new PrintWriter(fWriter);
//
//                pWriter.println("Max Fitness Index: " + getMaxFitnessIndex(currentPopulation) + ", " + getMaxFitness(currentPopulation));
//                pWriter.println("Average Constraint: " + averageConstraint(currentPopulation));
//                pWriter.println("AverageFitness: " + averageFitness(currentPopulation));
//
//
//                for (int i = 0; i < currentPopulation.length; i++) {
//                    System.out.println("index " + i + " constraints " + currentPopulation[i].getConstraints() + " fitness " + currentPopulation[i].getFitness());
//                    System.out.println(currentPopulation[i]);
//
//                    pWriter.println("index " + i + " constraints " + currentPopulation[i].getConstraints() + " fitness " + currentPopulation[i].getFitness());
//                    pWriter.println(currentPopulation[i]);
//                    pWriter.println(" ");
//
//                }
//                pWriter.close();
//            } catch(Exception e) {
//                e.printStackTrace();
//            }

            Chromosome[][] twoPop = this.getFeasibleInfeasible(currentPopulation);
            Arrays.sort(twoPop[0]);
            Arrays.sort(twoPop[1]);
            for (int i = 0; i < newPopulation.length - this._elitism; i++) {
                Chromosome[] usedPopulation = twoPop[1];
                if(this._rnd.nextDouble() < (double)(twoPop[0].length) / currentPopulation.length) {
                    usedPopulation = twoPop[0];
                }
                Chromosome parent1 = this.rankSelection(usedPopulation);
                if(this._selectionMethod > 0){
                    parent1 = this.tournmentSelection(usedPopulation);
                }
                Chromosome child = parent1.clone();
                if (this._rnd.nextDouble() < this._crossover) {
                    Chromosome parent2 = this.rankSelection(usedPopulation);
                    if(this._selectionMethod > 0){
                        parent2 = this.tournmentSelection(usedPopulation);
                    }
                    child = parent1.crossover(parent2);
                }
                if (this._rnd.nextDouble() < this._mutation) {
                    child = child.mutate();
                }
                newPopulation[i] = child;
            }
            for(int i=0; i<this._elitism; i++){
                if(i < twoPop[0].length) {
                    newPopulation[newPopulation.length - 1 - i] = twoPop[0][i];
                }
                else {
                    newPopulation[newPopulation.length - 1 - i] = twoPop[1][i];
                }
            }
            currentPopulation = newPopulation;
        }
        this.calculateFitness(currentPopulation);
        saveGenerationData("last", currentPopulation);
//        try {
//            File popFile = new File(popFolder + "/final.txt");
//            FileWriter fWriter = new FileWriter(popFile, true);
//            PrintWriter pWriter = new PrintWriter(fWriter);
//
//            pWriter.println("Max Fitness Index: " + getMaxFitnessIndex(currentPopulation) + ", " + getMaxFitness(currentPopulation));
//            pWriter.println("Average Constraint: " + averageConstraint(currentPopulation));
//            pWriter.println("AverageFitness: " + averageFitness(currentPopulation));
//
//
//            for (int i = 0; i < currentPopulation.length; i++) {
//                System.out.println("index " + i + " constraints " + currentPopulation[i].getConstraints() + " fitness " + currentPopulation[i].getFitness());
//                System.out.println(currentPopulation[i]);
//
//                pWriter.println("index " + i + " constraints " + currentPopulation[i].getConstraints() + " fitness " + currentPopulation[i].getFitness());
//                pWriter.println(currentPopulation[i]);
//                pWriter.println(" ");
//
//            }
//            pWriter.close();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
        return currentPopulation;
    }

    public void saveGenerationData(String generationName, Chromosome[] generation) {
        try {
            File dir = new File(popFolder + "/" + generationName);
            File bestDir = new File(popFolder + "/best");
            if (!dir.exists()) {
                try {
                    dir.mkdirs();
                } catch (SecurityException se) {
                    System.err.println(se);
                }
            }
            if (!bestDir.exists()) {
                try {
                    bestDir.mkdirs();
                } catch (SecurityException se) {
                    System.err.println(se);
                }
            }
            PrintWriter popInfoFile = new PrintWriter(new FileWriter(popFolder + "/evolution_data.txt", true));
//            File popFile = new File(popFolder + "/" + (j + 1) + ".txt");
            int bestIndex = getMaxFitnessIndex(generation);
            popInfoFile.append("Max Fitness Index: " + bestIndex + ", " + getMaxFitness(generation) + "\n"
                    + "Average Constraint: " + averageConstraint(generation) + "\n"
                    + "AverageFitness: " + averageFitness(generation) + "\n");


            for (int i = 0; i < generation.length; i++) {
                PrintWriter pWriter = new PrintWriter(dir.getAbsolutePath() + "/gen_" + (i+1) + ".txt");

                System.out.println("index " + i + " constraints " + generation[i].getConstraints() + " fitness " + generation[i].getFitness());
                System.out.println(generation[i]);

                popInfoFile.append("index " + i + " constraints " + generation[i].getConstraints() + " fitness " + generation[i].getFitness() + "\n");
                pWriter.println(generation[i]);
                pWriter.close();

            }

            // save best info
            PrintWriter bestWriter = new PrintWriter(bestDir.getAbsolutePath() + "/" + generationName + ".txt");
            if(bestIndex != -1) {
                bestWriter.println(generation[bestIndex]);
            } else {
                bestWriter.println(" ");
            }
            bestWriter.close();
            popInfoFile.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");
            popInfoFile.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
