import java.util.ArrayList;
import java.util.List;

public class Operator {
    //加法
    public List<Integer> list = new ArrayList<>();
    public String result;//记录操作数结果
    public int Top;//Top分子
    public int Bottom;//Bottom分母
    public int aTop;
    public int bTop;
    public int aBottom;
    public int bBottom;
    public int cm;
    public String add(String a,String b){
        //将a,b字符串的分母转化为整形并求它们的最小公倍数,均按照分式加法运算
        initAB(a,b);//给操作数a,b初始化
        Top = aTop * cm/aBottom + bTop * cm/bBottom;
        Bottom = cm;
        result = Top +"/"+ Bottom;
        return  result;
    }
    public void initAB(String a,String b){
        //给a,b转化为分式并给分子分母赋值
        getFraction(a);
        aTop = list.get(0);
        aBottom = list.get(1);
        list.clear();
        getFraction(b);
        bTop = list.get(0);
        bBottom = list.get(1);
        list.clear();
        cm = getCM(aBottom,bBottom);//计算a,b分母的最小公倍数
    }
    public void getFraction(String str){
        //将字符串转化为分数，分子分母存在list中
        if(str == null) return ;
        if(str.contains("/")){
            String[] split = str.split("/");
            for (String s : split) {
                list.add(Integer.parseInt(s));//分子、分母加入list中
            }
        }else {
            //不是分数字符串情况下
            list.add(Integer.parseInt(str));//分子加入list中
            list.add(1);//分母为1
        }
    }
    public int getCM(int a,int b){
        //找出a,b的最小公倍数
        for(int i = (Math.max(a, b)); i<= a*b; i++){
            if(i % b == 0 && i % a == 0){
                return i;
            }
        }
        return -1;
    }
    //减法
    public String sub(String a,String b){
        //将a,b字符串的分母转化为整形并求它们的最小公倍数,均按照分式减法运算
        initAB(a,b);//给操作数a,b初始化
        Top = aTop * cm/aBottom - bTop * cm/bBottom;
        if(Top < 0){
            //判断是否结果为负，若为负，则返回空值
            return null;
        }
        Bottom = cm;
        result = Top +"/"+ Bottom;
        return  result;
    }
    //乘法
    public String multi(String a,String b){
        initAB(a,b);
        Top = aTop * cm/aBottom * bTop * cm/bBottom;
        if(aBottom == 1){
            aBottom = aBottom * cm;
        }
        if(bBottom == 1){
            bBottom = bBottom * cm;
        }
        Bottom = aBottom * bBottom;
        result = Top + "/" + Bottom;
        return result;
    }
    //除法
    public String division(String a,String b){
        initAB(a,b);
        Top = aTop * bBottom;
        Bottom = bTop * aBottom;
        result = Top + "/" + Bottom;
        return result;
    }
    public String[] strNum;
    public String[] strOperator;
    //计算
    public String calc(List<String> listNum,List<String> listOperator){
        //传入运算数和运算符进行分析计算
        //创建字符串数组方便索引所需运算符和运算数
        strNum = new String[listNum.size()];
        strOperator = new String[listOperator.size()];
        listNum.toArray(strNum);
        listOperator.toArray(strOperator);
        //遍历运算数，若无第二个运算数，则判断为运算结束，退出for循环
        for(int i = 0;strNum.length != 1;){
            if(listOperator.contains("(")){
                //括号优先级最高
                int j = listOperator.indexOf("(");//找出括号运算符的位置
                switch (strOperator[j+1]){
                    //判断括号里运算符并进行计算
                    case "+":
                        listNum.set(j, add(strNum[j],strNum[j+1]));break;
                    case "-":
                        listNum.set(j, sub(strNum[j],strNum[j+1]));break;
                    case "*":
                        listNum.set(j, multi(strNum[j],strNum[j+1]));break;
                    case "/":
                       listNum.set(j, division(strNum[j],strNum[j+1]));break;
                }
                delInitString(j+1,j+1,j,listNum,listOperator);
            }else {
                //若含有乘法除法，则优先计算
                if(listOperator.contains("*")){
                    int j = listOperator.indexOf("*");//找出乘法运算符的位置
                    if(strOperator[j].equals("*")){
                        listNum.set(j, multi(strNum[j],strNum[j+1]));
                    }
                    delInitString(j+1,j,0,listNum,listOperator);
                }else if(listOperator.contains("/")){
                    int j = listOperator.indexOf("/");//找出除法运算符的位置
                    if(strOperator[j].equals("/")){
                        listNum.set(j, division(strNum[j],strNum[j+1]));
                    }
                    delInitString(j+1,j,0,listNum,listOperator);
                }
                if(strOperator[i]!=null){
                    switch (strOperator[i]){
                        case "+":
                            listNum.set(i, add(strNum[i],strNum[i+1]));
                            delInitString(i+1,i,0,listNum,listOperator);
                            break;
                        case "-":
                            listNum.set(i, sub(strNum[i],strNum[i+1]));
                            delInitString(i+1,i,0,listNum,listOperator);
                            break;
                    }
                }
            }
        }
        result = listNum.get(listNum.size()-1);
        simplification(result);//化简
        return result;
    }
    public void delInitString(int j,int k,int l,List<String> list1,List<String> list2){
        list1.remove(j);//移除运算数
        list2.remove(k);//移除运算符
        if(l != 0){
            //若l不为0,则需额外移除括号"(",")"
            list2.remove(l+1);
            list2.remove(l);
        }
        strNum = list1.toArray(new String[0]);
        strOperator = list2.toArray(new String[0]);
    }
    //化简真分数
    public void simplification(String num){
        getFraction(num);
        int top;//分子
        int bottom;//分母
        int inter = 0;//若为带分数，则其inter为整数部分
        top = list.get(0);
        bottom = list.get(1);
        if(top > bottom){
            //若分子大于分母则转化为带分数
            inter = top / bottom;
            top = top - inter * bottom;
        }
        if(inter == 0){
            result = top + "/" + bottom;
        }else {
            result = inter +"`"+ top +"/"+ bottom;
        }
        System.out.println(result);
    }
}
