package changedSmell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import CSV.CSVParser;

public class ChangedSmellDataset {
    public void formClassDataset(String baselinePath, String contrastivePath,
                                 String dstPath){
        List<List<String>> res=new ArrayList<>();
        CSVParser csvp=new CSVParser();
        List<List<String>> baseline=CSVParser.readCSV(baselinePath);
        System.out.println("baselineSize:"+baseline.size());
        List<List<String>> contrastive=CSVParser.readCSV(contrastivePath);
        res.add(baseline.get(0));
        System.out.println("contrastiveSize:"+contrastive.size());

        //handler select
        List<List<String>> changedSmell=new ArrayList<>();
        List<List<String>> noSmell=new ArrayList<>();

        for(int i=1; i<baseline.size(); i++){
            List<String> line=baseline.get(i);
            int flag = 0;
            for(int j=1; j<contrastive.size(); j++){
                List<String> tmp=contrastive.get(j);
                if(line.get(line.size()-1).equals("true") && line.get(0).equals(tmp.get(0))){
                    flag=1;
                    break;
                }else
                    continue;
            }
            if(flag==0){
                if(line.get(line.size()-1).equals("true")){
                    changedSmell.add(line);
                }else{
                    noSmell.add(line);
                }
            }
        }

        System.out.println("changedSmellSize:"+changedSmell.size());
        System.out.println("noSmellSize:"+noSmell.size());

        for(int i=0; i<changedSmell.size(); i++)
            res.add(changedSmell.get(i));

        int RATIO=1;
        int count=0;
        while(count < changedSmell.size()*RATIO){
            count+=1;
            Random random = new Random();
            int j=random.nextInt(noSmell.size()-1);
            res.add(noSmell.get(j));
        }

        System.out.println(res.size());
        try {
            csvp.writeCSV(res, dstPath+"ch_dataset.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
