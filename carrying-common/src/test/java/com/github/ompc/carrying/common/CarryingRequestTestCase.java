package com.github.ompc.carrying.common;

import com.github.ompc.carrying.common.networking.protocol.CarryingRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * 请求报文测试用例
 * Created by oldmanpushcart@gmail.com on 14-9-7.
 */
public class CarryingRequestTestCase {

    @Test
    public void test_create_CarryingRequest() {

        final int index = 0xFE;
        final boolean isReTry = false;

        for( int cursor=0; cursor<0x0000; cursor++ ) {

            final CarryingRequest request = new CarryingRequest(cursor, isReTry, index);
            Assert.assertFalse(request.isReTry());
            Assert.assertEquals(request.getIndex(), index);

        }

    }

}
