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
        long value;
        try {
            value = NVMHelper.readVarInt(byteArrayInputStream);
            Assert.assertEquals(67305985, value);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testReadVarInt2() {
        byte[] sample = {(byte) 0xFF, 1, 2, 3, 4, 5, 6, 7, 8};
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sample);
        long value;
        try {
            value = NVMHelper.readVarInt(byteArrayInputStream);
            Assert.assertEquals(578437695752307201L, value);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testReadVarInt3() {
        byte[] sample = {(byte) 0xFD, 1, 2};
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sample);
        long value;
        try {
            value = NVMHelper.readVarInt(byteArrayInputStream);
            Assert.assertEquals(513, value);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testReadVarInt4() {
        byte[] sample = {(byte) 0xFC};
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sample);
        long value;
        try {
            value = NVMHelper.readVarInt(byteArrayInputStream);
            Assert.assertEquals(252, value);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testReadVarInt5() {
        byte[] sample = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sample);
        long value;
        try {
            value = NVMHelper.readVarInt(byteArrayInputStream);
            Assert.fail();
        } catch (IOException e) {
            // SUCCESS
        }
    }

}
