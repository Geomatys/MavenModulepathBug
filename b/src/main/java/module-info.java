/**
 * Module B depends on A.
 */
module b {
    requires a;
    exports b.api;                  // Package accessible by everyone.
    exports b.internal to a, c;     // Package accessible only by selected modules.
}
