
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，
 * 此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件。
 * */

public class CustomClassloader extends ClassLoader {

    public static void main(String[] args) {
        try {
            // new CustomClassloader().findClass("HelloWorld").newInstance();
            // 加载并初始化hello类
            Object helloClass = new CustomClassloader().findClass("Hello").newInstance();
            Method helloMethod = helloClass.getClass().getMethod("hello");
            helloMethod.invoke(helloClass);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) {
//        String base64 = Base64.getEncoder().encodeToString(betys);
//        String helloBase64 = "yv66vgAAADQAHAoABgAOCQAPABAIABEKABIAEwcAFAcAFQEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBAAg8Y2xpbml0PgEAClNvdXJjZUZpbGUBAA9IZWxsb1dvcmxkLmphdmEMAAcACAcAFgwAFwAYAQALaGVsbG8gd29ybGQHABkMABoAGwEACkhlbGxvV29ybGQBABBqYXZhL2xhbmcvT2JqZWN0AQAQamF2YS9sYW5nL1N5c3RlbQEAA291dAEAFUxqYXZhL2lvL1ByaW50U3RyZWFtOwEAE2phdmEvaW8vUHJpbnRTdHJlYW0BAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWACEABQAGAAAAAAACAAEABwAIAAEACQAAAB0AAQABAAAABSq3AAGxAAAAAQAKAAAABgABAAAAAQAIAAsACAABAAkAAAAlAAIAAAAAAAmyAAISA7YABLEAAAABAAoAAAAKAAIAAAADAAgABAABAAwAAAACAA0=";
//        byte[] bytes = decodeBase64(helloBase64);
//        Class clazz = defineClass(name, bytes, 0, bytes.length);

        String localpath = System.getProperty("user.dir");
        // Hello.xlass 位置
        String path = "file:" + localpath.substring(localpath.indexOf(":")+1).replace("\\","/") + "/02/Hello.xlass";
        byte[] bytes = decodeFileHello(path);
        Class clazz = defineClass(name, bytes, 0, bytes.length);
        return clazz;
    }

    /**
     * 解码Base64编码的字符串到byte数组
     * @param base64
     * @return
     */
    public byte[] decodeBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * 解码以x=255-x形式加密的Base64编码文件到byte数组
     * @param path
     * @return
     */
    public byte[] decodeFileHello(String path) {
        try {
            Path pa = Paths.get(new URI(path));
            byte[] betys = Files.readAllBytes(pa);
            for (int i=0;i<betys.length;i++) {
                betys[i] = (byte) (255 - betys[i]);
            }
            return betys;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}