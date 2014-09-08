package com.github.ompc.carrying.common;

import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import junit.framework.Assert;
import org.junit.Test;

import static com.github.ompc.carrying.common.CarryingConstants.SEQ_INDEX_BITS;
import static com.github.ompc.carrying.common.CarryingConstants.SEQ_RETRY_BITS;

/**
 * 请求报文测试用例
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public class CarryingRequestTestCase {

    @Test
    public void test_create_CarryingRequest() {

        final int index = 0x1FF;
        final boolean isReTry = false;

        for( int cursor=0; cursor<0xFFFF; cursor++ ) {

            final CarryingRequest request = new CarryingRequest(cursor, isReTry, index);
            Assert.assertFalse(request.isReTry());
            Assert.assertEquals(index, request.getIndex());
            Assert.assertEquals(cursor, (request.getCursor()));

        }

    }

}
