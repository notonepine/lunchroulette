package com.notonepine.lunchroulette;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Saquib Hafiz
 *
 */
public class NetworkService extends Service {
    private final IBinder mBinder = new NetworkBinder();
    private int MAX_NUM_OF_CALLS = 6;
    private Queue<NetworkRequest> backlog;
    private List<NetworkRequest> currentlyRequesting;
	private int readTimeoutTime = 20000;
	private int connectTimeoutTime = 30000;
	private int currentCallCount = 0;
	private boolean DEBUG = true;
	
	private enum RequestType { GET, POST, PUT, DELETE };
	
	public class NetworkBinder extends Binder {
		NetworkService getService() {
            return NetworkService.this;
        }
		
    }
	
	@Override
    public void onCreate() {
		backlog = new ConcurrentLinkedQueue<NetworkService.NetworkRequest>();
		currentlyRequesting = new ArrayList<NetworkService.NetworkRequest>(MAX_NUM_OF_CALLS);
		log("Created Network Service");
    }
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("Started Network Service.");
        return START_STICKY;
    }
	
	@Override
	public void onDestroy() {
		clearAll();
		log("Destroyed Network Service");
	}

	@Override
	public IBinder onBind(Intent intent) {
		log("Bound to Network Service");
		return mBinder;
	}
	
	/**
	 * HTTP GET call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param parameters A key-value map for parameters.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void get(String url, 
			Map<String, String> headers,
			Map<String, String> parameters,
			NetworkResponseListener listener) {
		makeCall(new NetworkRequest(url, headers, parameters, null, listener, RequestType.GET));
	}

	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param parameters A key-value map for parameters.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url, 
			Map<String, String> headers,
			Map<String, String> parameters,
			byte[] content,
			NetworkResponseListener listener) {
		makeCall(new NetworkRequest(url, headers, parameters, content, listener, RequestType.POST));
	}

	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param parameters A key-value map for parameters.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url, 
			Map<String, String> headers,
			Map<String, String> parameters,
			byte[] content,
			NetworkResponseListener listener) {
		makeCall(new NetworkRequest(url, headers, parameters, content, listener, RequestType.PUT));
	}
	
	/**
	 * HTTP DELETE call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param parameters A key-value map for parameters.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void delete(String url, 
			Map<String, String> headers,
			Map<String, String> parameters,
			NetworkResponseListener listener) {
		makeCall(new NetworkRequest(url, headers, parameters, null, listener, RequestType.DELETE));
	}
	
	private void makeCall(NetworkRequest request) {
		backlog.add(request);
		
		if (currentCallCount < MAX_NUM_OF_CALLS)
			executeRequest();
	}

	private void executeRequest() {
		NetworkRequest nextRequest;
		if ((nextRequest = backlog.poll()) != null) {
			incCurrentCallCount();
			currentlyRequesting.add(nextRequest);
			nextRequest.execute();
		}
		
		log("Executing a request from the backlog.");
	}
	
	/*
	 *		This is all the extended API for easier use
	 */
	
	/**
	 * HTTP GET call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void get(String url, 
			Map<String, String> headers,
			NetworkResponseListener listener) {
		get(url, headers, new HashMap<String, String>(), listener);
	}

	/**
	 * HTTP GET call.
	 * 
	 * @param url The URL string.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void get(String url,
		NetworkResponseListener listener) {
		get(url,  new HashMap<String, String>(),  new HashMap<String, String>(), listener);
	}

	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url, 
			Map<String, String> headers,
			String content,
			NetworkResponseListener listener) {
		post(url, headers, new HashMap<String, String>(), content.getBytes(), listener);
	}

	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url, 
			String content,
			NetworkResponseListener listener) {
		post(url, new HashMap<String, String>(), new HashMap<String, String>(), content.getBytes(), listener);
	}
	
	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url, 
			Map<String, String> headers,
			byte[] content,
			NetworkResponseListener listener) {
		post(url, headers, new HashMap<String, String>(), content, listener);
	}

	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url, 
			byte[] content,
			NetworkResponseListener listener) {
		post(url, new HashMap<String, String>(), new HashMap<String, String>(), content, listener);
	}

	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param parameters A key-value map for parameters.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url, 
			Map<String, String> headers,
			Map<String, String> parameters,
			NetworkResponseListener listener) {
		post(url, headers, parameters, new byte[0], listener);
	}

	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url, 
			Map<String, String> headers,
			NetworkResponseListener listener) {
		post(url, headers, new HashMap<String, String>(),  new byte[0], listener);
	}

	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url,
		NetworkResponseListener listener) {
		post(url, new HashMap<String, String>(), new HashMap<String, String>(),  new byte[0], listener);
	}
	
	/**
	 * HTTP POST call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param parameters A key-value map for parameters.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void post(String url, 
			Map<String, String> headers,
			Map<String, String> parameters,
			String content,
			NetworkResponseListener listener) {
		post(url, headers, parameters, content.getBytes(), listener);
	}

	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param parameters A key-value map for parameters.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url, 
			Map<String, String> headers,
			Map<String, String> parameters,
			String content,
			NetworkResponseListener listener) {
		put(url, headers, parameters, content.getBytes(), listener);
	}

	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url, 
			Map<String, String> headers,
			String content,
			NetworkResponseListener listener) {
		put(url, headers, new HashMap<String, String>(), content.getBytes(), listener);
	}
	
	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url, 
			Map<String, String> headers,
			byte[] content,
			NetworkResponseListener listener) {
		put(url, headers, new HashMap<String, String>(), content, listener);
	}

	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url, 
			String content,
			NetworkResponseListener listener) {
		put(url, new HashMap<String, String>(), new HashMap<String, String>(), content.getBytes(), listener);
	}
	
	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param content The content you want to post/put in the request.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url, 
			byte[] content,
			NetworkResponseListener listener) {
		put(url, new HashMap<String, String>(), new HashMap<String, String>(), content, listener);
	}

	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param parameters A key-value map for parameters.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url, 
			Map<String, String> headers,
			Map<String, String> parameters,
			NetworkResponseListener listener) {
		put(url, headers, parameters, new byte[0], listener);
	}

	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url, 
			Map<String, String> headers,
			NetworkResponseListener listener) {
		put(url, headers, new HashMap<String, String>(),  new byte[0], listener);
	}

	/**
	 * HTTP PUT call.
	 * 
	 * @param url The URL string.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void put(String url,
		NetworkResponseListener listener) {
		put(url, new HashMap<String, String>(), new HashMap<String, String>(),  new byte[0], listener);
	}

	/**
	 * HTTP DELETE call.
	 * 
	 * @param url The URL string.
	 * @param headers A key-value map for headers.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void delete(String url, 
			Map<String, String> headers,
			NetworkResponseListener listener) {
		delete(url, headers, new HashMap<String, String>(), listener);
	}

	/**
	 * HTTP DELETE call.
	 * 
	 * @param url The URL string.
	 * @param listener The NetworkResponseListener that gets called post execution of the request.(cannot be null).
	 */
	public void delete(String url,
		NetworkResponseListener listener) {
		delete(url, new HashMap<String, String>(), new HashMap<String, String>(), listener);
	}
	
	private class NetworkRequest extends AsyncTask<Void, Void, Void>{
		private String url; 
		private Map<String, String> headers;
		private Map<String, String> parameters;
		private byte[] content;
		private NetworkResponseListener listener;
		private RequestType requestType;
				
		private NetworkRequest(String url, 
				Map<String, String> headers,
				Map<String, String> parameters,
				byte[] content,
				NetworkResponseListener listener,
				RequestType requestType) {
			
			this.url = url;
			this.headers = headers;
			this.parameters = parameters;
			this.content = content;
			this.listener = listener;
			this.requestType = requestType;
			
		    url += getParameterString();
		    
		    log("Created new network request with the following attributes " + 
		    		" url : " + url +
		    		", headers : " + headers +
		    		", parameters : " + parameters +
		    		", content : " + content +
		    		", requestType : " + requestType);
		}

		private String getParameterString() {
			if(!url.endsWith("?"))
		        url += "?";

		    List<NameValuePair> params = new LinkedList<NameValuePair>();

			for (Entry<String, String> parameter : parameters.entrySet())
				params.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));

		    return URLEncodedUtils.format(params, "utf-8");
		}

		@Override
		protected Void doInBackground(Void... params) {
			log("Started executing request.");
			
			try {
				HttpResponse response = (new DefaultHttpClient()).execute(getRequest());
				
				int responseCode = response.getStatusLine().getStatusCode();
		        
		        if (responseCode != 200) {
		        	String reasonPhrase = response.getStatusLine().getReasonPhrase();
					log("Successfully executed request however received bad response : " + responseCode + " - " + reasonPhrase);
		        	listener.onError(new Exception(reasonPhrase));
		        } else {
		        	log("Successfully executed request.");
		        	listener.onSuccess(response.getEntity().getContent());
		        }
			} catch (Exception e) {
				log("Unsuccessfully executed request. Reason " + e.getMessage());
				listener.onError(e);
			}
			log("Finished executing request.");
			return null;
		}
		
		private HttpUriRequest getRequest() {
			
			switch(requestType) {
			case GET:
				HttpGet requestGet = new HttpGet(url);
				for (Entry<String, String> header : headers.entrySet())
					requestGet.setHeader(header.getKey(), header.getValue());
				return requestGet;
			case POST:
				HttpPost requestPost = new HttpPost(url);
				for (Entry<String, String> header : headers.entrySet())
					requestPost.setHeader(header.getKey(), header.getValue());
				requestPost.setEntity(new ByteArrayEntity(content));
				return requestPost;
			case PUT:
				HttpPut requestPut = new HttpPut(url);
				for (Entry<String, String> header : headers.entrySet())
					requestPut.setHeader(header.getKey(), header.getValue());
				requestPut.setEntity(new ByteArrayEntity(content));
				return requestPut;
			case DELETE:
				HttpDelete requestDelete = new HttpDelete(url);
				for (Entry<String, String> header : headers.entrySet())
					requestDelete.setHeader(header.getKey(), header.getValue());
				return requestDelete;
			default:
				HttpGet request = new HttpGet(url);
				for (Entry<String, String> header : headers.entrySet())
					request.setHeader(header.getKey(), header.getValue());
				return request;
			}
		}

		@Override
		public void onPostExecute(Void params) {
			log("On post request execution.");
			currentlyRequesting.remove(this);
			decCurrentCallCount();
			executeRequest();
		}
		
	}
	
	public interface NetworkResponseListener {
		/**
		 * Called when the request successfully returned with valid data.
		 * 
		 * @param data The InputStream containing the valid data.
		 */
		public void onSuccess(InputStream data);
		
		/**
		 * Called when there is an error in making the request or if the call returns with a bad status code (if it is not status code 200).
		 * 
		 * @param error The Exception that contains the error message.
		 */
		public void onError(Exception error);
	}
	
	/**
	 * @param stream
	 * @return The string value from the inputstream.
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static String inputStreamToString(InputStream stream)
			throws IOException, UnsupportedEncodingException {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
 
		return sb.toString();
	}

	private void decCurrentCallCount() {
		currentCallCount--;
		log("Decremented Current Call Count to " + currentCallCount);
	}

	private void incCurrentCallCount() {
		currentCallCount++;
		log("Incremented Current Call Count to " + currentCallCount);
	}

	/**
	 * @return Get the number of calls currently being executed.
	 */
	public int getCurrentCallCount() {
		return currentCallCount;
	}
	
	/**
	 * @return Get the max number of calls the service can make.
	 */
	public int getMaxNumOfCalls() {
		return MAX_NUM_OF_CALLS;
	}

	/**
	 * Set the max number of calls the service can make (default 6).
	 * 
	 * @param maxNumOfCalls The number of max calls.
	 */
	public void setMaxNumOfCalls(int maxNumOfCalls) {
		MAX_NUM_OF_CALLS = maxNumOfCalls;
	}

	/**
	 * @return The read timeout time.
	 */
	public int getReadTimeoutTime() {
		return readTimeoutTime;
	}

	/**
	 * Set the read timeout time (default 20000ms).
	 * 
	 * @param readTimeoutTime The read timeout time in milliseconds.
	 */
	public void setReadTimeoutTime(int readTimeoutTime) {
		this.readTimeoutTime = readTimeoutTime;
	}

	/**
	 * @return The connect timeout time.
	 */
	public int getConnectTimeoutTime() {
		return connectTimeoutTime;
	}

	/**
	 * Set the connect timeout time (default 30000ms).
	 * 
	 * @param connectTimeoutTime The connect timeout time in milliseconds.
	 */
	public void setConnectTimeoutTime(int connectTimeoutTime) {
		this.connectTimeoutTime = connectTimeoutTime;
	}

	private void log(String message) {
		if (DEBUG)
			Log.d("NetworkService", message);
	}
	
	/**
	 * Turn debug mode on or off for the service.
	 * 
	 * @param debug True if you want to see log statements, false otherwise.
	 */
	public void setDebug(boolean debug) {
		DEBUG = debug;
	}
	
	/**
	 * Clear all ongoing and pending requests.
	 */
	public void clearAll() {
		clearBackLog();
		
		clearCurrentRequests();
		
		log("Cleared all network requests.");
	}

	/**
	 * Clear all the currently executing requests.
	 */
	public void clearCurrentRequests() {
		if (currentlyRequesting != null) {
			for (NetworkRequest request : currentlyRequesting) {
				request.cancel(true);
				request.listener.onError(new Exception("Cancelled request mid execution."));
				log("Cancelled request mid execution.");
			}
			
			currentlyRequesting.clear();
		}
	}

	/**
	 * Clear all the pending requests that have not been executed yet.
	 */
	public void clearBackLog() {
		if (backlog != null) {
			synchronized (backlog) {
				for (NetworkRequest request : backlog) {
					request.cancel(true);
					request.listener.onError(new Exception("Cancelled request before execution."));
					log("Cancelled request before execution.");
				}
				backlog.clear();
			}
		}
	}
}
