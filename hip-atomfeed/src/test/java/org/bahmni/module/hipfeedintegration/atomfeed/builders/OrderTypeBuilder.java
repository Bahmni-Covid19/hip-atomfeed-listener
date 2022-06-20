package org.bahmni.module.hipfeedintegration.atomfeed.builders;

import org.bahmni.module.hipfeedintegration.model.OrderType;

public class OrderTypeBuilder {
    private OrderType orderType;

    public OrderTypeBuilder() {
        orderType = new OrderType();
    }

    public OrderTypeBuilder withName(String orderTypeName) {
        orderType.setName(orderTypeName);
        return this;
    }

    public OrderType build() {
        return orderType;
    }
}
