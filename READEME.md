#java_Kafka2OpenTSDB(ByHBASE)

###该程序是通过RPC的形式往OpenTSDB写入数据的实例

###组件： Kafka+OPenTSDB（RPC）

由于com.stumbleupon.async.Deferred的特性，如果使用Spark Streaming会导致程序在运行时崩溃（原因暂不清楚），所以目前使用原始的java程序载体。

该程序主要解决高IO下的OPenTSDB延迟问题。

###该程序为1.0版本，在之后也对其做了一部分优化:

1,程序内部优化，进一步提高读写速度。

2,调整偏移量的读写机制，防止丢数据。

3,优化部署逻辑，保证kill-9程序或者程序崩溃时候，不会影响数据的一致性和准确性。


该代码设计很多原理性的东西，例如Java的多线程，OpenTSDB的读写机制，目前还没有吃透。但是这是一段我觉得非常有趣的代码。  (￣▽￣)～■干杯□～(￣▽￣) 

