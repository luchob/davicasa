package eu.balev.davicasa.processors.removeduplicates;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A base class for processing of potential identical objects.
 * 
 * The utility class receives a list of objects and a comparator that is able to
 * identify which objects are identical. The list of identical objects is then
 * reduced to a single object (e.g. duplicates may be deleted).
 * 
 * The reduction itself is performed in the subclasses. Subclasses must
 * implement the {@link IdenticalObjectsProcessor#reduce(Object, Object)} method
 * and the {@link IdenticalObjectsProcessor#reduce(Object)} in case that only a
 * unique object is found in the provided list.
 */
abstract class IdenticalObjectsProcessor<T>
{
	/**
	 * Processes the identical objects in the provided list.
	 * 
	 * @param objectsList
	 *            the list of objects
	 * @param objectsComparator
	 *            a comparator which is able to identify identical objects
	 */
	void processIdenticalObject(List<T> objectsList,
			Comparator<T> objectsComparator)
	{
		List<T> objectsListCopy = new LinkedList<>(objectsList);

		while (!objectsListCopy.isEmpty())
		{
			T first = objectsListCopy.remove(0);
			List<T> identicals = new LinkedList<T>();
			identicals.add(first);

			Iterator<T> remainingsIter = objectsListCopy.iterator();
			while (remainingsIter.hasNext())
			{
				T next = remainingsIter.next();
				if (objectsComparator.compare(first, next) == 0)
				{
					identicals.add(next);
					remainingsIter.remove();
				}
			}

			processIdenticals(identicals);
		}
	}

	/**
	 * Processes a list of object(s) which are identified as unique.
	 *  
	 * @param identicalObjects
	 */
	private void processIdenticals(List<T> identicalObjects)
	{
		if (identicalObjects.isEmpty())
		{
			throw new IllegalArgumentException(
					"This should not happen, we expect at least one object...");
		}
		else if (identicalObjects.size() == 1)
		{
			unique(identicalObjects.get(0));
		}
		else
		{
			identicalObjects.stream().reduce(this::reduce);
		}
	}

	/**
	 * Reduces two identical objects to one. For example one may be deleted.  
	 * 
	 * @param first the first of the two identical objects
	 * @param second the second of the two identical objects
	 * 
	 * @return a reduced object
	 */
	protected abstract T reduce(T first, T second);

	/**
	 * Processes a unique object - one for which no duplicate had been found.
	 * 
	 * @param object the unique object
	 */
	protected abstract void unique(T object);
}
