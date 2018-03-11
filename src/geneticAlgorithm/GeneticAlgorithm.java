package geneticAlgorithm;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import Enum.MachineLearingType;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.System.exit;

public class GeneticAlgorithm {

	private double maxFitness;
	private int generation;
	
	private int populationSize;				//种群规模
	private double mutationRate;			//变异率
	private double crossoverRate;			//交叉率
	private int elitismCount;				//精英计数
	private Instances trainDataset;			//训练数据集
	private Instances testDataset;			//测试数据集
	private Classifier classifier;
	
	public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount,
							Instances trainDataset, Instances testDataset, MachineLearingType MLT) {
		super();
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;
		this.trainDataset = trainDataset;
		this.testDataset = testDataset;
		if(MLT==MachineLearingType.J48)
			this.classifier= new J48();
		else if(MLT==MachineLearingType.SMO) {
			this.classifier = new SMO();
		}else if(MLT==MachineLearingType.JRIP)
			this.classifier=new JRip();
		else if(MLT==MachineLearingType.RF)
			this.classifier=new RandomForest();
		else {
			System.out.println("Didn't supported Machining Learning Algorithm");
			exit(0);
		}
		this.maxFitness=0;
		this.generation=0;
	}
	
	public Population initPopulation(int chromosomeLength){
		Population population = new Population(this.populationSize, chromosomeLength);
		return population;
	}
	
	public double calcFitness(Individual individual){
		Instances rawDataset=this.trainDataset;
		double precision=0;

		Remove rm=new Remove();
		ArrayList<Integer> rmArrayList=new ArrayList<Integer>();
		for(int geneIndex=0;geneIndex<individual.getChromosomeLength();geneIndex++){
			if(individual.getGene(geneIndex)==1)
				rmArrayList.add(geneIndex);
		}
		int[] rmList=new int[rmArrayList.size()];
		for(int i=0; i<rmArrayList.size(); i++)
			rmList[i]=rmArrayList.get(i).intValue();
		rm.setAttributeIndicesArray(rmList);
		try {
			rm.setInputFormat(rawDataset);
			Instances filterDataset= Filter.useFilter(rawDataset, rm);
			System.out.println(filterDataset);
			System.out.println(filterDataset.classIndex());

			this.classifier.buildClassifier(filterDataset);
			Evaluation eval = new Evaluation(filterDataset);
			if(this.testDataset!=null){
				eval.evaluateModel(this.classifier, testDataset);
				precision=eval.precision(0);
			}else{
				eval.crossValidateModel(this.classifier, filterDataset, 10, new Random(1234));
				precision=eval.precision(0);
			}

			individual.setFitness(precision);
		} catch (Exception e) {
			e.printStackTrace();
			exit(0);
		}

		
		return precision;
	}
	
	public void evalPopulation(Population population){
		double populationFitness = 0;
		for(Individual individual:population.getIndividuals())
			populationFitness += calcFitness(individual);
		
		population.setPopulationFitness(populationFitness);
	}

	public boolean isTerminationConditionMet(Population population){
		double tmp=0;
		for(Individual individual:population.getIndividuals())
			if(individual.getFitness()>tmp)
				tmp=individual.getFitness();
		if(Math.abs(tmp-this.maxFitness)<0.001){
			this.generation++;
		}else{
			this.maxFitness=tmp;
			this.generation=0;
		}

		if(generation>10)
			return true;
		else
			return false;
	}
	
	public Individual selectParent(Population population){
		Individual individuals[] = population.getIndividuals();
		
		double populationFitness = population.getPopulationFitness();
		double rouletteWheelPosition = Math.random()*populationFitness;
		
		double spinWheel = 0;
		for(Individual individual:individuals){
			spinWheel += individual.getFitness();
			if(spinWheel>=rouletteWheelPosition)
				return individual;
		}
		return individuals[population.size()-1];
	}
	
	public Population crossoverPopulation(Population population){
		//Create new population
		Population newPopulation = new Population(population.size());
		
		//Loop over current population by fitness
		for(int populationIndex=0;populationIndex<population.size();populationIndex++){
			Individual parent1 = population.getFittest(populationIndex);
			
			//Applay crossover to this individual
			if(this.crossoverRate>Math.random() && populationIndex>this.elitismCount){
				//Initialize offspring
				Individual offspring = new Individual(parent1.getChromosomeLength());
				
				//Find second parent
				Individual parent2 = selectParent(population);
				
				//Loop over genome
				for(int geneIndex=0;geneIndex<parent1.getChromosomeLength();geneIndex++){
					if(0.5>Math.random())
						offspring.setGene(geneIndex, parent1.getGene(geneIndex));
					else
						offspring.setGene(geneIndex, parent2.getGene(geneIndex));
				}
				
				//Add offspring to new population
				newPopulation.setIndividuals(populationIndex, offspring);
			}else{
				//Add individual to new population without applying crossvoer
				newPopulation.setIndividuals(populationIndex, parent1);
			}
		}
		return newPopulation;
	}
	
	public Population mutatePopulation(Population population){
		//Initialize new population
		Population newPopulation = new Population(this.populationSize);
		
		//Loop over current population by fitness
		for(int populationIndex=0;populationIndex<population.size();populationIndex++){
			Individual individual = population.getFittest(populationIndex);
			
			//Loop over individual's genes
			for(int geneIndex=0;geneIndex<individual.getChromosomeLength();geneIndex++){
				//Skip mutation if this is an elite individual
				if(populationIndex>=this.elitismCount){
					//Does this gene need mutation?
					if(this.mutationRate>Math.random()){
						//Get new gene
						int newGene = 1;
						if(individual.getGene(geneIndex)==1)
							newGene=0;
						//Mutate gene
						individual.setGene(geneIndex, newGene);
					}
				}
			}
			//Add individual to population
			newPopulation.setIndividuals(populationIndex, individual);	
		}
		//Return mutated population
		return newPopulation;
	}
	
}
