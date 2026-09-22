package cn.iocoder.yudao.module.marketing.service.gate;

import cn.iocoder.yudao.module.marketing.controller.app.gate.vo.GateResultRespVO;

/** 内容包闸门。实际执行复制、导出时必须重新调用本服务。 */
public interface ContentGateService {
    GateResultRespVO getGateResult(Long packageId);
}
