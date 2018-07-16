package Evolution;

import Evolution.generator.*;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.io.FileWriter;
import java.io.PrintWriter;

public class EvolveALevel {

    SlicesLibrary library;

//    Agent perfect;
//    Agent disabled;
//    String popFolder = "LJ/";
//    String popFolder = "EB/";
//    String popFolder = "NR/";

//    String popFolder = "LJC/";
//    String popFolder = "EBC/";
    String popFolder = "NRC/";
    GeneticAlgorithm ga;

//    public void init() {
//        perfect = = new AStarAgent();
//        disabled = new LimitedJumpAgent();
//    }
    public static void main(String[] args) {

        EvolveALevel eal = new EvolveALevel();
        eal.library = new RepeatedLevelSlicesLibrary();
        File directory = new File("levels/");
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith("txt");
            }
        });
        try{
            for (File f : files) {
                String[] lines = Files.readAllLines(f.toPath()).toArray(new String[0]);
                eal.library.addLevel(lines);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
//        long startTime = System.currentTimeMillis();
//        long time = 1000*60*60;
//        int count = 0;
        eal.init();

        // time based
//        while (System.currentTimeMillis() - startTime < time) {
//            eal.evolve(count + 1);
//            count++;
//        }
        // gen based
        eal.evolve();
    }

    public void init() {
        ga = new GeneticAlgorithm(library, 100, 18, 0, 0.9, 0.3, 1);
    }
    public void evolve() {

        try {
            this.ga.initFileSystem(popFolder);
//            this.ga = new GeneticAlgorithm(library, 100, 18, 0.9, 0.3, 1);
            Chromosome[] pop = this.ga.evolve(120);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
