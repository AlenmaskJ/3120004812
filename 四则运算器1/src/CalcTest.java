import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalcTest {
    public void main() {
        Operator operator = new Operator();
        List<String> listNum = new ArrayList<>();
        List<String> listOperator = new ArrayList<>();
        String result;
        Collections.addAll(listNum,"7/8","3","6","1","1","1","1","1","1");
        Collections.addAll(listOperator,"/","*","+","(","/",")","(","-",")","(","+",")","(","*",")","-");

        result = operator.calc(listNum,listOperator);
        System.out.println(result);
    }
}
