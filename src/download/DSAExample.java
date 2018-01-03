package download;

import java.io.IOException;

public class DSAExample {

    public static void main(String[] args) {
        //testLocalDataSourceAdapter();
        testWebDataSourceAdapter();
    }

    public static void testWebDataSourceAdapter() {
        DataSourceAdapter dsa=new DataSourceAdapter();
        String src="https://www.apache.org/dist/ant/source/apache-ant-1.10.1-src.tar.xz";
        String dst="D:/cp/a";
        try{
            dsa.webDataSourceAdapter(src, dst);
        }catch (IOException e){
            System.out.println("IOException:"+e.getMessage());
        }

    }

    public static void testLocalDataSourceAdapter(){
        DataSourceAdapter dsa=new DataSourceAdapter();
        String src = "D:/cp/a";
        String dst = "D:/cp/b";
        try{
            dsa.localDataSourceAdapter(src, dst);
        }catch (IOException e){
            System.out.println("IOException:"+e.getMessage());
        }

    }
}
