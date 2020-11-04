## 第一题 使用 GCLogAnalysis.java 演练串行/并行/CMS/G1案例

### GC 日志解读

#### 打印 GC 日志

`java -XX:+PrintGCDetails GCLogAnalysis `

#### 导出 GC 日志，包含时间戳

`java -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

#### 截取一次 YGC 日志
```
2020-10-25T22:57:48.923+0800: 0.502: [GC (Allocation Failure) [PSYoungGen: 33159K->5105K(38400K)] 33159K->8849K(125952K), 
0.0042466 secs] [Times: user=0.03 sys=0.03,real=0.00 secs]
```
2020-10-25T22:57:48.923+0800，GC 事件开始时间，+0800 表示东八区。

0.502，JVM 启动到发生 GC 的间隔时间，单位秒。

GC，区分 Minor GC 还是 Full GC 的标志，表示 Minor GC。

Allocation Failure，（内存）分配失败，触发 GC 的原因。

PSYoungGen，垃圾收集器名，并行的标记-复制（mark-copy）算法。

33159K->5105K(38400K)，年轻代从 33M 到 5M，总 38M。

33159K->8849K(125952K)，整个堆从 33M 到 8M，总 125M。年轻代少了 28M 但总共却只少了 25M，因为 3M 去了老年代。

0.0042466 secs，GC 用时 4ms。

[Times: user=0.03 sys=0.03, real=0.00 secs]，user-所有 GC 线程所消耗的 CPU 时间之和，sys-GC 过程中操作系统调用和系统等待事件所消耗的时间，real-应用程序暂停的时间。

#### 截取一次 FGC 日志
```
2020-10-25T22:57:49.088+0800: 0.667: [Full GC (Ergonomics) [PSYoungGen: 35828K->17842K(163840K)] [ParOldGen: 90981K->
90862K(162304K)] 126809K->108705K(326144K), [Metaspace: 2617K->2617K(1056768K)], 0.0273695 secs] 
[Times: user=0.09 sys=0.00, real=0.03 secs]
```
Full GC，清理年轻代和老年代。

Ergonomics，人机工程学，JVM 认为需进行垃圾回收。

ParOldGen，垃圾收集器名，并行的标记-清除-整理（mark-sweep-compact）算法。年轻代大量减少，老年代几乎没有回收。

Metaspace: 2617K->2617K(1056768K)，Metaspace 区没有回收。

0.0273695 secs，用时 27ms。


### 不同 GC 对比

日志文件位于01文件夹中

#### 串行 GC

`java -XX:+UseSerialGC -Xms256m -Xmx256m -Xloggc:gc-SerialGC.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

YGC 和 FGC 时间较长。老年代未满前，每次 YGC 年轻代减少明显，且效果大致相同，堆总量增加。老年代满后、回收率尚可时，GC 中有年轻代 DefNew 和老年代 Tenured，但 DefNew 不减少内存。老年代满后、回收率低下时，FGC 持续发生，YGC 失效，年轻代内存不减少，老年代少许减少。重复时间较长的 FGC 相当于程序奔溃。

#### 并行 GC

`java -XX:+UseParallelGC -Xms512m -Xmx512m -Xloggc:gc-ParallelGC.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

YGC 比串行执行时间减少明显，年轻代每次 YGC 后留存内存缓慢增加。FGC 后年轻代清空，老年代减少。老年代回收率低下时，FGC 持续发生。

#### CMS GC

`java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -Xloggc:gc-CMSGC.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

YGC 与并行 GC 一致。FGC 变成 CMS 并发 GC，测试机器 4 核 16 线程，所以默认 CMS 为 4 并发。

CMS GC 分为 CMS Initial Mark（初始标记），CMS-concurrent-mark（并发标记），CMS-concurrent-preclean（并发预清理），CMS-concurrent-abortable-preclean（并发可终止预清理），CMS Final Remark（最终标记），CMS-concurrent-sweep（并发清除），CMS-concurrent-reset（并发重置）。前四个步骤打印每个并发日志，其中穿插着 YGC，说明业务依旧运行。有 STW 只有初始标记和最终标记两个阶段。CMS GC 时间比并行 FGC 时间减少。

#### G1 GC

`java -XX:+UseG1GC -Xms512m -Xmx512m -Xloggc:gc-G1.demo.log -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis`

(G1 Evacuation Pause) (young)，纯转移暂停年轻代 GC，时间比并行 GC 减少，不需要等年轻代快满时再 GC。

(G1 Evacuation Pause) (mixed)，年轻代和老年代混合转移暂停 GC，时间也很短。 

并发标记分为 initial-mark（初始标记），concurrent-root-region-scan-start（Root区扫描），concurrent-mark-start（并发标记），remark（再次标记），cleanup（清理）。

#### 补充

堆配置过小，发生 OOM

`java -Xmx128m -XX:+PrintGCDetails GCLogAnalysis`

使用 128m，512m，1024m，2g，4g 不同内存，GC 次数减少，发生 OOM 概率下降，每次 GC 时间变长。

不配置 -Xms，或者与 -Xmx 大小不一致，第一次 GC 会提前。

## 第二题 使用压测工具（wrk或sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 案例

### 压测


压测命令，每秒 20 个并发，持续 60 秒

`sb -u http://localhost:8088/api/hello -c 20 -N 60`

部分测试数据

