package com.github.ompc.carrying.client.persistence;

/**
 * 将从server获取的数据顺序写入文件
 * @author admin
 *
 */
public interface CarryingDataPersistenceDao {
	
	void persistenceData() throws Exception;
	
}
