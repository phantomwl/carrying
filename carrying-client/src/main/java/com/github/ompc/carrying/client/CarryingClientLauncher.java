package com.github.ompc.carrying.client;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ompc.carrying.client.array.DataConsumerArrayManager;
import com.github.ompc.carrying.client.consumer.CarryingConsumer;
import com.github.ompc.carrying.client.consumer.CarryingResponseListener;
import com.github.ompc.carrying.client.persistence.impl.MappedCarryingDataPersistenceDao;
import com.github.ompc.carrying.client.util.BytesReverseUtil;
import com.github.ompc.carrying.common.domain.Row;
import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import com.github.ompc.carrying.common.networking.protocol.CarryingResponse;

/**
 * 搬运客户端启动器
 * Created by vlinux on 14-9-7.
 */
public class CarryingClientLauncher {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final AtomicInteger carrierIndex = new AtomicInteger(0);

    private final int CLI_NUM;
    private final int CARRIER_NUM;
    private final CarryingConsumer[] consumers;
    private final CountDownLatch countDown;

    private DataConsumerArrayManager dataConsumerArrayManager;
    
    /**
     * 搬运工
     */
    private class Carrier implements Runnable {

        private boolean isRunning = true;
        private CarryingResponse response;
        private DataConsumerArrayManager dataConsumerArrayManager;
        
        Carrier(DataConsumerArrayManager dataConsumerArrayManager){
        	this.dataConsumerArrayManager = dataConsumerArrayManager;
        }
        
        @Override
        public void run() {

            final int index = carrierIndex.getAndIncrement();
            final CarryingConsumer consumer = consumers[index % CLI_NUM];
            final ReentrantLock lock = new ReentrantLock();
            final Condition condition = lock.newCondition();

            currentThread().setName("CarryingConsumer-Carrier-" + index);
            logger.info("{} was started.", currentThread().getName());

            int cursor = 0;
            boolean isReTry = false;
            while (isRunning) {

                final CarryingRequest request = new CarryingRequest(cursor, isReTry, index);
                try {
                    consumer.request(request, new CarryingResponseListener() {
                        @Override
                        public void onResponse(CarryingResponse response) {

                            lock.lock();
                            try {
                                Carrier.this.response = response;
                                condition.signal();
                            } finally {
                                lock.unlock();
                            }//try

                        }
                    });

                    lock.lock();
                    try {
                        condition.await(500, MILLISECONDS);
                    } catch (InterruptedException e) {
                        currentThread().interrupt();
                    } finally {
                        lock.unlock();
                    }//try

                    if (null == response) {
                        logger.info("request={} timeout, need retry!", request.getSequence());
                        continue;
                    }

                    if (response.isEOF()) {
                        Carrier.this.isRunning = false;
                    } else {
                    	
                    	// conver to row
                    	Row row = new Row();
                    	row.setLineNum(response.getLineNumber());
                    	row.setData(response.getData());
                    	BytesReverseUtil.reverse(row.getData());
                    	dataConsumerArrayManager.put(row);
                    }

                    isReTry = false;
                    response = null;
                    cursor++;
                } catch (IOException e) {
                    logger.warn("consumer={} send request={} failed, need retry!",
                            new Object[]{index, request.getSequence()}, e);
                    isReTry = true;
                    continue;
                }//try

            }//while

            countDown.countDown();
            logger.info("{} was finished.", currentThread().getName());

        }
        
    }


    private CarryingClientLauncher(InetSocketAddress address, ClientOption option) throws IOException, InterruptedException {

        CLI_NUM = option.getConsumerNumbers();
        CARRIER_NUM = option.getCarrierNumbers();

        consumers = new CarryingConsumer[CLI_NUM];
        countDown = new CountDownLatch(CARRIER_NUM);
        
        dataConsumerArrayManager = new DataConsumerArrayManager(option);
        
        // 初始化Consumer池
        initConsumers(address, option);

        // 初始化搬运工
        initCarriers(dataConsumerArrayManager);

        // 砖头写入文件
        new MappedCarryingDataPersistenceDao(option, dataConsumerArrayManager).persistenceData();
        
        countDown.await();

    }

    /**
     * 初始化Consumer池
     *
     * @param serverAddress
     * @param option
     * @throws IOException
     */
    private void initConsumers(InetSocketAddress serverAddress, ClientOption option) throws IOException {
        for (int index = 0; index < CLI_NUM; index++) {
            final CarryingConsumer consumer = new CarryingConsumer(serverAddress, option, pool);
            consumers[index] = consumer;
            consumer.connect();
        }

        Runtime.getRuntime().addShutdownHook(new Thread("Consumer-Shutdown-Hook"){

            @Override
            public void run() {
                for( CarryingConsumer consumer : consumers ) {
                    if( null != consumer
                            && consumer.isConnected()) {
                        consumer.disconnect();
                    }
                }
            }
        });

        logger.info("init consumers finished. count={}", CLI_NUM);
    }

    /**
     * 初始化搬运工
     */
    private void initCarriers(DataConsumerArrayManager dataConsumerArrayManager) {
    	
        for (int i = 0; i < CARRIER_NUM; i++) {
            new Thread(new Carrier(dataConsumerArrayManager)).start();
        }

    }


    public static void main(String... args) throws IOException, InterruptedException {

        final long startTime = System.currentTimeMillis();
        final ClientOption clientOption = new ClientOption(args[2]);

        try {
            final InetSocketAddress serverAddress = new InetSocketAddress(args[0], Integer.valueOf(args[1]));
            new CarryingClientLauncher(serverAddress, clientOption);
        } finally {

            final long finishTime = System.currentTimeMillis();
            System.out.println(finishTime - startTime);
            System.exit(0);

        }


    }


}
