package com.github.toolbelt.web.services.statistics.support.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.toolbelt.web.services.statistics.support.json.mixin.FrameIdMixin;
import com.github.toolbelt.web.services.statistics.support.json.mixin.FrameMixin;
import com.github.toolbelt.web.services.statistics.support.json.mixin.OperationListImplMixin;
import com.github.toolbelt.web.services.statistics.support.json.mixin.OperationMapImplMixin;
import com.github.toolbelt.web.services.statistics.support.json.mixin.OperationMixin;
import com.github.toolbelt.web.services.statistics.support.json.mixin.OperationTypeMixin;
import com.github.toolbelt.web.services.statistics.support.json.mixin.TimeRangeMixin;
import com.google.common.base.Throwables;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationType;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.FrameId;
import com.springsource.insight.util.time.TimeRange;
import org.springframework.util.ClassUtils;

/**
 * Responsible for {@code Java -> JSON} mapping of {@code Spring Insight} internal classes.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class SpringInsightModule extends SimpleModule {

    private static final String MODULE_NAME = "spring-insight";

    private static final Version MODULE_VERSION = VersionUtil.parseVersion(
            "1.9.1", "com.fasterxml.jackson.datatype", "jackson-datatype-spring-insight");

    private static final Class OPERATION_LIST_IMPL_CLASS;
    private static final Class OPERATION_MAP_IMPL_CLASS;

    static {
        try {
            // obtain references to non-public classes
            OPERATION_LIST_IMPL_CLASS = ClassUtils.forName("com.springsource.insight.intercept.operation.OperationListImpl",
                    RequestStatisticsMapperFactory.class.getClassLoader());
            OPERATION_MAP_IMPL_CLASS = ClassUtils.forName("com.springsource.insight.intercept.operation.OperationMapImpl",
                    RequestStatisticsMapperFactory.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw Throwables.propagate(e);
        }
    }

    public SpringInsightModule() {
        super(MODULE_NAME, MODULE_VERSION);

        // customize serialization of {@code Spring Insight} core classes
        setMixInAnnotation(Frame.class, FrameMixin.class);
        setMixInAnnotation(FrameId.class, FrameIdMixin.class);
        setMixInAnnotation(Operation.class, OperationMixin.class);
        setMixInAnnotation(OperationType.class, OperationTypeMixin.class);
        setMixInAnnotation(OPERATION_LIST_IMPL_CLASS, OperationListImplMixin.class);
        setMixInAnnotation(OPERATION_MAP_IMPL_CLASS, OperationMapImplMixin.class);
        setMixInAnnotation(TimeRange.class, TimeRangeMixin.class);
    }
}
