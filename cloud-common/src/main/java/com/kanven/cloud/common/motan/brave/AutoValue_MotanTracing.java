package com.kanven.cloud.common.motan.brave;

import brave.Tracing;

public class AutoValue_MotanTracing extends MontanTracing {

	private final Tracing tracing;

	private AutoValue_MotanTracing(Tracing tracing) {
		this.tracing = tracing;
	}

	@Override
	Tracing tracing() {
		return tracing;
	}

	@Override
	public Builder toBuilder() {
		return new Builder(this);
	}

	static final class Builder extends MontanTracing.Builder {

		private Tracing tracing;

		Builder() {
		}

		Builder(MontanTracing source) {
			this.tracing = source.tracing();
		}

		@Override
		MontanTracing.Builder tracing(Tracing tracing) {
			if (tracing == null) {
				throw new NullPointerException("Null tracing");
			}
			this.tracing = tracing;
			return this;
		}

		@Override
		public MontanTracing build() {
			return new AutoValue_MotanTracing(tracing);
		}

	}

}