`java -jar -Xms512m -Xmx512m .\gateway-server-0.0.1-SNAPSHOT.jar`
```
218070  (RPS: 3361)1)
---------------Finished!----------------
Finished at 2020/10/28 20:56:19 (took 00:01:05.0924628)
Status 200:    218078

RPS: 3560.3 (requests/second)
Max: 246ms
Min: 0ms
Avg: 0.3ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 1ms
  95%   below 2ms
  98%   below 3ms
  99%   below 5ms
99.9%   below 11ms
```
`java -jar -Xms1g -Xmx1g .\gateway-server-0.0.1-SNAPSHOT.jar`
```
222391  (RPS: 3403.3)
---------------Finished!----------------
Finished at 2020/10/28 20:46:40 (took 00:01:05.4599195)
Status 200:    222398

RPS: 3638 (requests/second)
Max: 300ms
Min: 0ms
Avg: 0.3ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 1ms
  95%   below 2ms
  98%   below 3ms
  99%   below 5ms
99.9%   below 14ms
```
`java -jar -Xms4g -Xmx4g .\gateway-server-0.0.1-SNAPSHOT.jar`
```
253894  (RPS: 3914.1)
---------------Finished!----------------
Finished at 2020/10/28 21:02:40 (took 00:01:05.0695611)
Status 200:    253894

RPS: 4146.7 (requests/second)
Max: 119ms
Min: 0ms
Avg: 0.2ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 0ms
  95%   below 1ms
  98%   below 3ms
  99%   below 4ms
99.9%   below 8ms
```

`java -jar -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m .\gateway-server-0.0.1-SNAPSHOT.jar`
```
214557  (RPS: 3315.5)
---------------Finished!----------------
Finished at 2020/10/28 21:26:53 (took 00:01:04.9069217)
Status 200:    214559

RPS: 3505.8 (requests/second)
Max: 214ms
Min: 0ms
Avg: 0.3ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 1ms
  95%   below 2ms
  98%   below 4ms
  99%   below 5ms
99.9%   below 15ms
```
`java -jar -XX:+UseConcMarkSweepGC -Xms1g -Xmx1g .\gateway-server-0.0.1-SNAPSHOT.jar`
```
220202  (RPS: 3393.4)
---------------Finished!----------------
Finished at 2020/10/28 20:40:19 (took 00:01:05.0643463)
Status 200:    220202

RPS: 3598.6 (requests/second)
Max: 206ms
Min: 0ms
Avg: 0.3ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 1ms
  95%   below 2ms
  98%   below 3ms
  99%   below 5ms
99.9%   below 11ms
```
`java -jar -XX:+UseConcMarkSweepGC -Xms4g -Xmx4g .\gateway-server-0.0.1-SNAPSHOT.jar`
```
229998  (RPS: 3552.2)
---------------Finished!----------------
Finished at 2020/10/28 21:29:10 (took 00:01:04.8526304)
Status 200:    230000

RPS: 3763 (requests/second)
Max: 215ms
Min: 0ms
Avg: 0.3ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 1ms
  95%   below 2ms
  98%   below 3ms
  99%   below 5ms
99.9%   below 15ms
```

`java -jar -XX:+UseG1GC -Xms512m -Xmx512m .\gateway-server-0.0.1-SNAPSHOT.jar`
```
217631  (RPS: 3345.4)
---------------Finished!----------------
Finished at 2020/10/28 21:23:46 (took 00:01:05.1112473)
Status 200:    217632

RPS: 3563.4 (requests/second)
Max: 308ms
Min: 0ms
Avg: 0.3ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 1ms
  95%   below 2ms
  98%   below 3ms
  99%   below 5ms
99.9%   below 12ms
```
`java -jar -XX:+UseG1GC -Xms1g -Xmx1g .\gateway-server-0.0.1-SNAPSHOT.jar`
```
225222  (RPS: 3459.7)
---------------Finished!----------------
Finished at 2020/10/28 20:36:09 (took 00:01:05.2285466)
Status 200:    225224

RPS: 3683.3 (requests/second)
Max: 70ms
Min: 0ms
Avg: 0.3ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 1ms
  95%   below 2ms
  98%   below 3ms
  99%   below 5ms
99.9%   below 9ms
```
`java -jar -XX:+UseG1GC -Xms4g -Xmx4g .\gateway-server-0.0.1-SNAPSHOT.jar`
```
254853  (RPS: 3927.9)
---------------Finished!----------------
Finished at 2020/10/28 21:08:16 (took 00:01:05.0062726)
Status 200:    254853

RPS: 4165.6 (requests/second)
Max: 209ms
Min: 0ms
Avg: 0.2ms

  50%   below 0ms
  60%   below 0ms
  70%   below 0ms
  80%   below 0ms
  90%   below 1ms
  95%   below 1ms
  98%   below 3ms
  99%   below 4ms
99.9%   below 8ms
```

随着内存变大，三种 GC 的吞吐量都会变大。CMS GC 的吞吐量表现较差，平行 GC 的最大延迟最大，4g 内存下，G1 GC 的吞吐量和延迟综合性能最好。

*测试机上因同时运行其他软件，不能很好的控制变量，相同场景多次测试会出现差别较大的情况。

## 第三题 使用 HttpClient 或 OkHttp 访问 http://localhost:8801

```
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        String url = "http://localhost:8801";

        // httpclient
        String res = httpClientdoGet(url);
        System.out.println("[httpclient response]==>"+res);

        // okhttp
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            System.out.println("[okhttp response]==>"+response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String httpClientdoGet(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                return EntityUtils.toString(responseEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

```
