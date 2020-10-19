### Java 字节码

.java 文件经过 javac 命令编译为 .class 文件，再通过 java 命令运行。java 命令先让类加载器从文件系统中加载字节码文件（.class），最后变成 JVM 中的类。

Java bytecode 由单字节（byte）指令组成，最多支持 256 个操作码（opcode）。实际 Java 只使用了 200 左右的操作码。

操作码分为四类：
1. 栈操作指令，包括与局部变量交互指令；
2. 程序流程控制指令；
3. 对象操作指令，包括方法调用指令；
4. 算术运算以及类型转换指令。

操作码为二进制格式，操作码助记符由类型前缀和操作名称两部分组成。例如，‘iadd’表示对整数执行加法运算，‘i’前缀代表‘integer’。
 
#####  javap 命令

javap 是标准 JDK 内置的一款工具，用于反编译 class 文件，可以获取 class 文件中的指令清单。 
 
javap 命令查看字节码，class 文件为二进制格式，javap 通过助记符翻译二进制。
编译：javac HelloByteCode.java
助记符查看字节码：javap ‐c HelloByteCode.class
字节码添加常量池信息等详细信息：javap ‐c ‐verbose HelloByteCode.class

#### 操作码说明

操作码表示的为栈操作，因为 JVM 是基于栈的计算器，每个线程有自己的线程栈（JVM Stack），一次方法调用，创建一个栈帧。栈帧由操作数栈（又一个栈），局部变量数组和一个 Class 引用组成。Class 引用指向当前方法在运行时常量池中对应的 Class。

局部变量存于本地变量数组，操作时需将变量取到操作数栈，用完放回本地变量数组。

##### 类

major version:52，minor version:0，表示 jdk 版本，这里为 jdk8。

ACC_PUBLIC，标识类为 public 类。
ACC_SUPER，用于修正 invokespecial 指令调用 super 类方法。
ACC_STATIC，static 

Constant pool，常量池，栈上的操作指向常量池的具体意义。
\#1，常量编号，用于栈上操作以引用。 
Methodref  表明这个常量指向的是一个方法。

##### 方法

方法的修饰符+名称+参数类型清单+返回值类型，为“方法签名”。

descriptor:()V，默认无参构造方法。
descriptor:([Ljava/lang/String;)V，左方括号表示数组，L 表示对象，java/lang/String 表示参数类型，V 表示方法返回值是 void。 flags: ACC_PUBLIC, ACC_STATIC，表示方法为 public 和 static。

stack=2, locals=2, args_size=1，分别表示操作数栈深度，局部变量数组槽位个数，方法的参数个数。

LineNumberTable: line 3:0，表示 java 代码第 3 行对应字节码数组序号的 0 行。

##### 操作

操作码前的数字是字节码数组序号，间隔不相等的原因是, 有一部分操作码会附带操作数, 占用字节码数组空间。

JVM 一共 5 种数据类型，a 对象引用，int，long，float，double。其他基本类型 boolean，byte，char，short 在字节码中通过 int 表示。

local variable -> stack，本地变量表放入操作数栈称为 load 操作，
stack -> local variable，操作数栈放回本地变量表称为 store 操作。

aload_1，astore_1，a 表示对象引用，_1 表示对应 localVariableTable 的 slot（槽位大于3，分开写 aload 4）。
dstore 4，表示将 double 类型的变量放回本地变量数组中（即赋值）。
iconst_1，表示一个 int 类型变量被赋值为 1。
i2d，int 转为 double。

循环指令
if_icmpge 43，icmp，ge 比较大小，超出循环则跳转至字节码数组标号 43
iinc 4,1，int 类型自增加 1
goto 18，跳转至字节码数组标号 18

方法调用指令
invokestatic，调用类的静态方法，方法调用指令中最快的一个。 
invokespecial，调用构造函数，也可以调用同一个类中的 private 方法, 以及可见的超类方法。 
invokevirtual，调用公共，受保护和 package 级的私有方法。 
invokeinterface，通过接口引用调用方法。 
invokedynamic，JDK7 新增指令，实现“动态类型语言”（Dynamically Typed Language）支持，也是 JDK8 以后支持 lambda 表达式的实现基础。
 
待补充