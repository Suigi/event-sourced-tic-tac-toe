package ninja.ranner.xogame.spring;

import ninja.ranner.xogame.domain.CellFilled;
import ninja.ranner.xogame.domain.GameCreated;
import ninja.ranner.xogame.domain.GameDrawn;
import ninja.ranner.xogame.domain.GameWon;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

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

        eventClasses.forEach(clazz -> {
            hints.reflection().registerType(
                    clazz,
                    MemberCategory.INTROSPECT_PUBLIC_METHODS,
                    MemberCategory.INVOKE_PUBLIC_METHODS,
                    MemberCategory.DECLARED_FIELDS);
        });

    }
}