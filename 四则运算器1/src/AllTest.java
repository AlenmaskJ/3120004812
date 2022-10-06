public class AllTest {
    public static void main(String[] args) {
        AddTest addTest = new AddTest();
        CalcTest calcTest = new CalcTest();
        DivisionTest divisionTest =new DivisionTest();
        MultiTest multiTest = new MultiTest();
        SimplificationTest simplificationTest = new SimplificationTest() ;
        SubTest subTest = new SubTest();
        addTest.main();
        divisionTest.main();
        multiTest.main();
        simplificationTest.main();
        subTest.main();
        calcTest.main();
    }
}
