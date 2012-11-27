package com.lavans.lacoder.util;

import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ガーベージコレクションタイマー
 * 1分おきにSystem.gc()を呼び出してlogにGCの結果を書き出す。
 * -verbose:gcで起動するとcatalina.outにGCのログが出るが時刻がわからないので
 * 1分置きにSystem.outに時刻を出力する。
 * luz.xmlに下記を書いておけばlog、System.out共に表示しなくなる。
 * <param name="gctimer.show" value="false"/>
 *
 * GCの間隔(単位：秒) デフォルト60秒
 * <param name="gctimer.interval" value="100"/>
 *
 * @author dobashi
 *
 */
public class GCTimer {
	/** logger */
	private static Log logger = LogFactory.getLog(GCTimer.class);

	/** interval of GC */
	private long interval = 10*60*1000; // 10分間隔
	/** メインタイマー */
	private Timer timer = new Timer();
	private boolean isShowTime = true;

	/**
	 * タイマー開始
	 */
	public void start(){
		try{
			// GCログを出すか
			Config config = Config.getInstance();
			isShowTime = !config.getParameter("gctimer.show").equalsIgnoreCase("false");
			String intervalStr = config.getParameter("gctimer.interval");
			try {
				interval = Long.parseLong(intervalStr)*1000;
			} catch (NumberFormatException e) {
			}
		}catch(FileNotFoundException e){
		}


		GcTimerTask task = new GCTimer.GcTimerTask();
		long delay = interval - System.currentTimeMillis()%interval; // １分未満切り捨て
		timer.scheduleAtFixedRate(task, delay, interval);
	}
	public void cancel(){
		timer.cancel();
	}

	/**
	 * GCタスク
	 * @author Yuki
	 *
	 */
	private class GcTimerTask extends TimerTask{
		@Override
		public void run() {
			try {
				long total = Runtime.getRuntime().totalMemory();
				long before = Runtime.getRuntime().freeMemory();
				long starttime = System.currentTimeMillis();
				System.gc();
				long time = System.currentTimeMillis() - starttime;
				long after = Runtime.getRuntime().freeMemory();
				//long max = Runtime.getRuntime().maxMemory();

				if(isShowTime){
					NumberFormat nf = NumberFormat.getInstance();
					logger.info("[GC "+ nf.format((total-before)/1024) + "K->"+ nf.format((total-after)/1024) +"K("+ nf.format(total/1024) +"K), "+ (double)time/1000 +" secs]");
					// verbose:gcを出力してるcatalina.out向けに時刻を打刻
					SimpleDateFormat sdfCurrent = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					System.out.println(sdfCurrent.format(new Date()));
				}
			} catch (Exception e) {
				logger.error("gc中にエラー", e);
			}
		}
	}
}
