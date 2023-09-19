package com.luban.alipay.handler.trade;

import cn.hutool.core.map.MapUtil;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;
import com.luban.alipay.request.AlipayRequestUpdater;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayTradePayHandler extends AbstractAlipayHandler<AlipayTradePayModel, AlipayTradePayRequest, AlipayTradePayResponse> {

    public AlipayTradePayHandler(Supplier<AlipayContext<AlipayTradePayModel, AlipayTradePayRequest, AlipayTradePayResponse>> supplier) {
        super(supplier);
    }


    @Override
    public AlipayRequestUpdater<AlipayTradePayModel, AlipayTradePayRequest, AlipayTradePayResponse> otherTextParam(Map<String, String> otherTextParam) {
        if (MapUtil.isNotEmpty(otherTextParam)) {
            final AlipayTradePayRequest request = this.alipayContext.request;
            otherTextParam.forEach(request::putOtherTextParam);
        }
        return this;
    }
}
