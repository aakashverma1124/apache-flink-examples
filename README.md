## Prerequisite
- [Install Apache Flink](https://nightlies.apache.org/flink/flink-docs-stable/docs/try-flink/local_installation/)
- Start the cluster (follow the instruction from official documentation)
- Have JDK 11 installed
- You can also [install Flink using Homebrew](https://formulae.brew.sh/formula/apache-flink)

## Create JAR
```shell
mvn clean package
```

## To start a Socket
```shell
nc -l 9999
```

## To run Flink job, open a new terminal and submit Flink job
- If you have installed Flink using Homebrew the below command should work.
```shell
flink run /path/to/jarfile
```
- otherwise, if you have installed flink using Binary.
```shell
./bin/flink run /path/to/jarfile
```

## Join [Discord](https://discord.innoskrit.in) in case you have any query.