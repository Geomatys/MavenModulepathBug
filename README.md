Small demonstration about the necessity to compile with `--module-path` option.
Prepare the test:

```
mvn package
mkdir client/target/tmp
```

Compile on the command line:

```
javac --module-path service/target/service-1.0.jar -d client/target/tmp client/src/main/java/module-info.java client/src/main/java/test/client/Main.java
```

Following work as expected. Try the same command, but replacing the `--module-path` by `--class-path`:

```
javac --class-path service/target/service-1.0.jar -d client/target/tmp client/src/main/java/module-info.java client/src/main/java/test/client/Main.java
```

Result is (as expected):

```
client/src/main/java/module-info.java:2: error: module not found: service
    requires service;
             ^
1 error
```
