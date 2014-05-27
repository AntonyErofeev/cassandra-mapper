package ru.tflow.mapping.resolvers;

import com.datastax.driver.core.DataType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import ru.tflow.mapping.ExtendedDataType;
import ru.tflow.mapping.utils.ReflectionUtils;
import ru.tflow.mapping.utils.Tuple3;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Basic interface for field type resolvers
 *
 * User: erofeev
 * Date: 11/30/13
 * Time: 9:18 PM
 */
public interface MappingResolver {

    /**
     * Try to resolve field to serializable into cassandra model data type
     *
     * @param f Field to resolve
     * @return Optional of resolved field or empty if not found
     */
    public Optional<ExtendedDataType> resolve(java.lang.reflect.Field f);

}
