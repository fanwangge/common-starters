package com.luban.alipay.handler.other;

import com.alipay.api.domain.AlipayCommerceCityfacilitatorVoucherGenerateModel;
import com.alipay.api.request.AlipayCommerceCityfacilitatorVoucherGenerateRequest;
import com.alipay.api.response.AlipayCommerceCityfacilitatorVoucherGenerateResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayCommerceCityfacilitatorVoucherGenerateHandler extends AbstractAlipayHandler<AlipayCommerceCityfacilitatorVoucherGenerateModel, AlipayCommerceCityfacilitatorVoucherGenerateRequest, AlipayCommerceCityfacilitatorVoucherGenerateResponse> {
    public AlipayCommerceCityfacilitatorVoucherGenerateHandler(Supplier<AlipayContext<AlipayCommerceCityfacilitatorVoucherGenerateModel, AlipayCommerceCityfacilitatorVoucherGenerateRequest, AlipayCommerceCityfacilitatorVoucherGenerateResponse>> supplier) {
        super(supplier);
    }
}
