package com.luban.alipay.handler.trade;

import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayTradeQueryHandler extends AbstractAlipayHandler<AlipayTradeQueryModel, AlipayTradeQueryRequest, AlipayTradeQueryResponse> {
    public AlipayTradeQueryHandler(Supplier<AlipayContext<AlipayTradeQueryModel, AlipayTradeQueryRequest, AlipayTradeQueryResponse>> supplier) {
        super(supplier);
    }
}