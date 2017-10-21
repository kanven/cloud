package com.kanven.cloud.common.motan.brave;

import brave.Tracing;

public abstract class MotanTracing {

	public static MotanTracing create(Tracing tracing) {
		return newBuilder(tracing).build();
	}

	public static Builder newBuilder(Tracing tracing) {
		return new AutoValue_MotanTracing.Builder().tracing(tracing);
	}

	public MotanClientInterceptor createClientInterceptor() {
		return new MotanClientInterceptor(this);
	}

	public MotanServerInterceptor createServerInterceptor() {
		return new MotanServerInterceptor(this);
	}

	abstract Tracing tracing();

	public abstract String serverName();

	public abstract String host();

	public abstract int port();

	public abstract Builder toBuilder();

	public static abstract class Builder {

		abstract Builder tracing(Tracing tracing);

		public abstract MotanTracing build();

		abstract Builder serverName(String serverName);

		abstract Builder host(String host);

		abstract Builder port(int port);

		Builder() {
		}
	}

	MotanTracing() {

	}

}
