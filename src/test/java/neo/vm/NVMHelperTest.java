package neo.vm;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class NVMHelperTest {

    @Test
    public void testReadVarBytes() {
        byte[] sample = {5, 'a', 'b', 'c', 'd', 'e'};
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sample);
        try {
            byte[] data = NVMHelper.readVarBytes(byteArrayInputStream);
            byte[] expected = new byte[]{'a', 'b', 'c', 'd', 'e'};
            Assert.assertArrayEquals(expected, data);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testReadVarInt() {
        byte[] sample = {(byte) 0xFE, 1, 2, 3, 4};
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sample);
        long value = 0;
        try {
            value = NVMHelper.readVarInt(byteArrayInputStream);
        } catch (IOException e) {
            Assert.fail();
        }
        Assert.assertEquals(67305985, value);
    }

}
