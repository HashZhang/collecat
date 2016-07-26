drop table if exists task;
create table task(
  id int(16) primary key auto_increment comment '主键id自增',
  initial_sql varchar(200)  comment '源语句',
  schema_used varchar(32) comment '表所在的schema',
  time_field varchar(32)  comment '抽取表时间字段，是查询抽取的关键',
  start_time timestamp  comment '起始时间',
  end_time datetime default null  comment '终止时间',
  routine_time int(16)  comment '周期：秒',
  allocate_routine varchar(16) comment '调度周期设置',
  is_active tinyint(1) comment '是否在运行',
  kafka_topic varchar(128)  comment 'kafka主题',
  kafka_url varchar(128)  comment 'kafka链接地址',
  kafka_cluster_name  varchar(32)  comment 'kafka集群名称',
  kafka_topic_tokens   varchar(128)  comment 'kafka主题token',
  kafka_message_size int(16)  comment '单条消息结果集大小',
  message_format varchar(16)  comment '结果格式，目前有csv和json两种格式',
	UNIQUE KEY (`initial_sql`, `schema_used`, `time_field`)
);

drop table if exists subtask;
create table subtask(
	id int(16) primary key auto_increment comment '主键id自增',
	task_id int(16) comment '对应的task id',
	initial_sql varchar(200)  comment '源语句',
	schema_used varchar(32) comment '表所在的schema',
  time_field varchar(32)  comment '抽取表时间字段，是查询抽取的关键',
  last_time timestamp  comment '上次生成Job最后时间',
	curr_time timestamp  comment '当前调度时间',
	end_time datetime default null comment '结束时间',
  routine_time int(16)  comment '周期：秒',
  allocate_routine varchar(16) comment '调度周期设置',
  is_active tinyint(1) comment '是否在运行',
  kafka_topic varchar(128)  comment 'kafka主题',
  kafka_url varchar(128)  comment 'kafka链接地址',
  kafka_cluster_name  varchar(32)  comment 'kafka集群名称',
  kafka_topic_tokens   varchar(128)  comment 'kafka主题token',
  kafka_message_size int(16)  comment '单条消息结果集大小',
	mysql_url varchar(128)  comment '执行库所在MySQL实例的链接地址',
  mysql_username varchar(32)  comment '执行库所在MySQL实例的用户名',
  mysql_password varchar(64)  comment '执行库所在MySQL实例的密码',
  message_format varchar(16)  comment '结果格式，目前有csv和json两种格式',
	UNIQUE KEY (`task_id`, `mysql_url`),
	key(`task_id`)
);

drop table if exists job;
create table job(
  id int(16) primary key auto_increment comment '主键id自增',
  subtask_id int(16) comment '对应的subtask id',
	time_field varchar(32)  comment '抽取表时间字段，是查询抽取的关键',
  time_field_start timestamp DEFAULT current_timestamp comment '开始时间',
  time_field_end timestamp DEFAULT current_timestamp comment '结束时间',
  job_sql varchar(1024)  comment '改写后具体job语句',
  mysql_url varchar(128)  comment '执行库所在MySQL实例的链接地址',
  mysql_username varchar(32)  comment '执行库所在MySQL实例的用户名',
  mysql_password varchar(64)  comment '执行库所在MySQL实例的密码',
  created_time timestamp  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  modified_time timestamp  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  kafka_topic varchar(128)  comment 'kafka主题',
  kafka_url varchar(128)  comment 'kafka链接地址',
  kafka_cluster_name  varchar(32)  comment 'kafka集群名称',
  kafka_topic_tokens   varchar(128)  comment 'kafka主题token',
  kafka_message_size int(16)  comment '单条消息结果集大小',
  message_format varchar(16)  comment '结果格式，目前有csv和json两种格式',
  status int(2) DEFAULT 0 comment '0未完成，1已完成,2有异常',
  node_assigned_to int(16)
);

drop table if exists node;
create table node(
  id int(16) primary key auto_increment comment '主键id自增',
  ip varchar(64),
  current_node_id varchar(128)
);