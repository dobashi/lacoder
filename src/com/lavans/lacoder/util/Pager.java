/* $Id: Pager.java 509 2012-09-20 14:43:25Z dobashi $
 * create: 2004/10/18
 * (c)2004 Lavans Networks Inc. All Rights Reserved.
 */
package com.lavans.lacoder.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lavans.lacoder.commons.ArrayUtils;

//import com.lavans.util.Logger;

/**
 *
 * @author dobashi
 * @version 1.00
 */
public class Pager<E> implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 4429141113724792971L;

	/** Logger */
//	private static Logger logger = Logger.getInstance();

	/** ページ情報 */
	private PageInfo pageInfo = null;

	/** 全件数。 */
	private long totalCount;
	/** 全ページ数。 */
	private int totalPage;

	/** 次ページ制御用リンク先URL */
	private String linkUrl="";
	/** 次ページ制御用リンクパラメータ */
	private String linkParam="";

	/** 保持しているデータのリスト */
	private List<E> dataList;

	/** 属性格納用Map */
	private Map<String, Object> attributeMap = new HashMap<String, Object>();

	// 複数選択
	/** 前画面での全ID */
	public static final String ID_LIST = "id_list";
	/** 保存済みID */
	public static final String CHECKED_ID_LIST = "checked_id_list";
	/** 今回選択されたID */
	public static final String CHECKED_ID = "checked_id";
	/** 複数選択時の選択済みID一覧 */
	private Set<String> checkedIdList;

	/**
	 * コンストラクタ。表示行数指定。
	 */
	public Pager(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
		dataList = new ArrayList<E>(pageInfo.getRows());
		checkedIdList = new HashSet<String>();
	}

	//----------------------------------------------------------
	// ページ制御情報設定
	//----------------------------------------------------------
	/**
	 * 件数設定。
	 * この件数を元にページ数や次ページ存在チェック等を行う。
	 * DAOクラスで読込の際に使用する。
	 *
	 */
	public void setTotalCount(long x){
		totalCount = x;
		// 全ページ数
		totalPage = (int)(totalCount / pageInfo.getRows() -1);
		if(totalCount % pageInfo.getRows() != 0){
			totalPage++;
		}
	}

	/** 前のページがあるか。 */
	public boolean hasPrev(){
		return pageInfo.getPage()>0;
	}
	/** 次のページがあるか。 */
	public boolean hasNext(){
		return pageInfo.getPage() < totalPage;
	}
//	protected void setHasNext(boolean x){
//		hasNext = x;
//	}
	/** 全ページ数。 */
	public int getTotalPage(){
		return totalPage;
	}

	/**
	 * 全件データから現在のページを表すpagerにする。
	 * @param totalData
	 */
	public void setTotalData(List<E> totalData){
		// 全件数
		setTotalCount(totalData.size());

		int start = pageInfo.getPage() * pageInfo.getRows();
		for(int i=start; i<start+pageInfo.getRows(); i++){
			if(i>=totalData.size()){
				break;
			}
			add(totalData.get(i));
		}

	}


	//----------------------------------------------------------
	// 複数選択処理
	//----------------------------------------------------------
	/**
	 * 複数選択時の選択項目判別処理
	 * 下記のようにパラメータを渡すと、選択/非選択の判定を行う。
	 * pager.setCheckedParams(request.getParamegerMap());
	 */
	public void setCheckedParams(Map<String, String[]> params){
		// 今回選択されたID
		String checkedId[] = null;
		if(params.containsKey(CHECKED_ID)){
			checkedId = params.get(CHECKED_ID);
			// 今回選択されたIDを保存
			checkedIdList.addAll(Arrays.asList(checkedId));
		}
		// 保存済みID
		if(params.containsKey(CHECKED_ID_LIST) && !params.get(CHECKED_ID_LIST)[0].equals("")){
			checkedIdList.addAll(Arrays.asList(params.get(CHECKED_ID_LIST)[0].split(",")));
		}

		// 前画面の分がなければここで終了
		if(!params.containsKey(ID_LIST)) return;
		// 前画面で表示していたすべてのid
		String idList[] = params.get(ID_LIST)[0].split(",");
		// 保存済みIDから、前回表示で選択されなかったIDを引く
		for(int i=0; i<idList.length; i++){
			if(!ArrayUtils.contains(checkedId, idList[i])){
				checkedIdList.remove(idList[i]);
			}
		}
	}


	/**
	 * タイトル : ポジションデータ取得処理
	 * 説明 :
	 * @return List
	 * @param なし
	 * @throws なし
	 */
	public E get(int i) {
		return dataList.get(i);
	}

	/**
	 * タイトル : ポジションデータ取得処理
	 * 説明 :
	 * @return List
	 * @param なし
	 * @throws なし
	 */
	public void add(E obj) {
		dataList.add(obj);
	}

	/**
	 * タイトル : ポジションリスト取得処理
	 * 説明 :
	 * @return List
	 * @param なし
	 * @throws なし
	 */
	public List<E> getList() {
		return dataList;
	}

	/**
	 * タイトル : ポジションリストサイズ取得処理
	 * 説明 :
	 * @return int
	 * @param なし
	 * @throws なし
	 */
	public int size() {
		return dataList.size();
	}

	/**
	 * 表示開始番号。
	 * @return
	 */
	public int getStartNumber(){
		return pageInfo.getPage()*pageInfo.getRows()+1;
	}

	/**
	 * 表示終了番号。
	 * @return
	 */
	public int getEndNumber(){
		return getStartNumber()+dataList.size()-1;
	}

	/**
	 * @return totalCount を戻します。
	 */
	public long getTotalCount() {
		return totalCount;
	}
	/**
	 * @return linkParam を戻します。
	 */
	public String getLinkParam() {
		// pageInfoのrowsも自動的に付加する。
		if(pageInfo!=null){
			return linkParam + PageInfo.ROWS +"="+pageInfo.getRows()+"&";
		}
		return linkParam;
	}
	/**
	 * @param linkParam linkParam を設定。
	 */
	public void setLinkParam(String linkParam) {
		this.linkParam = linkParam;
//		this.linkParam = StringUtil.encodeParameterValues(linkParam);
	}
	/**
	 * @return linkUrl を戻します。
	 */
	public String getLinkUrl() {
		return linkUrl;
	}
	/**
	 * @param linkUrl linkUrl を設定。
	 */
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	/**
	 * @return pageInfo を戻します。
	 */
	public PageInfo getPageInfo() {
		return pageInfo;
	}

	/**
	 * 属性追加。
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, Object value){
		attributeMap.put(key, value);
	}
	/**
	 * 属性取得。
	 * @param key
	 * @param value
	 */
	public Object getAttribute(String key){
		return attributeMap.get(key);
	}

	/**
	 * @return the checkedIdList
	 */
	public Set<String> getCheckedIdList() {
		return checkedIdList;
	}

	/**
	 * @param checkedIdList the checkedIdList to set
	 */
	public void setCheckedIdList(Set<String> checkedIdList) {
		this.checkedIdList = checkedIdList;
	}
}
