This is the test case at the link below, ported from Maven to Gradle:

    https://github.com/Geomatys/MavenModulepathBug

Run `gradle test` on the command line. The result is the same as Maven.
For reproducing on the command-line, first run `gradle jar`, then:

```
java --class-path service/build/libs/service.jar:client/build/libs/client.jar test.client.Main
```

Expected behavior:

```
java --module-path service/build/libs/service.jar --class-path client/build/libs/client.jar --add-modules ALL-MODULE-PATH test.client.Main
```
