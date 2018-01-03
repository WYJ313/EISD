package CSV;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
    public static List<List<String>> readCSV(String path){
        File csv=new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String line = "";
        List<List<String>> res=new ArrayList<>();
        try {
            List<String> allString = new ArrayList<>();
            while ((line = reader.readLine()) != null)  //读取到的内容给line变量
            {
                String[] word=line.split(",");
                List<String> lst= new ArrayList<>();
                for(int i=0; i<word.length; i++)
                    lst.add(word[i]);
                res.add(lst);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    public void writeCSV(List<List<String>> lst, String path) throws IOException {
        File file = new File(path);
        if(file.exists())
            file.delete();
        else
            file.createNewFile();
        FileWriter fw = null;
        BufferedWriter writer = null;
        try {
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            for(int i=0; i<lst.size(); i++){
                List<String> line=lst.get(i);
                StringBuilder sb=new StringBuilder();
                for(int j=0; j<line.size();j++) {
                    if(j!=line.size()-1) {
                        sb.append(line.get(j)+",");
                    }else {
                        sb.append(line.get(j));
                    }
                }
                writer.write(sb.toString());
                writer.newLine();//换行
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

