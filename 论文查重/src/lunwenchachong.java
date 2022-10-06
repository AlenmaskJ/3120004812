import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class lunwenchachong {
    public static String textA;//原论文
    public static String textB;//待比较论文
    public static String originPath = null;//原论文绝对路径
    public static String comparePath = null;//待比较论文绝对路径
    public static String answerPath = null;//待比较论文绝对路径
    public static List<String> paragraphListA =new ArrayList<>();//原文段落
    public static List<String> paragraphListB =new ArrayList<>();//待比较段落
    public static List<String> listA;//原文词组
    public static List<String> listB;//待比较词组
    public static List<String> dict=new ArrayList<>();//所有词词典
    public static List<String> dictA=new ArrayList<>();//原论文词典
    public static List<String> dictB=new ArrayList<>();//待比较词典
    public static Map<String, Integer> map=new LinkedHashMap<>();
    public static Map<String,Integer> map1=new LinkedHashMap<>();
    public static Map<String,Integer> map2=new LinkedHashMap<>();
    private static final String SENTENSE_SPLIT_REGEX = "[。]";
    private static final String PARAGRAPH_SPLIT_REGEX = "[\n\n]";
    public static void main(String[] args) {
        try{
            //获取文件路径
            getPath(args);

            //读入文件路径
            textA = FileUtils.readFileToString(new File(originPath),"UTF-8");
            textB = FileUtils.readFileToString(new File(comparePath),"UTF-8");


            //去除特殊字符
            String  regEX="[：“”—·~#@￥%&*《》…{} ]";
            textA = textA.replaceAll(regEX, "");
            textB = textB.replaceAll(regEX, "");

            //记录最终相似度结果
            double result = 0;

            //根据自然段划分得出相似度
            parseText(textA,paragraphListA,PARAGRAPH_SPLIT_REGEX);
            parseText(textB,paragraphListB,PARAGRAPH_SPLIT_REGEX);
            result += getSimilar();
            paragraphListA.clear();
            paragraphListB.clear();

            //根据句子划分得出相似度
            parseText(textA,paragraphListA,SENTENSE_SPLIT_REGEX);
            parseText(textB,paragraphListB,SENTENSE_SPLIT_REGEX);
            result += getSimilar();
            paragraphListA.clear();
            paragraphListB.clear();

            //整体得出相似度
            result += getSentenceSimilarity(textA,textB);

            //取3次计算相似度平均值
            result = result/3;

            //取小数点后两位
            DecimalFormat decimalFormat=new DecimalFormat("#.##");
            System.out.print("重复率：");
            System.out.println(decimalFormat.format(result));
            FileUtils.write(new File(args[2]),"重复率："+decimalFormat.format(result)+"\n","UTF-8",true);


        }catch (Exception e){
            System.out.println("请输入合法路径！");
        }
    }
    //输入文件路径
    public static void getPath(String[] arg){
        System.out.println("请输入论文原文的文件的绝对路径：");
        if (arg[0].contains(" ")){
            System.out.println("请输入合法路径");
        }
            originPath = arg[0];

        System.out.println("请输入抄袭版论文的文件的绝对路径：");
            comparePath = arg[1];

        System.out.println("请输入答案相似度的文件的绝对路径：");
            answerPath = arg[2];

    }

    //计算词频
    public static void calcFrequency(){
        for (String s : listA) {
            for (Map.Entry<String, Integer> entry : map1.entrySet()) {
                if (s.equals(entry.getKey())) {
                    map1.put(entry.getKey(), entry.getValue() + 1);
                }
            }
        }
        for (String s : listB) {
            for (Map.Entry<String, Integer> entry : map2.entrySet()) {
                if (s.equals(entry.getKey())) {
                    map2.put(entry.getKey(), entry.getValue() + 1);
                }
            }
        }
    }

    //划分文字段落
    public static void parseText(String text,List<String> list,String REGEX) {
        String[] paragraphs = text.split(REGEX);
        for (String paragraph : paragraphs) {
                list.add(paragraph.trim());
        }
        list.toString().replaceAll("\\s+","");
        list.removeAll(Collections.singleton(""));
    }

    //计算具体句子的余弦相似度
    public static double getSentenceSimilarity(String text1,String text2){
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> resultA = segmenter.sentenceProcess(text1);
        List<String> resultB = segmenter.sentenceProcess(text2);
        List<String> stop_words = Arrays.asList("！","，","：","”","“","？","：“","。”","（","）","+","-","*","@","#","%","$","^","&","`","、","。","<",
                ">","《","》","——","？”","》，");
        listA =
                resultA.stream().map(String::trim).filter(o -> !stop_words.contains(o)).collect(Collectors.toList());
        listB =
                resultB.stream().map(String::trim).filter(o -> !stop_words.contains(o)).collect(Collectors.toList());

        //获取词袋
        getWordDict();

        //初始化map
        initMap(map, dictA);
        initMap(map1, dict);
        initMap(map2, dict);

        //遍历map计算出词频
        calcFrequency();
        List<Integer> list1 =new ArrayList<>(dict.size());
        List<Integer> list2 =new ArrayList<>(dict.size());

        //计算词频向量
        calcVector(list1, 0);
        calcVector(list2, 1);

        //计算余弦相似度
        double result;
        result = calcCosine(list1,list2);
        return result;
    }

    //获取词袋
    public static  void getWordDict(){
        dictA = removeDuplicate(listA);
        dictB = removeDuplicate(listB);
        dict  = dictA;
        dict.addAll(dictB); // 得到  listA, listB 的并集。
        dict = removeDuplicate(dict);
    }

    //去除list中重复字符
    public static ArrayList<String> removeDuplicate(List<String> list){
        ArrayList<String> listTemp = new ArrayList<>();
        for (String o : list) {
            if (!listTemp.contains(o)) {
                listTemp.add(o);
            }
        }
        return listTemp;
    }

    //初始化map
    public static void initMap(Map<String,Integer> map, List<String> list){
        for (String o : list) {
            map.put(o, 0);
        }
    }

    //计算词频向量
    public static void calcVector(List<Integer> list, int a){
        if(a==0){
            for(Map.Entry<String, Integer> entry :map1.entrySet()){
                list.add(entry.getValue());
            }
        }
        if(a==1){
            for(Map.Entry<String, Integer> entry :map2.entrySet()){
                list.add(entry.getValue());
            }
        }
    }

    //余弦相似度算法
    public static double calcCosine(List<Integer> list1, List<Integer> list2){
        double sum = 0;
        double sq1 = 0;
        double sq2 = 0;
        double result;
        for (int i = 0; i <  list1.size(); i++) {
            sum += list1.get(i)*list2.get(i);
            sq1 += list1.get(i)*list1.get(i);
            sq2 += list2.get(i)*list2.get(i);
        }
        result = sum/(Math.sqrt(sq1)*Math.sqrt(sq2));
        return result;
    }

    //计算相似度
    public static double getSimilar(){
        int size =Math.min(paragraphListA.size(),paragraphListB.size());
        double similarSum =0;
        for (int i = 0; i < size ; i++) {
            similarSum += getSentenceSimilarity(paragraphListA.get(i),paragraphListB.get(i));
        }
        return similarSum/size;
    }
}
