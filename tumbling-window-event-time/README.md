This job requires socket running on `localhost:9999` and streaming the data in the format `timestamp,int`.
For example:
```shell
1725806942861,26
1725806942912,91
1725806942967,93
1725806943020,12
1725806943073,48
1725806943130,87
1725806943181,20
1725806943233,4
1725806943288,14
1725806943342,3
1725806943397,96
1725806943449,15
1725806943504,79
.
.
.
```
<br>
Here is a Java code that can run a socket streaming the above data. As soon as you run this program, and run flink job, the job will start processing this data and save the results to output file.

## SocketRunner.java
```java
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SocketRunner {
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(9090);
        try {
            Socket socket = listener.accept();
            System.out.println("Got new connection: " + socket.toString());
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Random random = new Random();
                while(true) {
                    int num = random.nextInt(100);
                    String string = "" + System.currentTimeMillis() + "," + num;
                    System.out.println(string);
                    out.println(string);
                    Thread.sleep(50);
                }
            } finally {
                socket.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            listener.close();
        }
    }
}
```

## Run flink job
```shell
flink run /Users/aakashverma/Documents/learning/Java/apache-flink/apache-flink-examples/tumbling-window-event-time/target/tumbling-window-event-time-1.0-SNAPSHOT.jar
```
