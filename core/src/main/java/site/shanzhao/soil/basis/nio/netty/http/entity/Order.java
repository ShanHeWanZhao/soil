package site.shanzhao.soil.basis.nio.netty.http.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author tanruidong
 * @date 2021/01/30 12:34
 */
@Data
@Accessors(chain = true)
public class Order {
    private long orderNumber;
    private Customer customer;
    private Shipping shipping;
    private Address shipTo;
    private Address billTo;
    private Float total;
}
