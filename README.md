# SAMPLE-API
`Springboot2 + apache camel (Red Hat Fuse 7.7) + prometheus + jaeger`
## PROJECT STRUCTURE

```
.
├── .gitignore
├── README.md
├── configuration
│   ├── configmap
│   │   └── sample-api-env.yml
│   └── secret
│       └── sample-api.yml
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com
        │       └── redhat
        │           └── fuse
        │               └── sample
        │                   ├── AdminFuseApplication.java
        │                   ├── configuration
        │                   │   └── PrometheusPropertiesConfiguration.java
        │                   ├── model
        │                   │   ├── ApiResponse.java
        │                   │   ├── BaseModel.java
        │                   │   ├── ErrorMessage.java
        │                   │   └── health
        │                   │       └── Health.java
        │                   ├── processor
        │                   │   └── ExceptionProcessor.java
        │                   └── route
        │                       ├── internal
        │                       │   ├── HealthInternalRoute.java
        │                       │   └── PrometheusInternalRoute.java
        │                       └── rest
        │                           └── IntegrationRestRoute.java
        └── resources
            ├── application.yaml
            └── logback.xml
```
command: `tree -a -I '.git|.idea|.DS_Store|target'`

### APPLICATION DEFAULT ENDPOINTS

| Method | URL | Description |
| ------ | --- | ----------- |
| GET    | http://localhost:8080/actuator | Springboot actuator endpoint |
| GET    | http://localhost:8080/actuator/metrics | Springboot actuator metrics endpoint |
| GET    | http://localhost:8080/actuator/metrics/jvm.memory.used | Springboot actuator metrics endpoint for jvm.memory.used (example) |
| GET    | http://localhost:8080/actuator/prometheus | Springboot actuator prometheus endpoint |

### APPLICATION CAMEL REST ENDPOINTS

| Method | URL | Description |
| ------ | --- | ----------- |
| GET    | http://localhost:8080/api/v1/status | Application health endpoint (based on actuator health endpoint) |
| GET    | http://localhost:8080/api/v1/prometheus | Application prometheus endpoint (based on actuator prometheus endpoint) |

### MAVEN COMMAND LINE REFERENCE
```
╰> mvn dependency:tree -Dverbose
╰> mvn clean package
╰> mvn spring-boot:run
```

### (DEPLOY) JAEGER LOCAL EXAMPLE
```
docker run -d --name jaeger \
  -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
  -p 5775:5775/udp \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 14268:14268 \
  -p 14250:14250 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.20
```

Then, you can then navigate to `http://localhost:16686` to access the Jaeger UI.

Jaeger Openshift templates:
https://github.com/jaegertracing/jaeger-openshift

### (TEST) JAEGER TRACING
Open the application on port 8080.
At this point, the application will try to flush the spans into jaeger.
If there are any connection issues, you'll see something like in the application log:

```
io.jaegertracing.internal.exceptions.SenderException: Failed to flush spans.
	at io.jaegertracing.thrift.internal.senders.ThriftSender.flush(ThriftSender.java:115)
	at io.jaegertracing.internal.reporters.RemoteReporter$FlushCommand.execute(RemoteReporter.java:160)
	at io.jaegertracing.internal.reporters.RemoteReporter$QueueProcessor.run(RemoteReporter.java:182)
	at java.lang.Thread.run(Thread.java:748)
Caused by: io.jaegertracing.internal.exceptions.SenderException: Could not send 6 spans
	at io.jaegertracing.thrift.internal.senders.HttpSender.send(HttpSender.java:69)
	at io.jaegertracing.thrift.internal.senders.ThriftSender.flush(ThriftSender.java:113)
	... 3 common frames omitted
Caused by: java.net.ConnectException: Failed to connect to localhost/0:0:0:0:0:0:0:1:14268
    ...
	at io.jaegertracing.thrift.internal.senders.HttpSender.send(HttpSender.java:67)
	... 4 common frames omitted
Caused by: java.net.ConnectException: Connection refused (Connection refused)
```

otherwise, the application log will successfully create a new Span, like:
```
2020-11-05 17:22:12.892 [INFO ] i.j.i.reporters.LoggingReporter - Span reported: e47758760033eee2:e47758760033eee2:0:1 - GET
2020-11-05 17:22:12.926 [INFO ] i.j.i.reporters.LoggingReporter - Span reported: e47758760033eee2:f19cdd20eb08c400:e47758760033eee2:1 - errorHtml
```

### FUSE 7.7 SUPPORTED CONFIGURATIONS:
https://access.redhat.com/articles/348423<br>
https://access.redhat.com/articles/375743<br>
https://access.redhat.com/articles/310603<br>
