package com.github.toolbelt.web.services.statistics.support.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.net.MediaType;
import org.springframework.core.convert.converter.Converter;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;

/**
 * Generic {@code Object -> JSON DataSource} converter based on {@code Jackson} library.
 *
 * @param <S> the source type
 *
 * @author <a href="mailto:y.skopets@gmail.com">Yaroslav Skopets</a>
 */
public class ObjectToJsonDataSourceConverter<S> implements Converter<S, DataSource> {

    private static final String JSON_MEDIA_TYPE = MediaType.JSON_UTF_8.toString();

    private final ObjectMapper objectMapper;

    public ObjectToJsonDataSourceConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ByteArrayDataSource convert(Object source) {
        try {
            final byte[] valueAsBytes = objectMapper.writeValueAsBytes(source);
            return new ByteArrayDataSource(valueAsBytes, JSON_MEDIA_TYPE);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
