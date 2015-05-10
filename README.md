# comfirm-perf
confirm benchmark test with rabbitmq.

## Usage

###1.  set config.

> $ cd comfirm-perf
$ vim src/main/java/MQConfig.java
$ mvn clean compile

###2. start consumer

> $ mvn exec:java -Dexec.mainClass="Recver"

###3. start producer

> $ mvn exec:java -Dexec.mainClass="ConfirmSender"

or

> $ mvn exec:java -Dexec.mainClass="BatchConfirmSender"

or

> $ mvn exec:java -Dexec.mainClass="AsyncConfirmSender"

