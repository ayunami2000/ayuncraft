package net.lax1dude.eaglercraft;

import org.json.JSONObject;

public interface ServerQuery {
	
	public static final long defaultTimeout = 10000l;
	
	public static class QueryResponse {
		public final String responseType;
		private final Object responseData;
		public final String serverVersion;
		public final String serverBrand;
		public final String serverName;
		public final long serverTime;
		public final long clientTime;
		public final boolean serverCracked;
		public QueryResponse(JSONObject obj) {
			this.responseType = obj.getString("type").toLowerCase();
			this.responseData = obj.get("data");
			this.serverVersion = obj.getString("vers");
			this.serverBrand = obj.getString("brand");
			this.serverName = obj.getString("name");
			this.serverTime = obj.getLong("time");
			this.clientTime = System.currentTimeMillis();
			this.serverCracked = obj.has("cracked") ? obj.getBoolean("cracked") : false;
		}
		public boolean isResponseString() {
			return responseData instanceof String;
		}
		public boolean isResponseJSON() {
			return responseData instanceof JSONObject;
		}
		public String getResponseString() {
			return (String)responseData;
		}
		public JSONObject getResponseJSON() {
			return (JSONObject)responseData;
		}
	}

	public boolean isQueryOpen();
	public void close();
	
	public void send(String str);
	
	public default void send(JSONObject obj) {
		send(obj.toString());
	}
	
	public int responseAvailable();
	public int responseBinaryAvailable();
	public QueryResponse getResponse();
	public byte[] getBinaryResponse();
	
	// normally I wouldn't resort to race conditions but TeaVM has no
	// java.util.concurrent classes for semaphore-like behavior

	public default boolean awaitResponseAvailable(long timeout) {
		long start = System.currentTimeMillis();
		while(isQueryOpen() && responseAvailable() <= 0 && (timeout <= 0l || System.currentTimeMillis() - start < timeout)) {
			try {
				Thread.sleep(0l, 250000);
			} catch (InterruptedException e) {
			}
		}
		return responseAvailable() > 0;
	}
	
	public default boolean awaitResponseAvailable() {
		return awaitResponseAvailable(defaultTimeout);
	}
	
	public default boolean awaitResponseBinaryAvailable(long timeout) {
		long start = System.currentTimeMillis();
		while(isQueryOpen() && responseBinaryAvailable() <= 0 && (timeout <= 0l || System.currentTimeMillis() - start < timeout)) {
			try {
				Thread.sleep(0l, 250000);
			} catch (InterruptedException e) {
			}
		}
		return responseBinaryAvailable() > 0;
	}

	public default boolean awaitResponseBinaryAvailable() {
		return awaitResponseBinaryAvailable(defaultTimeout);
	}

	public default QueryResponse awaitResponse(long timeout) {
		return awaitResponseAvailable(timeout) ? getResponse() : null;
	}
	
	public default QueryResponse awaitResponse() {
		return awaitResponseAvailable() ? getResponse() : null;
	}
	
	public default byte[] awaitResponseBinary(long timeout) {
		return awaitResponseBinaryAvailable(timeout) ? getBinaryResponse() : null;
	}

	public default byte[] awaitResponseBinary() {
		return awaitResponseBinaryAvailable() ? getBinaryResponse() : null;
	}

}
