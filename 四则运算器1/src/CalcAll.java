import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalcAll {
    public void calcAll(List<String[]> listNumArray,List<char[]> listOperatorArray ) {
        Operator operator = new Operator();
        for (int i = 0; i < listNumArray.size(); i++) {
            String str = new String(listOperatorArray.get(i));
            List<String> listOperator = new ArrayList<>();
            listOperator.add(str);
            List<String> listNum = Arrays.asList(listNumArray.get(i));
            operator.calc(listNum,listOperator);
        }
    }
}
