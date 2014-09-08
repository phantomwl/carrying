package com.github.ompc.carrying.client.persistence.impl;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ompc.carrying.client.ClientOption;
import com.github.ompc.carrying.client.array.DataConsumerArray;
import com.github.ompc.carrying.client.array.DataConsumerArrayManager;
import com.github.ompc.carrying.client.persistence.CarryingDataPersistenceDao;
import com.github.ompc.carrying.common.domain.Row;

public class MappedCarryingDataPersistenceDao implements CarryingDataPersistenceDao{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private FileChannel fileChannel;
	private File file;
	private MappedByteBuffer mappedBuffer;

	private DataConsumerArrayManager dataConsumerArrayManager;
	private CountDownLatch latch = new CountDownLatch(1);
	private ClientOption option;
	
	public MappedCarryingDataPersistenceDao(ClientOption option, DataConsumerArrayManager dataConsumerArrayManager){
		this.option = option;
		this.dataConsumerArrayManager = dataConsumerArrayManager;
	}
	
	private Runnable dataPersistenceRunnable = new Runnable(){

		@Override
		public void run() {
			
			List<DataConsumerArray> arrayingDataArrays = dataConsumerArrayManager.getDataConsumerArrays();
		
			for(int i=0;i<arrayingDataArrays.size();i++){
				Row[] rows = arrayingDataArrays.get(i).getRows();
				for(int j=0;j<rows.length;j++){
					Row row = rows[j];
					if(row==null){
						break;
					}
					mappedBuffer.put(Long.valueOf(row.getLineNum()).byteValue());
					//换行符字节表示方式
					mappedBuffer.put(row.getData()).put("\r\n".getBytes());
					if (isEOF()) {
						try {
							mappedBuffer = fileChannel.map(
									FileChannel.MapMode.READ_WRITE,
									mappedBuffer.position(),
									option.getMapBufferSize());
						} catch (IOException e) {
							logger.error(
									"error@MappedCarryingDataPersistenceDao dataPersistenceRunnable IOException",
									e);
						}
                    }//if
				}//for
			}//for
			
			closeFileChannel();
			
			latch.countDown();
			
		}
	};
	
	@SuppressWarnings("resource")
	@Override
	public void persistenceData(){
		
		file = new File(option.getDataFilePath());
		
		if(null==file
				  ||!file.exists()
				  ||!file.canWrite()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("error@MappedCarryingDataPersistenceDao persistenceData IOException",e);
			}
		}//if
		
		try {
			fileChannel = new RandomAccessFile(file,"rw").getChannel();
			mappedBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,0,option.getMapBufferSize());
			
			final Thread thread = new Thread(dataPersistenceRunnable,"data-persitence-thread");
			thread.start();
			
			latch.await();
			
			closeFileChannel();
			
		} catch (Exception e) {
			logger.error("error@MappedCarryingDataPersistenceDao persistenceData exception",e);
		}//try
	}
	
    /**
     * 判断是否到达缓冲区末尾
     * @return
     */
    private boolean isEOF(){
            return mappedBuffer.remaining()<256;
    }
	
	/**
	 * 关闭文件通道
	 */
	private void closeFileChannel(){
		if(null != fileChannel){
			try{
				fileChannel.close();
			}catch(Throwable t){
				logger.info("info@MappedCarryingDataPersistenceDao closeFileChannel,file={}",file);
			}
		}
	}
	
	public void setFile(File file) {
		this.file = file;
	}

}
