package ninja.ranner.xogame.spring;

import com.zaxxer.hikari.util.ConcurrentBag;
import ninja.ranner.xogame.domain.CellFilled;
import ninja.ranner.xogame.domain.GameCreated;
import ninja.ranner.xogame.domain.GameDrawn;
import ninja.ranner.xogame.domain.GameWon;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import javax.sql.rowset.spi.SyncFactory;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(RuntimeHints.class)
public class RuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(org.springframework.aot.hint.RuntimeHints hints,
                              ClassLoader classLoader) {

        List<Class<?>> eventClasses = List.of(
                GameCreated.class,
                CellFilled.class,
                GameDrawn.class,
                GameWon.class
        );
        eventClasses.stream()
                    .flatMap(t -> Arrays.stream(t.getConstructors()))
                    .flatMap(c -> Arrays.stream(c.getParameterTypes()))
                    .distinct()
                    .forEach(param -> hints.reflection()
                                           .registerType(param,
                                                   MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                                   MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS,
                                                   MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
                                                   MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                                                   MemberCategory.INTROSPECT_PUBLIC_METHODS,
                                                   MemberCategory.INVOKE_PUBLIC_METHODS,
                                                   MemberCategory.DECLARED_FIELDS
                                           ));

        eventClasses.forEach(clazz -> hints.reflection()
                                           .registerType(
                                                   clazz,
                                                   MemberCategory.INTROSPECT_PUBLIC_METHODS,
                                                   MemberCategory.INVOKE_PUBLIC_METHODS,
                                                   MemberCategory.DECLARED_FIELDS,
                                                   MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS));

        registerJdbcTemplateAccess(hints);
    }

    private void registerJdbcTemplateAccess(org.springframework.aot.hint.RuntimeHints hints) {
        hints.reflection()
             .registerType(
                     Statement[].class,
                     MemberCategory.UNSAFE_ALLOCATED
             )
             .registerType(
                     ConcurrentBag.IConcurrentBagEntry[].class,
                     MemberCategory.UNSAFE_ALLOCATED
             )
             .registerType(
                     TypeReference.of("com.sun.rowset.providers.RIOptimisticProvider"),
                     MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
             .registerType(
                     TypeReference.of("com.sun.rowset.providers.RIXMLProvider"),
                     MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
             .registerType(
                     java.sql.Types.class,
                     MemberCategory.PUBLIC_FIELDS);

        hints.resources()
             .registerResourceBundle("com.sun.rowset.RowSetResourceBundle")
             .registerPattern(x -> x.includes(TypeReference.of(SyncFactory.class), "*rowset.properties"));
    }
}