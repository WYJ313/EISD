package label;

public class OCExample {
    public static void main(String[] args){
        String src="D:/label.csv";
        String dst="D:/";
        OriginClassifier oc=new OriginClassifier();
        oc.labelGodClas(src, dst);
    }
}
