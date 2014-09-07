package com.github.ompc.carrying.client;

import com.github.ompc.carrying.client.consumer.CarryingConsumer;
import com.github.ompc.carrying.client.consumer.CarryingResponseListener;
import com.github.ompc.carrying.common.CarryingConstants;
import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import com.github.ompc.carrying.common.networking.protocol.CarryingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.ompc.carrying.common.CarryingConstants.CORK_BUFFER_SIZE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * 搬运客户端启动器
 * Created by vlinux on 14-9-7.
 */
public class CarryingClientLauncher {

    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final int CLI_NUM = 2;
            //CPU_NUM * 2;
    private static final int CARRIER_NUM = CPU_NUM * 20;
                    // CLI_NUM * 10;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final CarryingConsumer[] consumers = new CarryingConsumer[CLI_NUM];
    private final AtomicInteger carrierIndex = new AtomicInteger();
    private CountDownLatch countDown = new CountDownLatch(CARRIER_NUM);

    /**
     * 搬运工
     */
    private class Carrier implements Runnable {

        private boolean isRunning = true;
        private CarryingResponse response;

        @Override
        public void run() {

            final int index = carrierIndex.getAndIncrement();
            final CarryingConsumer consumer = consumers[index%CLI_NUM];
            final ReentrantLock lock = new ReentrantLock();
            final Condition condition = lock.newCondition();

            Thread.currentThread().setName("CarryingConsumer-Carrier-"+index);
            logger.info("{} was started.", Thread.currentThread().getName());

            int cursor = 0;
            boolean isReTry = false;
            while( isRunning ) {

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
//                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }//try

                    if( null == response ) {
                        logger.info("request={} timeout, need retry!", request.getSequence());
                        continue;
                    }

                    if( response.isEOF() ) {
                        Carrier.this.isRunning = false;
                    } else {
                        // TODO write to file.
//                        logger.info("response={}",new String(response.getData()));
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
            logger.info("{} was finished.", Thread.currentThread().getName());

        }

    };

    private CarryingClientLauncher(CarryingConsumer.Option option) throws IOException, InterruptedException {

        // 初始化Consumer池
        initConsumers(option);

        // 初始化搬运工
        initCarriers();

        countDown.await();

    }

    /**
     * 初始化Consumer池
     * @param option
     * @throws IOException
     */
    private void initConsumers(CarryingConsumer.Option option) throws IOException {
        for( int index=0; index<CLI_NUM; index++ ) {
            final CarryingConsumer consumer = new CarryingConsumer(option, pool);
            consumers[index] = consumer;
            consumer.connect();
        }
        logger.info("init consumers finished. count={}",CLI_NUM);
    }

    /**
     * 初始化搬运工
     */
    private void initCarriers() {

        for( int i=0;i<CARRIER_NUM;i++ ) {
            new Thread(new Carrier()).start();
        }

    }


    public static void main(String... args) throws IOException, InterruptedException {

//        args = new String[]{"127.0.0.1", "8787"};
        final long startTime = System.currentTimeMillis();
        try {
            final InetSocketAddress address = new InetSocketAddress(args[0], Integer.valueOf(args[1]));
            final CarryingConsumer.Option option = new CarryingConsumer.Option();
            option.serverAddress = address;
            option.tcpNoDelay = false;
            option.sendBufferSize =
                    //CARRIER_NUM * 8 * 2;
                    CORK_BUFFER_SIZE;
            option.receiveBufferSize =
                    //1024*212*0xFF;
                    CORK_BUFFER_SIZE;
//            option.sendBufferSize = 1024*1024;
            new CarryingClientLauncher(option);
        } finally {

            final long finishTime = System.currentTimeMillis();
            System.out.println( finishTime - startTime );
            System.exit(0);

        }



    }


}
