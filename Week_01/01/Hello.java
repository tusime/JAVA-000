/**
 * 写一个简单的 Hello.java，涉及基本类型，四则运行，if 和 for，分析一下对应的字节码
 */

public class Hello {
    public static void main(String[] args) {
        int a = 1;
        int b = a + 2;
        int c = 0;
        String str = "str";
        if(a<b) {
            c = a * b + 6;
        }
        for (int i=0; i<3; i++) {
            c--;
        }
    }
}

