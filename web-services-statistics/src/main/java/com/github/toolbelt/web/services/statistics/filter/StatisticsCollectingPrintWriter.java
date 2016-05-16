package com.github.toolbelt.web.services.statistics.filter;

import com.github.toolbelt.web.services.statistics.support.io.DelegatingPrintWriter;
import com.github.toolbelt.web.services.statistics.timing.Clock;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ReflectionUtils;

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

/**
 * {@code Decorator} of {@link javax.servlet.ServletResponse#getWriter()} that collects generic response {@code metrics},
 * such as {@code Time Till First Response Byte In Millis} and {@code Response Body Written Chars Count}.
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class StatisticsCollectingPrintWriter extends DelegatingPrintWriter {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsCollectingPrintWriter.class);

    private static final Function<Class, String> CLASS_NAME_FUNCTION = new Function<Class, String>() {
        @Override
        public String apply(Class aClass) {
            return aClass.getName();
        }
    };

    private static final long UNKNOWN_WRITTEN_CHARS_COUNT = 0L;

    private static final Long UNKNOWN_TIME_FIRST_CHAR_WRITTEN_MILLIS = null;

    private static final Field PRINT_WRITER_OUT_FIELD = ReflectionUtils.findField(PrintWriter.class, "out", Writer.class);

    private static final ImmutableSet<Class> ATTESTED_PRINT_WRITER_CLASSES = ImmutableSet.<Class>of(PrintWriter.class);

    private static final ImmutableSet<String> SUPPORTED_PRINT_WRITER_CLASSES;

    static {
        ReflectionUtils.makeAccessible(PRINT_WRITER_OUT_FIELD);

        SUPPORTED_PRINT_WRITER_CLASSES = ImmutableSet.<String>builder()
                .addAll(Iterables.transform(ATTESTED_PRINT_WRITER_CLASSES, CLASS_NAME_FUNCTION))
                // Piggyback on standard Spring facilities in order to define a list of PrintWriter subclasses that can be reliably instrumented
                .addAll(SpringFactoriesLoader.loadFactoryNames(StatisticsCollectingPrintWriter.class, StatisticsCollectingPrintWriter.class.getClassLoader()))
                .build();
    }

    private final StatisticsCollectingWriter instrumentedWriter;

    public StatisticsCollectingPrintWriter(PrintWriter actualPrintWriter, Clock clock) {
        this(actualPrintWriter, clock, SUPPORTED_PRINT_WRITER_CLASSES);
    }

    @VisibleForTesting
    public StatisticsCollectingPrintWriter(PrintWriter actualPrintWriter, Clock clock, Class... supportedPrintWriterClasses) {
        this(actualPrintWriter, clock, FluentIterable.from(
                ImmutableSet.<Class>builder()
                        .addAll(ATTESTED_PRINT_WRITER_CLASSES)
                        .add(supportedPrintWriterClasses)
                        .build())
                .transform(CLASS_NAME_FUNCTION)
                .toSet());
    }

    protected StatisticsCollectingPrintWriter(PrintWriter actualPrintWriter, Clock clock, ImmutableSet<String> supportedPrintWriterClasses) {
        super(actualPrintWriter);
        this.instrumentedWriter = instrument(actualPrintWriter, clock, supportedPrintWriterClasses);
    }

    protected StatisticsCollectingWriter instrument(PrintWriter actualPrintWriter, Clock clock, ImmutableSet<String> supportedPrintWriterClasses) {
        final String printWriterClass = actualPrintWriter.getClass().getName();
        if (supportedPrintWriterClasses.contains(printWriterClass)) {
            final Writer actualWriter = (Writer) ReflectionUtils.getField(PRINT_WRITER_OUT_FIELD, actualPrintWriter);
            final StatisticsCollectingWriter instrumentedWriter = new StatisticsCollectingWriter(actualWriter, clock);
            ReflectionUtils.setField(PRINT_WRITER_OUT_FIELD, actualPrintWriter, instrumentedWriter);
            return instrumentedWriter;
        } else {
            LOG.warn("Can't instrument PrintWriter of type '{}'.\n" +
                            "Consequently, generic response metrics, such as `Time Till First Response Byte In Millis` and " +
                            "`Response Body Written Chars Count`, will be unavailable for that output stream.\n" +
                            "\n" +
                            "In order to resolve the issue, you need to study the source code of '{}'\n" +
                            "and make sure instrumentation will work properly with it.\n" +
                            "Once you're certain, put a file `META-INF/spring.factories` with the following contents somewhere on the application classpath:\n" +
                            "\n" +
                            "---------------------------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                            "| Contents of an example `META-INF/spring.factories` file that enables instrumentation of '{}':\n" +
                            "---------------------------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                            "| # Piggyback on standard Spring facilities in order to define a list of PrintWriter subclasses that can be reliably instrumented\n" +
                            "| com.github.toolbelt.web.services.statistics.filter.StatisticsCollectingPrintWriter=\\\n" +
                            "| %{}\n" +
                            "---------------------------------------------------------------------------------------------------------------------------------------------------------------------\n",
                    printWriterClass, printWriterClass, printWriterClass, printWriterClass);
        }
        return null;
    }

    public boolean isStatisticsCollected() {
        return instrumentedWriter != null;
    }

    public long getWrittenCharsCount() {
        return instrumentedWriter != null ? instrumentedWriter.getWrittenCharsCount() : UNKNOWN_WRITTEN_CHARS_COUNT;
    }

    public Long getTimeFirstCharWrittenMillis() {
        return instrumentedWriter != null ? instrumentedWriter.getTimeFirstCharWrittenMillis() : UNKNOWN_TIME_FIRST_CHAR_WRITTEN_MILLIS;
    }
}
