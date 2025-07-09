package site.shanzhao.soil.basis.nio.netty;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author tanruidong
 * @date 2021/01/23 16:04
 */
@Data
@Builder
public class UserInfo implements Serializable {
    private String username;
    private int userId;

    @Tolerate
    public UserInfo() {
    }
    public byte[] codeC(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] nameBytes = this.username.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(nameBytes.length);
        buffer.put(nameBytes);
        buffer.putInt(this.userId);
        buffer.flip();
        nameBytes = null;
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

}
