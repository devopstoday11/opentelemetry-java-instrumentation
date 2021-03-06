/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package muzzle;

import io.opentelemetry.instrumentation.util.gc.GCUtils;
import io.opentelemetry.javaagent.tooling.muzzle.Reference;
import io.opentelemetry.javaagent.tooling.muzzle.collector.ReferenceCollector;
import io.opentelemetry.javaagent.tooling.muzzle.matcher.ReferenceMatcher;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;

public class MuzzleWeakReferenceTest {
  /*
   * Spock holds strong references to all local variables. For weak reference testing we must create our strong references away from Spock in this java class.
   *
   * Even returning a WeakReference<ClassLoader> is enough for spock to create a strong ref.
   */
  public static boolean classLoaderRefIsGarbageCollected() throws InterruptedException {
    ClassLoader loader = new URLClassLoader(new URL[0], null);
    WeakReference<ClassLoader> clRef = new WeakReference<>(loader);
    Reference[] refs =
        ReferenceCollector.collectReferencesFrom(TestClasses.MethodBodyAdvice.class.getName())
            .values()
            .toArray(new Reference[0]);
    ReferenceMatcher refMatcher = new ReferenceMatcher(refs);
    refMatcher.getMismatchedReferenceSources(loader);
    loader = null;
    GCUtils.awaitGC(clRef);
    return clRef.get() == null;
  }
}
