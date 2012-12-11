package com.lavans.lacoder.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lavans.lacoder.di.annotation.Scope;
import com.lavans.lacoder.util.ParameterUtils;

/**
 * HttpClient
 * 
 * Make easy to get or post request. This class uses URLConnection, so it's simple.
 * 
 * Usege:
 * String html = new SinpleHttpClient(url).request();	// just get
 * String html = new SinpleHttpClient(url).setQuery(query).request();	// get with query
 * 
 * Or you can write as bellow.
 * SimpleHttpClient client = new SinpleHttpClient(url);
 * client.setCharset(charset);
 * client.setPostData(map);
 * client.setRequestProperties(requestProperties);
 * String html = client.request();
 * 
 * TODO Post binary with "multipart/form-data"
 * http://blog.oklab.org/?p=136
 * 
 * @author dobashi
 *
 */
@Scope("prototype")
public class SimpleHttpClient {
	/** logger */
	private static final Log logger = LogFactory.getLog(SimpleHttpClient.class);

	private static final String DEFAULT_CHARSET="UTF-8";
	
	// Build parameter
	/** url. required. */
	private String urlStr=null;
	private String charset=DEFAULT_CHARSET;
	private String query=null;
	private String postData=null;
	//private Map<String, String> postParams=null;
	private Map<String, String> requestProperties=null;
	
	/**
	 * URLをセットする。ビルダー。
	 * 
	 * @param url
	 * @return
	 */
	public SimpleHttpClient setUrl(String url){
		this.urlStr = url;
		return this;
	}
	
	/**
	 * charsetをセットする。ビルダー。
	 * setPostData(Map)を使うとURLエンコード
	 * 
	 * @param url
	 * @return
	 */
	public SimpleHttpClient setCharset(String charset){
		this.charset = charset;
		return this;
	}

	/**
	 * queryをセットする。ビルダー。
	 * GETメソッドの時のQueryString。
	 * urlの後ろに"?aaa=ccc"という形式で付加するのと同じ。
	 * 
	 * @param url
	 * @return
	 */
	public SimpleHttpClient setQuery(String query){
		this.query = query;
		return this;
	}
	
	/**
	 * postDataをセットする。ビルダー。
	 * POSTメソッドで渡すデータ。
	 * 
	 * @param url
	 * @return
	 */
	public SimpleHttpClient setPostData(String setPostData){
		this.postData = setPostData;
		return this;
	}
	
	/**
	 * postDataをセットする。ビルダー。
	 * POSTメソッドで渡すデータ。Map形式。
	 * 既にセット済みのデータがある場合は上書きされる。
	 * デフォルトではUTF-8URLエンコードするので、charset変更の必要がある場合はこのメソッドを呼ぶ前に
	 * setCharset(String)する。
	 * 
	 * @param url
	 * @return
	 */
	public SimpleHttpClient setPostData(Map<String, String> postParams){
		this.postData = ParameterUtils.toStoreString(ParameterUtils.convertToStringArrayMap(postParams),charset);
		return this;
	}

	/**
	 * requetPropetriesをセットする。ビルダー。
	 * {@link URLConnection#setRequestProperty(String, String)}でセットするプロパティ。
	 * 
	 * @param url
	 * @return
	 */
	public SimpleHttpClient setRequestProperties(Map<String, String> requestProperties){
		this.requestProperties = requestProperties;
		return this;
	}
	
//	public String get(String urlStr, String charsetName) throws Exception {
//		return doRequest(urlStr, null, charsetName, false);
//	}
//	public String post(String urlStr, String paramStr) throws Exception {
//		return doRequest(urlStr, paramStr, DEFAULT_CHARSET, true);
//	}
//	public String post(String urlStr, String paramStr, String charsetName) throws Exception {
//		return doRequest(urlStr, paramStr, charsetName, true);
//	}
//	private String doRequest(String urlStr, String paramStr, String charsetName, boolean isPost) throws Exception {
//	}
	public String request() throws IOException, UnsupportedEncodingException {
		// debug
		logger.info(urlStr + query==null?"":"?"+query + postData==null?"":" post["+ postData +"]");

		if(query!=null){
			urlStr += "?"+query;
		}
		// 
		URL url = new URL(urlStr);
		URLConnection con = url.openConnection();
		if(requestProperties!=null){
			for(Entry<String, String> entry: requestProperties.entrySet()){
				con.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
			
		PrintStream os = null;
		BufferedReader is = null;
		try {
			// POSTの時
			if(postData!=null){
				con.setDoOutput(true);
				os = new PrintStream(con.getOutputStream());
				os.print(postData);
				os.flush();
			}
			is = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			StringBuilder builder = new StringBuilder();
			String s;
			while((s=is.readLine())!=null){
				logger.info(s);
				builder.append(s);
			}

			return builder.toString();
		} finally {
			if(os!=null){ try { os.close(); } catch (Exception e) { logger.warn(e); }}
			if(is!=null){try { is.close(); } catch (Exception e) { logger.warn(e); }}
		}
	}

}

/* sample
// 接続先
HttpConnector httpConnector = BeanManager.getBean(HttpConnector.class)
	.setUrl(serverUrl+path)
	.setCharset(CHARSET)
	.setPostData(params);
//			return httpConnector.request();

 */
