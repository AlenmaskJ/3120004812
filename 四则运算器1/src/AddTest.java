public class AddTest {
    //加法运算符单元测试
    public void main() {
        Operator operator = new Operator();
        String a = "44/3";
        String b = "2/3";
        String result =operator.add(a,b);
        System.out.println(result);
    }

}
