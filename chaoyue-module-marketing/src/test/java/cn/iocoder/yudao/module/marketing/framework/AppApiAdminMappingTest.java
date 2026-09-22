package cn.iocoder.yudao.module.marketing.framework;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.web.config.WebProperties;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 详细设计 00 §2：/app-api/marketing/** 必须判定为 ADMIN。
 */
class AppApiAdminMappingTest {

    @BeforeEach
    void setUp() {
        new WebFrameworkUtils(new WebProperties());
    }

    @Test
    void marketingPing_isAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/app-api/marketing/ping");
        assertEquals(UserTypeEnum.ADMIN.getValue(), WebFrameworkUtils.getLoginUserType(request));
    }

    @Test
    void otherAppApi_isMember() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/app-api/system/dict-data/type");
        assertEquals(UserTypeEnum.MEMBER.getValue(), WebFrameworkUtils.getLoginUserType(request));
    }

    @Test
    void adminApi_isAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/admin-api/system/user/page");
        assertEquals(UserTypeEnum.ADMIN.getValue(), WebFrameworkUtils.getLoginUserType(request));
    }
}
