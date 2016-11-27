package eu.balev.davicasa.inject;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

/**
 * Injects the actual SLF4J logger in the properly annotated field.
 */
public class SLF4JMembersInjector<T> implements MembersInjector<T> {

	private final Field field;
	private final Logger logger;

	SLF4JMembersInjector(Field field) {

		this.field = field;
		this.logger = LoggerFactory.getLogger(field.getDeclaringClass());
		field.setAccessible(true);
	}

	public void injectMembers(T t) {
		try {
			field.set(t, logger);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}