package com.taduk.analyzer.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;

public class PredicateImpl implements Predicate {

	private String propertyName;
	private Object inValue;

	public PredicateImpl(String propertyName, Object inValue) {
		this.propertyName = propertyName;
		this.inValue = inValue;
	}

	@Override
	public boolean evaluate(Object object) {
		try {
			Object beanValue;
			if (propertyName.indexOf(".") > 0) {
				beanValue = PropertyUtils.getNestedProperty(object, propertyName);
			} else {
				beanValue = PropertyUtils.getProperty(object, propertyName);	
			}
			if (beanValue == null)
				return false;
			if (!beanValue.getClass().equals(inValue.getClass())) {
				 throw new RuntimeException("value.class!=beanValue.class");
			}
			return compare(inValue, beanValue);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		
	}

	private boolean compare(Object inValue, Object beanValue) {
		return inValue.equals(beanValue);
	}
	
	
	public static void main(String[] args) {
	}
	

}
