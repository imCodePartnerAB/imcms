package imcode.util;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.BeanItem;

import java.util.Collection;

/**
 * A {@link com.vaadin.data.Property} that references a single property
 * of many similar {@link com.vaadin.data.util.BeanItem}'s.
 * <p>
 * The getter methods retrieve the value from the first {@link com.vaadin.data.util.BeanItem} in the list of
 * bean items.
 * </p>
 * <p>
 * The setter methods set the value on all of the {@link com.vaadin.data.util.BeanItem}'s.
 * </p>
 *
 * @param <T>   type of the property value
 */
public class MultiSetBeanItemProperty<T> extends AbstractProperty<T> {
    private String propertyName;
    private Class<T> propertyClass;
    private Collection<BeanItem<?>> beanItems;

    public MultiSetBeanItemProperty(String propertyName, Class<T> propertyClass, Collection<BeanItem<?>> beanItems) {
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
        this.beanItems = beanItems;

        if (beanItems.isEmpty()) {
            throw new IllegalArgumentException("Must have at least 1 BeanItem");
        }
    }

    @Override
    public T getValue() {
        return (T) getBaseProperty().getValue();
    }

    @Override
    public void setValue(T newValue) throws ReadOnlyException {
        for (BeanItem beanItem : beanItems) {
            Property prop = beanItem.getItemProperty(propertyName);
            prop.setValue(newValue);
        }

        fireValueChange();
    }

    @Override
    public boolean isReadOnly() {
        return getBaseProperty().isReadOnly();
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        boolean changed = (getBaseProperty().isReadOnly() != newStatus);

        for (BeanItem beanItem : beanItems) {
            Property prop = beanItem.getItemProperty(propertyName);
            prop.setReadOnly(newStatus);
        }

        if (changed) {
            fireReadOnlyStatusChange();
        }
    }

    protected Property getBaseProperty() {
        BeanItem beanItem = beanItems.iterator().next();

        return beanItem.getItemProperty(propertyName);
    }

    @Override
    public Class<? extends T> getType() {
        return propertyClass;
    }
}
