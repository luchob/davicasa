package eu.balev.davicasa.processors.inject;

import java.lang.reflect.Field;

import org.slf4j.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * A Guice type listener for SLF4J loggers.
 */
public class SLF4JTypeListener implements TypeListener {

	public <T> void hear(TypeLiteral<T> typeLiteral,
			TypeEncounter<T> typeEncounter) {

		// the class which members will be injected
		Class<?> clazz = typeLiteral.getRawType();

		while (clazz != null) {

			// any fields which are annotated with inject logger
			for (Field field : clazz.getDeclaredFields()) {
				if (field.getType() == Logger.class
						&& field.isAnnotationPresent(InjectLogger.class)) {

					typeEncounter.register(new SLF4JMembersInjector<T>(field));
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
}