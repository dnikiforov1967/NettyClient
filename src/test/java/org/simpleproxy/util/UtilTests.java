package org.simpleproxy.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UtilTests {

	@Test
	public void contentLengthTest() {
		ByteBuf buff = Unpooled.buffer(256);
		buff.writeCharSequence("abcdef", StandardCharsets.UTF_8);
		assertEquals(6, ProxyUtil.getContentLength(buff));
	}

}
