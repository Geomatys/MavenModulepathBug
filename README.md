This project is a small test case demonstrating a problem
with the way that Maven 3.8.6 handles modularized dependencies,
i.e. JAR files containing a `module-info.class` file.
This problem applies also to Gradle 8.2.1.
The problem is reproduced on the command-line with the wrong options given to the `java` command,
and the expected behavior is reproduced with the correct options that we need Maven to provide.

# The problem
When invoking Java tools such as `java` or `javac`,
the project dependencies can be put either on the class-path or on the module-path
using the command-line `--class-path` and `--module-path` options respectively.
Maven and Gradle use automatically the module-path if all the following conditions are true:

1. the dependency is modularized (i.e. contains a `module-info.class` file or an `Automatic-Module-Name` attribute in `MANIFEST.MF`), and
2. the project using the dependency is itself modularized.

Condition #1 is fine, but #2 is problematic.
The fact that a dependency is declared on the class-path rather than the module-path
changes the way that `java.util.ServiceLoader` discovers the provided services.

* If the dependency is on the class-path, `ServiceLoader` scans the content of `META-INF/services` directory.
* If the dependency is on the module-path, `ServiceLoader` uses the declarations in `module-info.class`.

Even if condition #2 is false (i.e. a project is not modularized),
modularized dependencies still need to be declared on the module-path
_for allowing the dependency to discover its own services,
or the services of a transitive modularized dependency._
If a modularized dependency is put on the class-path instead,
it has consequence not only for the project using that dependency,
**but also for the dependency itself, which become unable to use its own `module-info.class`.**

# Demonstration
This test case contains two Maven modules, named `service` and `client`.
The first Maven module declares a dummy services with 4 providers, named _A_, _B_, _C_ and _D_.
Providers _A_ and _D_ are declared in `module-info`.
Providers _B_ and _C_ are declared in `META-INF/services`.
A `ShowMyServices` class lists the services discovered by `java.util.ServiceLoader`.

The second Maven module has only a `main` method invoking `ShowMyServices`.
This second module intentionally has no `module-info.java` file.
The use case is a big module that we cannot modularize immediately
(because modularization brings stronger encapsulation,
which may require API changes in the project to modularize),
but still want to use modularized dependencies.
The test can be run as below:

```bash
git clone https://github.com/Geomatys/MavenModulepathBug
cd MavenModulepathBug
mvn install
```

During test execution, the following is printed:

```
Running test.client.MainTest
Start searching for services...
Provider B declared in META-INF.
Provider C declared in META-INF.
Done.
The dependency has been loaded as an unnamed module.
Consequently its `module-info` file has been ignored,
and the `META-INF/services` directory is used instead.
```

The above test demonstrates that `module-info` has been ignored in the context of JUnit test execution.
The following demonstrates that `module-info` is also ignored in the context of application execution:

```bash
cd client
mvn exec:java
cd ..
```

## Expected behavior
The Maven wrong behavior can be reproduced on the command-line as below.
All commands on this page use Unix syntax.
For execution on Windows, replace `/` by `\` and `:` by `;`:

```bash
java --class-path service/target/service-1.0.jar:client/target/client-1.0.jar test.client.Main
```

The expected behavior can be reproduced with the following command-line.
The main difference is that the `service.jar` dependency is moved from class-path to module-path:

```bash
java --module-path service/target/service-1.0.jar --class-path client/target/client-1.0.jar --add-modules ALL-MODULE-PATH test.client.Main
```

Above command-line produces the following output:

```
Start searching for services...
Provider A declared in module-info.
Provider D declared in module-info.
Done.
The dependency has been loaded as named module. Great!
This is what we need for the `module-info` to be used.
```

# Conclusion
Unless Maven provides configuration options that we did not see,
the way that Maven decides what to put on `--class-path` and what to put on `--module-path`
is a quasi-blocker issue for gradual modularisation of large projects.
This is because Maven choices break usages of `java.util.ServiceLoader` in the dependencies themselves.
The workaround for library developers is to declare all service providers in _both_
`module-info` and `META-INF/services`, with the risk of inconsistencies.
This workaround forces developers to renounce to the usage of `provider()` static methods
(that method was for for ensuring that singleton provider instances are used),
because the `provider()` static method works only for providers declared in `module-info`.

Ideally, developers should have explicit control on whether to put a dependency on the class-path or module-path.
There is scenarios where a developer way want to force Maven to put a dependency on the module-path
even for a non-modularized module, for example if the developer really wants automatic module.
Conversely, forcing a modularized dependency to be on the class-path may be useful for testing purposes,
for example for replacing the service providers declared in that module by patched services declared in
`META-INF/services` elsewhere (it does not need to be in the patched module).

# Workaround
This repository contains a `workaround` sub-directory with a code generator that developers can use.
The main method expect the following arguments:

* `--target=dir` where `dir` is the directory where to write generated files.
* `--package=name` (optional) where `name` is the Java package name of the Java code to generate.
* Path to JAR files (any number of them).

The program parses de `module-info.class` entries of all specified JAR files
and generates a `META-INF/services/` directory with all service providers found.
If a service provider declares a public static `provider()` method,
then the program also generates a `java` sub-directory with Java code for wrappers.
Those wrappers redirect all methods of the service interface to the same methods of
the provider obtained by a call to the `provider()` statc method.
