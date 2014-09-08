package com.github.ompc.carrying.client.array;

import com.github.ompc.carrying.common.domain.Row;

public class DataConsumerArray{
	private Row[] rows;
	/**
	 * 数组标识
	 */
	private int num;
	public DataConsumerArray(int num,int size){
		this.num = num;
		rows = new Row[size];
	}
	
	public void putData(Row row){
		int index = 0;
		if(num==0){
			index = (int)row.getLineNum();
		}else{
			index = (int)row.getLineNum()%num;
		}
		rows[index] = row;
	}
	
	public Row[] getRows(){
		return this.rows;
	}
}
