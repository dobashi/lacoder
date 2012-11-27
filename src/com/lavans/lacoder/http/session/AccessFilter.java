/* $Id: AccessFilter.java 509 2012-09-20 14:43:25Z dobashi $
 * created: 2006/01/20
 */
package com.lavans.lacoder.http.session;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.xpath.XPathExpressionException;

import com.lavans.lacoder.util.Config;


/**
 * 連続アクセスを制御するフィルター。
 * セッションIDまたは口座番号をキーとしてアクセス回数をカウントする。
 * 1秒以内に規定回数以上のアクセスがあったら、エラーとする。
 *
 * @author dobashi
 */
public class AccessFilter implements Filter {

    /** オーバーフローがあった場合のフラグ */
    public static final String ACCESS_FLG = "access_flg";

    /**  */
    public static final String ACCESSDATA_KEY = "accessdata_key";

	/** アクセスのインターバル(ミリ秒) */
	private static final long ACCESS_INTERVAL = 1000;

	/** アクセスの可能回数 */
	private static int maxAccessCount = 10;

	static{
		String accControl = null;
		try {
			/** 設定ファイル読込クラス */
			Config config = Config.getInstance();
			accControl = config.getNodeValue("/luz/http/access_control");
		} catch (XPathExpressionException | FileNotFoundException e) {
		}
		if(!accControl.equals("")){
			// 未指定ならデフォルトの10とする。
			maxAccessCount = Integer.parseInt(accControl);
		}
	}

	/**
	 * 1秒あたりのアクセス回数をチェックする。
	 * 個別のActionクラスでアクセス解除できるように、ここではセッションにフラグを
	 * 立てるだけで例外はThrowしない。ここで例外をThrowすると、画像の一覧表示などが
	 * できなくなる。
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
	    // セッション管理ロジック
	    HttpSession session = ((HttpServletRequest)req).getSession();

	    // 0以下ならアクセスチェック無し
	    if(maxAccessCount<1){
			chain.doFilter(req, res);
			return;
	    }

	    // セッションIDをキーにアクセス数を取得する。
		AccessData data = (AccessData)session.getAttribute(ACCESSDATA_KEY);
		if(data==null){
			data = new AccessData(maxAccessCount);
			session.setAttribute(ACCESSDATA_KEY, data);
		}
	    // 毎回時刻比較を行わなくて済むように、maxのカウント数を入れておいて
	    // カウントが0になったときだけ比較するようにする。

        // アクセス可能回数をチェック
        if(data.getCount()>0){
		    // アクセス可能回数を減らす
        	data.decreaseCount();
        }else{
        	// カウントが0になった
            // 前回のアクセス時刻、現在時刻を取得
    	    long now = System.currentTimeMillis();
		    // 現在時刻と10回前のアクセス時刻の差をチェック
		    if((now - data.getLastAccessTime()) < ACCESS_INTERVAL){
		    	// 1秒以内ならエラーフラグをセット
		    	session.setAttribute(ACCESS_FLG, req.getRemoteAddr());
//		    	throw new AccessOverFlowException("Too many access from["+ req.getRemoteAddr() +":"+ session.getId() +"]");
		    }else{
		    	// 1秒以上たっているなら初期化
		    	data.init(maxAccessCount);
		        session.removeAttribute(ACCESS_FLG);
		    }
        }

		chain.doFilter(req, res);
	}


	/* (非 Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
	}

	/* (非 Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

}

