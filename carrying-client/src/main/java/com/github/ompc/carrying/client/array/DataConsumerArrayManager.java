package com.github.ompc.carrying.client.array;

import java.util.ArrayList;
import java.util.List;

import com.github.ompc.carrying.client.ClientOption;
import com.github.ompc.carrying.common.domain.Row;
/**
 * 采用队列方式维护所有数据源
 * @author admin
 *
 */
public class DataConsumerArrayManager {
	
	private List<DataConsumerArray> dataConsumerArrays = new ArrayList<DataConsumerArray>(16284);
	
	/**
	 * 维护数组状态 0 未创建 1 创建中 2 已创建
	 */
	private byte[] consumerArrayStatus = new byte[1024*1024];
	private ClientOption option;
	
	public DataConsumerArrayManager(ClientOption option){
		this.option = option;
		for(int i=0;i<1024*1024;i++){
			consumerArrayStatus[i] = 0;
		}
	}
	
	public void put(Row row){
	
		//数组标识
		
		int num = (int)(row.getLineNum()/option.getDataArraySize());

		if(!containsDataConsumerArray(num) && consumerArrayStatus[num]==0){
			createNewDataConsumerArray(num);
		}else if(consumerArrayStatus[num]==1){
			waitForCreate(num);
		}
		
		getDataConsumerArrayByNum(num).putData(row);;
		
	}
	
	private synchronized void createNewDataConsumerArray(int num){
		
		consumerArrayStatus[num] = 1;//数组创建中
		
		if(containsDataConsumerArray(num)){
			return;
		}
		
		DataConsumerArray dataConsumerArray = new DataConsumerArray(num,option.getDataArraySize());
		
		dataConsumerArrays.add(num, dataConsumerArray);
		
		consumerArrayStatus[num] = 2;//数组已创建
	}
	
	/**
	 * 数组创建中，阻塞其他线程
	 * @param num
	 */
	private void waitForCreate(int num){
		while(consumerArrayStatus[num]==1){	
		}
	}
	
	public boolean containsDataConsumerArray(int num){
		
		if(dataConsumerArrays.size()<=num){
			return false;
		}
		return true;
		
	}
	
	public DataConsumerArray getDataConsumerArrayByNum(int num){
		return dataConsumerArrays.get(num);
	}

	public List<DataConsumerArray> getDataConsumerArrays() {
		return dataConsumerArrays;
	}
	
}
