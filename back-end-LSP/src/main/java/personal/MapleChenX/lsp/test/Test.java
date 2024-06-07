package personal.MapleChenX.lsp.test;

public class Test {
    public static void main(String[] args) {

        String a = "123%456789_98764%5312_45689";

        int i = a.lastIndexOf("_");
        System.out.println(i);
        String substring = a.substring(i);
        System.out.println(substring);

        int i1 = a.lastIndexOf("_", i - 1);
        System.out.println(i1);
        String substring1 = a.substring(i1 + 1, i);

        int i2 = a.indexOf("%", 6); // 返回的是数组下标
        System.out.println(i2);
        String substring2 = a.substring(i2); //subString是左闭右开
        System.out.println(substring2);




    }

}
