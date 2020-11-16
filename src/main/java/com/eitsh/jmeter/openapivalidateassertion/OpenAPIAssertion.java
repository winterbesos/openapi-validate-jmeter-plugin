package com.eitsh.jmeter.openapivalidateassertion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class OpenAPIAssertion extends AbstractTestElement implements Serializable, Assertion, TestBean {

    static final String OPEN_API_DOCUMENT_PATH = "openAPIDocumentPath";
    static final String REQUEST_METHOD = "requestMethod";
    static final String REQUEST_PATH = "requestPath";

    private static final Logger log = LoggerFactory.getLogger(OpenAPIAssertion.class);

    public AssertionResult getResult(SampleResult sampleResult) {
        AssertionResult assertionResult = new AssertionResult(getName());

        String openApiDocumentPath = getOpenAPIDocumentPath();
        OpenAPI openAPI = new OpenAPIV3Parser().read(openApiDocumentPath);

        String path = getRequestPath();
        String method = getRequestMethod();
        String statusCode = sampleResult.getResponseCode();
        String mediaType = sampleResult.getMediaType();
        String responseData = sampleResult.getResponseDataAsString();

        ObjectMapper mapper = new ObjectMapper();
        try {
            Schema responseBodySchema = ModelUtil.getResponseBodySchema(openAPI, path, method, statusCode, mediaType);
            JsonNode schemaNode = mapper.readTree(responseData);
            validate(schemaNode, openAPI, responseBodySchema, "ROOT_NODE");
        } catch (Exception e) {
            return new AssertionResult(getName()).setResultForFailure(e.getMessage());
        }

        return assertionResult;
    }

    public void validate(JsonNode jsonNode, OpenAPI api, Schema schema, String propertyName) throws Exception {
        if (schema == null) return;
        if (schema.get$ref() != null) {
            validate(jsonNode, api, ModelUtil.getRefSchema(schema, api), propertyName);
            return;
        }

        JsonNodeType jsonType = jsonNode.getNodeType();

        if (schema instanceof ObjectSchema) {
            if (jsonType != JsonNodeType.OBJECT)
                throw new Exception("property type error, should be `object`: " + propertyName);
            ObjectSchema s = (ObjectSchema) schema;
            List<String> required = Optional.ofNullable(s.getRequired()).orElse(new ArrayList<>());
            for (String name:
                 required) {
                JsonNode node = jsonNode.get(name);
                if (node == null) throw new Exception("nonnull property: " + name);
            }

            for (Map.Entry<String, Schema> entry : s.getProperties().entrySet()) {
                String k = entry.getKey();
                Schema property = entry.getValue();
                JsonNode child = jsonNode.get(k);
                if (child != null && child.getNodeType() != JsonNodeType.NULL)
                    validate(child, api, property, k);
            }
        } else if (schema instanceof ArraySchema) {
            if (jsonType != JsonNodeType.ARRAY) throw new Exception("property type error, should be `array`");
            ArraySchema s = (ArraySchema) schema;
            Schema<?> items = s.getItems();

            for (JsonNode item : jsonNode) {
                validate(item, api, items, propertyName);
            }
        } else if (schema instanceof IntegerSchema) {
            if (jsonType != JsonNodeType.NUMBER)
                throw new Exception("property type error, should be `integer`: " + propertyName);
        } else if (schema instanceof StringSchema) {
            if (jsonType != JsonNodeType.STRING)
                throw new Exception("property type error, should be `string`: " + propertyName);
        } else if (schema instanceof NumberSchema) {
            if (jsonType != JsonNodeType.NUMBER)
                throw new Exception("property type error, should be `number`: " + propertyName);
        } else if (schema instanceof BooleanSchema) {
            if (jsonType != JsonNodeType.BOOLEAN)
                throw new Exception("property type error, should be `boolean`" + propertyName);
        } else if (schema instanceof ComposedSchema) {
            List<Schema> oneOf = ((ComposedSchema) schema).getOneOf();
            if (oneOf != null) {
                boolean hasRight = false;
                for (Schema s : oneOf) {
                    try {
                        validate(jsonNode, api, s, propertyName);
                        hasRight = true;
                        break;
                    } catch (Exception e) {}
                }
                if (!hasRight) throw new Exception("no valid");
            }
            List<Schema> allOf = ((ComposedSchema) schema).getAllOf();
            if (allOf != null) {
                for (Schema s : allOf) {
                    validate(jsonNode, api, s, propertyName);
                }
            }
            List<Schema> anyOf = ((ComposedSchema) schema).getAnyOf();
            if (anyOf != null) {
                for (Schema s : anyOf) {
                    // TODO: implement any of
                }
            }
        } else if (schema instanceof DateTimeSchema) {
            if (jsonType != JsonNodeType.STRING)
                throw new Exception("property type error, should be `string`(date-time): " + propertyName);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            try {
                Date date = sdf.parse(jsonNode.textValue());
                if (date == null) throw new Exception("property type error, should be `string`(date-time): " + propertyName);
            } catch (Exception e) {
                throw new Exception("property type error, should be `string`(date-time): " + propertyName);
            }


        } else {

            throw new Exception("unknown property type" + schema.getClass());
        }
    }

    public String getOpenAPIDocumentPath() {
        return getPropertyAsString(OPEN_API_DOCUMENT_PATH);
    }

    public void setOpenAPIDocumentPath(String path) {
        setProperty(OPEN_API_DOCUMENT_PATH, path);
    }

    public String getRequestMethod() {
        return getPropertyAsString(REQUEST_METHOD);
    }

    public void setRequestMethod(String method) {
        setProperty(REQUEST_METHOD, method);
    }

    public String getRequestPath() {
        return getPropertyAsString(REQUEST_PATH);
    }

    public void setRequestPath(String path) {
        setProperty(REQUEST_PATH, path);
    }

}
