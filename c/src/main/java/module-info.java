/**
 * Module C depends on B.
 */
module c {
    requires b;
    exports c.api;
}
