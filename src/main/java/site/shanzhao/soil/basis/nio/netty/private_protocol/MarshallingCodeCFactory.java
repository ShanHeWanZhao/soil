package site.shanzhao.soil.basis.nio.netty.private_protocol;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.*;

/**
 * @author tanruidong
 * @date 2021/02/19 11:12
 */
public class MarshallingCodeCFactory {
    public static NettyMarshallingDecoder buildMarshallingDecoder(){
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        return new NettyMarshallingDecoder(provider);
    }

    public static NettyMarshallingEncoder buildMarshallingEncoder(){
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        return new NettyMarshallingEncoder(provider);
    }
}
