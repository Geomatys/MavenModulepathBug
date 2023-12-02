package c.api;

import b.internal.Unsafe;

/**
 * Public API of module C.
 */
public class C {
    /** Creates a new instance. */
    public C() {}

    /**
     * Using private API of module B.
     */
    private void doSomeStuff() {
        new Unsafe();
    }
}
