package com.eitsh.jmeter.openapivalidateassertion;

import org.apache.jmeter.testbeans.BeanInfoSupport;

import java.beans.PropertyDescriptor;

public class OpenAPIAssertionBeanInfo extends BeanInfoSupport {

    public OpenAPIAssertionBeanInfo() {
        super(OpenAPIAssertion.class);

        PropertyDescriptor p;
        p = property(OpenAPIAssertion.OPEN_API_DOCUMENT_PATH);
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property(OpenAPIAssertion.REQUEST_METHOD);
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "GET");

        p = property(OpenAPIAssertion.REQUEST_PATH);
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

    }

}
