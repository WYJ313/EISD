package geneticAlgorithm;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

import Enum.SmellType;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class Dataset {
    private Instances dataset;

    public Dataset(SmellType ST, String path){
        System.out.println("Load dataset:"+path);
        CSVLoader loader=new CSVLoader();
        try {
            loader.setSource(new File(path));
            Instances rawDataset=loader.getDataSet();
            if(rawDataset.classIndex()==-1)
                rawDataset.setClassIndex(rawDataset.numAttributes()-1);
            Remove rm = new Remove();
            if(ST==SmellType.CLASS_TYPE) {
                rm.setAttributeIndices("1");
            }else if(ST==SmellType.METHOD_TYPE){
                int[] rmList=new int[2];
                rmList[0]=0;
                rmList[1]=1;
                rm.setAttributeIndicesArray(rmList);
            }
            rm.setInputFormat(rawDataset);
            this.dataset= Filter.useFilter(rawDataset, rm);

        }catch(IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Instances getDataset(){
        return this.dataset;
    }


}
