$ javap -c -verbose Hello.class
````
Classfile /G:/code/java-study-project/JAVA-000/Week_01/Hello.class
  Last modified 2020-10-18; size 436 bytes
  MD5 checksum f22b5681723191d0ab83672d4291f109
  Compiled from "Hello.java"
public class Hello
  minor version: 0
  major version: 52		// jdk 版本，8
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:			// 常量池，格式：常量编号=常量类型 常量具体含义或指向其他常量 注释
   #1 = Methodref          #4.#16         // java/lang/Object."<init>":()V
   #2 = String             #17            // str
   #3 = Class              #18            // Hello
   #4 = Class              #19            // java/lang/Object
   #5 = Utf8               <init>
   #6 = Utf8               ()V
   #7 = Utf8               Code
   #8 = Utf8               LineNumberTable
   #9 = Utf8               main
  #10 = Utf8               ([Ljava/lang/String;)V
  #11 = Utf8               StackMapTable
  #12 = Class              #20            // "[Ljava/lang/String;"
  #13 = Class              #21            // java/lang/String
  #14 = Utf8               SourceFile
  #15 = Utf8               Hello.java
  #16 = NameAndType        #5:#6          // "<init>":()V
  #17 = Utf8               str
  #18 = Utf8               Hello
  #19 = Utf8               java/lang/Object
  #20 = Utf8               [Ljava/lang/String;
  #21 = Utf8               java/lang/String
{
  public Hello();       // 自动添加的无参构造方法
    descriptor: ()V     // 方法签名，()-无参，V-返回值为void
    flags: ACC_PUBLIC   // 访问控制，ACC_PUBLIC-public
    Code:
      stack=1, locals=1, args_size=1    // stack-操作数栈深度，locals-局部变量数组槽位个数，args_size-方法参数个数（构造方法有this参数）
         0: aload_0                     // 0:-字节码数组序号，a-对象，load-本地变量数组放入操作数栈，入栈
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 1: 0                       // java代码第1行对应字节码数组序号0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V      // [-数组，L-对象，java/lang/String-参数类型，V-方法返回值void
    flags: ACC_PUBLIC, ACC_STATIC           // 访问控制，ACC_PUBLIC-public，ACC_STATIC-static
    Code:
      stack=2, locals=6, args_size=1        // 操作数栈深度2，本地变量数组localVariableTable槽位数为6，方法参数1（static方法没有this参数）
         0: iconst_1                        // int常量1放入操作数栈，int a = 1;
         1: istore_1                        // store-操作数栈放回本地变量数组，出栈，_1-本地变量数组的slot位，一个int类型变量出栈
         2: iload_1                         // load-本地变量数组放入操作数栈，入栈，store和load后的数字表示本地变量数组的slot位，int b = a + 2;
         3: iconst_2
         4: iadd                            // 栈中int类型进行相加操作
         5: istore_2
         6: iconst_0                        // int c = 0;
         7: istore_3
         8: ldc           #2                  // String str，String str = "str";
        10: astore        4                 // 4-store和load的槽位大于3，分开写
        12: iload_1                         // if(a<b) 
        13: iload_2
        14: if_icmpge     24                // 比较栈中数字，结果为fasle跳转至字节码序号24
        17: iload_1
        18: iload_2                         // c = a * b + 6;
        19: imul                            // 栈中int类型进行相乘操作
        20: bipush        6                 // int常量6入栈，const简写指令支持数字1-5
        22: iadd
        23: istore_3
        24: iconst_0                        // for (int i=0; i<3; i++)
        25: istore        5
        27: iload         5
        29: iconst_3
        30: if_icmpge     42
        33: iinc          3, -1             // 本地变量数组槽位3，减1，c--;
        36: iinc          5, 1              // 本地变量数组槽位5，加1，for (int i=0; i<3; i++)
        39: goto          27                // 跳转至27
        42: return
      LineNumberTable:
        line 3: 0
        line 4: 2
        line 5: 6
        line 6: 8
        line 7: 12
        line 8: 17
        line 10: 24
        line 11: 33
        line 10: 36
        line 13: 42
      StackMapTable: number_of_entries = 3
        frame_type = 255 /* full_frame */
          offset_delta = 23
          locals = [ class "[Ljava/lang/String;", int, int, int, class java/lang/String ]
          stack = []
        frame_type = 252 /* append */
          offset_delta = 2
          locals = [ int ]
        frame_type = 250 /* chop */
          offset_delta = 14
}
SourceFile: "Hello.java"
````