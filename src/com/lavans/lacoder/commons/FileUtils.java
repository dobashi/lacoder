package com.lavans.lacoder.commons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class FileUtils{
	/**
	 * 設定保存
	 * @param lastname
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static boolean write(String filename, Collection<String> data) throws IOException{
		String tmpFilename = filename+".tmp";
		String bakFilename = filename+".bak";
		boolean result=false;
		File file = new File(tmpFilename);

		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		for(String str: data){
			out.write(str+"\n");
		}
		// 書き込み完了
		out.close();

		// bakファイルが存在するなら消す
		File bakFile = new File(bakFilename);
		if(bakFile.exists()){
			bakFile.delete();
		}
		// 旧ファイル保存
		File orgFile = new File(filename);
		orgFile.renameTo(new File(bakFilename));
		// ファイル移動
		result = file.renameTo(new File(filename));

		return result;
	}
}
