package Evolution;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;

import Evolution.generator.*;

public class TestGenerator {
    public static void main(String[] args){
		SlicesLibrary library = new RepeatedLevelSlicesLibrary();

		File directory = new File("levels/");
		File[] files = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().endsWith("txt");
			}
		});
		try {
			for(File f:files){
			String[] lines = Files.readAllLines(f.toPath()).toArray(new String[0]);
			library.addLevel(lines);
			}
			GeneticAlgorithm ga = new GeneticAlgorithm(library, 10, 12, 3, 0.9, 0.3, 1);
			Chromosome[] pop = ga.evolve(1000);
			for(int i=0; i<pop.length; i++){
			System.out.println("index " + i + " constraints " + pop[i].getConstraints() + " fitness " + pop[i].getFitness());
			System.out.println(pop[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
