/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.elasticsearch.transport;

import io.opentelemetry.instrumentation.api.tracer.DatabaseClientTracer;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.attributes.SemanticAttributes;
import java.net.InetSocketAddress;
import org.elasticsearch.action.Action;

public class ElasticsearchTransportClientTracer
    extends DatabaseClientTracer<Void, Action<?, ?, ?>> {
  public static final ElasticsearchTransportClientTracer TRACER =
      new ElasticsearchTransportClientTracer();

  public Span onRequest(Span span, Class action, Class request) {
    span.setAttribute("elasticsearch.action", action.getSimpleName());
    span.setAttribute("elasticsearch.request", request.getSimpleName());
    return span;
  }

  @Override
  protected String normalizeQuery(Action<?, ?, ?> query) {
    return query.getClass().getSimpleName();
  }

  @Override
  protected String dbSystem(Void connection) {
    return "elasticsearch";
  }

  @Override
  protected InetSocketAddress peerAddress(Void connection) {
    return null;
  }

  @Override
  protected void onStatement(Span span, String statement) {
    span.setAttribute(SemanticAttributes.DB_OPERATION, statement);
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.auto.elasticsearch";
  }
}
