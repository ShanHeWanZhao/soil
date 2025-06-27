package site.shanzhao.soil.basis.nio.netty.http.entity;

/**
 * @author tanruidong
 * @date 2021/01/30 15:21
 */
public class OrderFactory {
    public static Order create(long orderId){
        Address address = new Address();
        address.setCity("成都市")
                .setCountry("中国")
                .setPostCode("123321")
                .setState("四川省")
                .setStreet1("天府大道");
        Customer customer = new Customer();
        customer.setCustomerNumber(orderId)
                .setFirstName("谭")
                .setLastName("瑞东");
        Order order = new Order();
        order.setOrderNumber(orderId)
                .setTotal(9999.99f)
                .setBillTo(address)
                .setCustomer(customer)
                .setShipping(Shipping.international_mail)
                .setShipTo(address);
        return order;
    }
}
