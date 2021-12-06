package benchmark;



import com.xiaofan0408.common.command.StringCommand;
import com.xiaofan0408.impl1.RedisClientImpl1;
import com.xiaofan0408.impl1.RedisConnectionExOne;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class RedisBenchmark {

    @Param({"10","100"})
    private Integer length;

    private RedisClientImpl1 redisClientImpl1;

    private RedisConnectionExOne redisConnection;

    private StringCommand stringCommand;

    private  String key = "key";

    private  String value = "value";

    @Setup
    public void init(){
        redisClientImpl1 = new RedisClientImpl1("127.0.0.1",6779);
        redisConnection = redisClientImpl1.connect();
        stringCommand = redisConnection.getStringCommand();
    }

    @Benchmark
    public void testSet10kb(){
        stringCommand.set("hello","world").subscribe().dispose();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // 导入要测试的类
                .include(RedisBenchmark.class.getSimpleName())
                // 预热5轮
                .warmupIterations(1)
                // 度量10轮
                .measurementIterations(2)
                .mode(Mode.Throughput)
                .forks(1)
                .threads(2)
                .output("./log/redis1.log")
                .build();

        new Runner(opt).run();

    }

}
