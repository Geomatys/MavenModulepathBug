Small demonstration about the necessity to compile with `--module-path` option.
Prepare the test:

```
mvn package
mkdir target
```

# Work: module-path
Test the compilation on the the command line.
The following works as expected:

```
javac --module-path service/target/service-1.0.jar -d target \
      client/src/main/java/module-info.java \
      client/src/main/java/test/client/Main.java
```

Test partial compilation. In this example, only the `Main.java` file is recompiled.
It still works as expected (the `Main.class` timestamp can be checked for confirmation).

```
touch client/src/main/java/test/client/Main.java
javac --module-path service/target/service-1.0.jar -d target \
      client/src/main/java/test/client/Main.java
```

# Does not work: class-path
Try the same command than above, but replacing the `--module-path` by `--class-path`.
This is the only change.

```
javac --class-path service/target/service-1.0.jar -d target \
      client/src/main/java/module-info.java \
      client/src/main/java/test/client/Main.java
```

Result is (as expected):

```
client/src/main/java/module-info.java:2: error: module not found: service
    requires service;
             ^
1 error
```

Try partial compilation, without the `module-info` line.
The error is similar, the only difference is that the `module-info.java` file is not mentioned.
But the Java compiler has nevertheless used the `module-info.class` file.

```
error: module not found: service
1 error
```

If the `module-info.java` file is deleted, then the compilation works:

```
rm target/module-info.class
javac --class-path service/target/service-1.0.jar -d target \
      client/src/main/java/test/client/Main.java
```

But there is no longer any check that the code is not accessing non-exported packages.
Even if `module-info.class` is injected after the compilation, compile-time safety is lost.
