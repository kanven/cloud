package com.kanven.cloud.common.motan.brave;

import brave.Tracing;

public class AutoValue_MotanTracing extends MotanTracing {

	private final Tracing tracing;

	private final String serverName;

	private final String host;

	private final int port;

	private AutoValue_MotanTracing(Tracing tracing, String serverName, String host, int port) {
		this.tracing = tracing;
		this.serverName = serverName;
		this.host = host;
		this.port = port;
	}

	@Override
	Tracing tracing() {
		return tracing;
	}

	@Override
	public String serverName() {
		return this.serverName;
	}

	@Override
	public String host() {
		return host;
	}

	@Override
	public int port() {
		return port;
	}

	@Override
	public Builder toBuilder() {
		return new Builder(this);
	}

	static final class Builder extends MotanTracing.Builder {

		private Tracing tracing;

		private String serverName;

		private String host;

		private int port;

		Builder() {
		}

		Builder(MotanTracing source) {
			this.tracing = source.tracing();
			this.serverName = source.serverName();
			this.host = source.host();
			this.port = source.port();
		}

		@Override
		MotanTracing.Builder tracing(Tracing tracing) {
			if (tracing == null) {
				throw new NullPointerException("Null tracing");
			}
			this.tracing = tracing;
			return this;
		}

		@Override
		public MotanTracing build() {
			return new AutoValue_MotanTracing(tracing, serverName, host, port);
		}

		@Override
		Builder serverName(String serverName) {
			this.serverName = serverName;
			return this;
		}

		@Override
		Builder host(String host) {
			this.host = host;
			return this;
		}

		@Override
		Builder port(int port) {
			this.port = port;
			return this;
		}

	}

}
