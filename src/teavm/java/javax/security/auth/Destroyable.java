/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javax.security.auth;

public interface Destroyable {
    public default void destroy() throws DestroyFailedException {
        throw new DestroyFailedException();
    }

    public default boolean isDestroyed() {
        return false;
    }
}
