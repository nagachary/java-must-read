package memory_management.implement;

public class SampleJavaClass {

    private static int sum = 0;

    public static void main(String [] args) {

        SampleJavaClass sampleJavaClass = new SampleJavaClass();
        sum = sampleJavaClass.addition(10, 20);

        System.out.println("sum is : "+sum);
    }

    private int addition(int a, int b) {
        return a + b;
    }
}
