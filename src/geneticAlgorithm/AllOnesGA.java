package geneticAlgorithm;

import Enum.SmellType;
import Enum.MachineLearingType;
import weka.core.Instances;


public class AllOnesGA {
	public static void main(String[] args){

		String trainPath="D:/ch_dataset.csv";
		String testPath="D:/test_dataset.csv";

		Dataset train_dt=new Dataset(SmellType.CLASS_TYPE, trainPath);
		Instances trainDataset=train_dt.getDataset();
		//System.out.println(trainDataset);

		Dataset test_dt=new Dataset(SmellType.CLASS_TYPE, testPath);
		Instances testDataset=test_dt.getDataset();
		//System.out.println(testDataset);

		GeneticAlgorithm ga = new GeneticAlgorithm(100, 0.01, 0.95,
				10, trainDataset, null, MachineLearingType.SMO);

		int chromosomeLength=trainDataset.numAttributes()-1;

		Population population = ga.initPopulation(chromosomeLength);
		
		ga.evalPopulation(population);
		int generation = 0;
		
		while(ga.isTerminationConditionMet(population)==false){
			System.out.println("Best solution:"+ population.getFittest(0).toString());
			
			//Apply crossover
			population = ga.crossoverPopulation(population);
			
			//Apply mutation
			population = ga.mutatePopulation(population);
			
			//Evaluation population
			ga.evalPopulation(population);
			
			//Increment the current generation
			generation++;
		}
		
		System.out.println("Found solution in "+ generation+"generations");
		System.out.println("Best solution: "+population.getFittest(0).toString());
		System.out.println("Best precision: "+population.getFittest(0).getFitness());
	}
}
