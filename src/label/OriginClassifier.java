package label;

import CSV.CSVParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class OriginClassifier {

    public void labelGodClas(String src, String dst){
        Map<String, Integer> metricsNameDict=new HashMap<>();
        List<List<String>> metricsValue=new ArrayList<>();

        if(getMetrics(src, metricsValue, metricsNameDict)==0) {
            System.out.println(metricsNameDict.size());
            System.out.println(metricsValue);
        }
        int ATFD_index=metricsNameDict.get("ATFD");
        int WMC_index=metricsNameDict.get("WMC");
        int TCC_index=metricsNameDict.get("TCC");

        for(int i=0; i<metricsValue.size(); i++){
            List<String> tmp=metricsValue.get(i);
            double ATFD=Double.valueOf(tmp.get(ATFD_index));
            double WMC=Double.valueOf(tmp.get(WMC_index));
            double TCC=Double.valueOf(tmp.get(TCC_index));
            if(ATFD>5 && TCC<0.3 && WMC>=47)
                tmp.add("true");
            else
                tmp.add("false");
            metricsValue.set(i, tmp);
        }
        List<String> lst=new ArrayList<String>();
        for(int i=0;i<metricsNameDict.size();i++){
            for(String key : metricsNameDict.keySet()){
                if(metricsNameDict.get(key)==i)
                    lst.add(key);
            }
        }
        lst.add("God Class");
        dst=dst+"GodClass.csv";
        List<List<String>> res=new ArrayList<>();
        res.add(lst);
        for(int i=0; i<metricsValue.size();i++)
            res.add(metricsValue.get(i));
        CSVParser csvParser=new CSVParser();
        try {
            csvParser.writeCSV(res, dst);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void labelDataClas(String src, String dst){
        Map<String, Integer> metricsNameDict=new HashMap<>();
        List<List<String>> metricsValue=new ArrayList<>();

        if(getMetrics(src, metricsValue, metricsNameDict)==0) {
            System.out.println(metricsNameDict.size());
            System.out.println(metricsValue);
        }
        int NOAM_index=metricsNameDict.get("NOAM");
        int WMC_index=metricsNameDict.get("WMC");
        int NIM_index=metricsNameDict.get("NIM");

        for(int i=0; i<metricsValue.size(); i++){
            List<String> tmp=metricsValue.get(i);
            double NOAM=Double.valueOf(tmp.get(NOAM_index));
            double WMC=Double.valueOf(tmp.get(WMC_index));
            double NIM=Double.valueOf(tmp.get(NIM_index));
            if(NOAM>2 && WMC<=21 && NIM<=30)
                tmp.add("true");
            else
                tmp.add("false");
            metricsValue.set(i, tmp);
        }
        List<String> lst=new ArrayList<String>();
        for(int i=0;i<metricsNameDict.size();i++){
            for(String key : metricsNameDict.keySet()){
                if(metricsNameDict.get(key)==i)
                    lst.add(key);
            }
        }
        lst.add("Data Class");

        List<List<String>> res=new ArrayList<>();
        res.add(lst);
        for(int i=0; i<metricsValue.size();i++)
            res.add(metricsValue.get(i));
        CSVParser csvParser=new CSVParser();
        try {
            csvParser.writeCSV(res, dst);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void labelBrainClass(String src, String dst){
        Map<String, Integer> metricsNameDict=new HashMap<>();
        List<List<String>> metricsValue=new ArrayList<>();

        if(getMetrics(src, metricsValue, metricsNameDict)==0) {
            System.out.println(metricsNameDict.size());
            System.out.println(metricsValue);
        }
        int ATFD_index=metricsNameDict.get("ATFD");
        int WMC_index=metricsNameDict.get("WMC");
        int TCC_index=metricsNameDict.get("TCC");
        int NBM_index=metricsNameDict.get("NBM");
        int LOC_index=metricsNameDict.get("LOC");

        for(int i=0; i<metricsValue.size(); i++){
            List<String> tmp=metricsValue.get(i);
            double ATFD=Double.valueOf(tmp.get(ATFD_index));
            double WMC=Double.valueOf(tmp.get(WMC_index));
            double TCC=Double.valueOf(tmp.get(TCC_index));
            double NBM=metricsNameDict.get("NBM");
            double LOC=metricsNameDict.get("LOC");
            if(!(WMC>=47 && TCC<0.3 && ATFD>5) && (WMC>=47 && TCC<0.5 && NBM>1 && LOC>=197) ||
                    (NBM==1 && LOC>=2*197 && WMC>=2*47))
                tmp.add("true");
            else
                tmp.add("false");
            metricsValue.set(i, tmp);
        }
        List<String> lst=new ArrayList<String>();
        for(int i=0;i<metricsNameDict.size();i++){
            for(String key : metricsNameDict.keySet()){
                if(metricsNameDict.get(key)==i)
                    lst.add(key);
            }
        }
        lst.add("Brain Class");

        List<List<String>> res=new ArrayList<>();
        res.add(lst);
        for(int i=0; i<metricsValue.size();i++)
            res.add(metricsValue.get(i));
        CSVParser csvParser=new CSVParser();
        try {
            csvParser.writeCSV(res, dst);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void labelBrainMethod(String src, String dst){
        Map<String, Integer> metricsNameDict=new HashMap<>();
        List<List<String>> metricsValue=new ArrayList<>();

        if(getMetrics(src, metricsValue, metricsNameDict)==0) {
            System.out.println(metricsNameDict.size());
            System.out.println(metricsValue);
        }
        int LOC_index=metricsNameDict.get("LOC");
        int CYCLO_index=metricsNameDict.get("CYCLO");
        int MAXNESTING_index=metricsNameDict.get("MAXNESTING");
        int NOAV_index=metricsNameDict.get("NOAV");

        for(int i=0; i<metricsValue.size(); i++){
            List<String> tmp=metricsValue.get(i);
            double LOC=Double.valueOf(tmp.get(LOC_index));
            double CYCLO=Double.valueOf(tmp.get(CYCLO_index));
            double MAXNESTING=Double.valueOf(tmp.get(MAXNESTING_index));
            double NOAV=metricsNameDict.get("NOAV_index");
            if(LOC>65 && (CYCLO/LOC)>=0.24 && MAXNESTING>=5 && NOAV>8)
                tmp.add("true");
            else
                tmp.add("false");
            metricsValue.set(i, tmp);
        }
        List<String> lst=new ArrayList<String>();
        for(int i=0;i<metricsNameDict.size();i++){
            for(String key : metricsNameDict.keySet()){
                if(metricsNameDict.get(key)==i)
                    lst.add(key);
            }
        }
        lst.add("Brain Method");

        List<List<String>> res=new ArrayList<>();
        res.add(lst);
        for(int i=0; i<metricsValue.size();i++)
            res.add(metricsValue.get(i));
        CSVParser csvParser=new CSVParser();
        try {
            csvParser.writeCSV(res, dst);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMetrics(String path, List<List<String>> metricsValue, Map<String, Integer> metricsName){
        String[] type=path.split("\\.");
        String separate="";
        if(type[type.length-1].equals("txt"))
            separate="\t";
        else if(type[type.length-1].equals("csv"))
            separate=",";
        else {
            System.out.println("unhandled file type");
            exit(-1);
        }
        File file=new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String line = "";
        int lineNum=1;
        try {
            while ((line = reader.readLine()) != null)  //读取到的内容给line变量
            {
                String[] word=line.split(separate);
                if(lineNum==1) {
                    for(int i=0; i<word.length; i++ )
                        metricsName.put(word[i], i);
                }else{
                    List<String> tmp=new ArrayList<>();
                    for(int i=0; i<word.length; i++)
                        tmp.add(word[i]);
                    metricsValue.add(tmp);
                }
                lineNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
