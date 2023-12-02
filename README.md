# Module Source Hierarchy purpose

This branch demonstrates the purpose of a Java compiler (`javac`) feature named
[Module Source Hierarchy](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javac.html#module-source-hierarchy).
This branch contains 3 modules named _a_, _b_ and _c_ where _a_ is a dependency of _b_, and _b_ is a dependency of _c_.
The source files are organized as classical Gradle projects.
For example the `build.gradle` file of module _b_ is simply as below:

```groovy
apply plugin: 'java'
description = 'Module B'
dependencies {
    implementation(project(":a"))
}
```

The `module-info.java` file of module `b` is as below.
Note that this file contains a _forward_ reference to module _c_,
because _c_ is a _dependent_ (not a dependency) of module _b_.
In other words, we have a cyclic graph of modules.

```java
module b {
    requires a;
    exports b.api;                  // Package accessible by everyone.
    exports b.internal to a, c;     // Package accessible only by selected modules.
}
```

Forward references like _c_ above are allowed in a limited number of Java statements:
`exports`, `opens` and Javadoc `@see`, `@link` and `@linkplain` tags.
For example, referencing _c_ in the `requires` statement would cause a compiler error.
But referencing _c_ in the `exports` statement is valid.
Actually, if we ignore Java reflection, useful `exports` statements can only contain forward references.
Exporting to module _a_ is useless (except if module _a_ does reflection) because _a_ cannot requires module _b_.
Forward references are allowed by necessity in `exports` statements, even at the cost of cyclic graphs,
but are not allowed in `requires` statements.

## Test with classical Gradle module
Run the following commands in a Unix shell (tested with Gradle 8.3 and Java 21):

```bash
git clone https://github.com/Geomatys/MavenModulepathBug cyclic-graph
cd cyclic-graph
git checkout cyclic-graph/gradle-modules
gradle compileJava
```

The compilation succeed, but with the following warning:

```
> Task :b:compileJava
b/src/main/java/module-info.java:4: warning: [module] module not found: c
    exports b.internal to a, c;     // Package accessible only by selected modules.
                             ^
1 warning
```

## Test with Javadoc check enabled
The following commands test a case identical to the previous test,
with only the `-Xdoclint` compiler option added in module _a_:

```bash
gradle clean
git checkout cyclic-graph/gradle-modules-doclint
gradle compileJava
```

Now, the compilation fails with a fatal error:

```
> Task :a:compileJava FAILED
a/src/main/java/a/api/A.java:4: error: reference not found
 * @see c.api.C
^
1 error
```

## Test with Module Source Hierarchy
The following commands change absolutely nothing to Java source code and directory hierarchy.
This test demonstrates that the https://github.com/gradle/gradle/issues/25974 issue is not
about the `src/main/java` hierarchy convention. The tag only changes Gradle configuration.
It removes all `build.gradle` files in modules _a_, _b_, _c_ and replaces them by a single
`build.gradle` file in the root directory. The `-Xdoclint` compiler option is still present.
This build configuration gives all modules together to the Java compiler,
in one single call to `javac`,
and lets the compiler manages inter-dependencies between modules.

```bash
gradle clean
git checkout cyclic-graph/module-source-hierarchy
gradle compileJava
```

All Java compiler warnings and errors are gone!

* No more "module not found: c" warning!
* No more "reference not found: @see c.api.C" error!

We can rely on compile-time checks of forward references.
This safety is the reason why Module Source Hierarchy is needed.
I'm not aware of any way to resolve those compiler warnings and errors with classical Gradle modules.
If some way is nevertheless possible, I suspect it may be more convolved than relying on compiler build-in support.
