package site.shanzhao.soil.basis.nio.netty.http.entity;

import lombok.Getter;

/**
 * @author tanruidong
 * @date 2021/01/30 12:37
 */
@Getter
public enum Shipping {
    standard_mail,
    priority_mail,
    international_mail,
    domestic_express,
    international_express;
}
