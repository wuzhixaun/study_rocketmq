package com.wuzx.fun.study_rocketmq.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzhixuan on 19-10-16.
 */
@AllArgsConstructor
@Data
public class ProductOrder implements Serializable {

    //订单id
    private long orderId;

    //订单类型
    private String type;


    public static List<ProductOrder> getProductOrderList() {
        List<ProductOrder> list = new ArrayList<>();
        list.add(new ProductOrder(111L, "创建订单"));
        list.add(new ProductOrder(222L, "创建订单"));
        list.add(new ProductOrder(111L, "支付订单"));
        list.add(new ProductOrder(222L, "支付订单"));
        list.add(new ProductOrder(333L, "创建订单"));
        list.add(new ProductOrder(222L, "完成订单"));
        list.add(new ProductOrder(333L, "支付订单"));
        list.add(new ProductOrder(111L, "完成订单"));
        list.add(new ProductOrder(333L, "完成订单"));

        return list;
    }
}
