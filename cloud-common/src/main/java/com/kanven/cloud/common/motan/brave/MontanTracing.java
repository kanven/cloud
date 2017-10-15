package com.kanven.cloud.common.motan.brave;

import brave.Tracing;

public abstract class MontanTracing {

	public static MontanTracing create(Tracing tracing) {
		return newBuilder(tracing).build();
	}

	public static Builder newBuilder(Tracing tracing) {
		return new AutoValue_MotanTracing.Builder().tracing(tracing);
	}

	abstract Tracing tracing();

	public abstract Builder toBuilder();

	public static abstract class Builder {

		abstract Builder tracing(Tracing tracing);

		public abstract MontanTracing build();

		Builder() {
		}
	}

	MontanTracing() {

	}

}
