package com.github.ompc.carrying.client.util;
/**
 * 字节数组逆序
 * @author admin
 *
 */
public class BytesReverseUtil {
	/**
	 * 对字节数组进行逆序
	 */
	public static byte[] reverse(byte[] bricks){
		if(null==bricks||bricks.length==0){
			return null;
		}
		byte temp;
		int len = bricks.length;
		for(int i=0;i<bricks.length/2;i++){
			temp = bricks[i];
			bricks[i] = bricks[len-i-1];
			bricks[len-i-1] = temp;
		}
		return bricks;
	}
}
