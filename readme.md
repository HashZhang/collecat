#Collecat - 涅槃数据库抽取系统
##背景介绍
涅槃项目分布式数据库依赖于MyCat构建。由于需要出报表以及与其他系统对接，我们需要将业务数据库中的数据导出到不同的大数据平台或其他系统。
由此我们想到了将数据导入至KafKa，然后由其他系统消费。
目前市面上的ETL工具例如Informatica等有些提供了将数据库中数据抽取出来到KafKa中的功能，但是没有一种针对分布式数据库的。
于是我们针对MyCat开发了按照MyCat配置信息直接抽取MyCat后台分片的这样一个系统
##整体架构
![架构图](.\document\img\image1.png)
采用Manager-Worker架构，Collecat-Manager负责发送抽取任务到ZK，Collecat-Node从ZK上获取抽取任务并执行
Collecat-Manager依赖于数据库持久化一些任务信息还有Node信息，依赖于ZK发布并传递任务信息。
Collecat-Node依赖于ZK注册并获取任务。
##抽象概念
Collecat-Manager将抽取任务抽象成了如下所示的主要三个元素：Task，SubTask，Job
![Collecat-Manager工作图](.\document\img\image2.png)
我们将这三个抽象概念的主要元素在上图中标识出来了。用户将Task提交到Collecat-Manager，这个Task主要包含用户要抽取的SQL语句。
这时Collecat-Manager根据MyCat配置找到该表的分片配置还有后台分片数据库连接串，根据分片，将task拆分成等于分片个数的Subtask。Subtask中包含具体要抽取的分片信息
Collecat-Manager根据Subtask，不断生成Job，注意，这里根据用户配置每个Job的SQL语句会被改写加上时间字段还有对应的时间段（这个Job生成机制之后会讲），发到ZK上的Job池。
每个Collecat-node会从ZK上的每个Job池中抢夺Job，抢夺后，从ZK上读取Job信息，并执行Job的SQL语句写入对应的KafKa Topic（这个其实是在用户提交Task时，task里面配置的）中
![Collecat-Node工作图](.\document\img\image3.png)