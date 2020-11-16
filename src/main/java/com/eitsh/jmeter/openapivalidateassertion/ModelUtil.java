package com.eitsh.jmeter.openapivalidateassertion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModelUtil {

    static Schema getResponseBodySchema(OpenAPI api, String path, String method, String statusCode, String mediaType) throws Exception {
        PathItem pathItem = api.getPaths().get(path);
        if (pathItem == null) throw new Exception("request path is undefined: " + path);

        Operation operation;
        switch (method.toLowerCase()) {
            case "get":
                operation = pathItem.getGet();
                break;
            case "post":
                operation = pathItem.getPost();
                break;
            case "put":
                operation = pathItem.getPut();
                break;
            case "delete":
                operation = pathItem.getDelete();
                break;
            case "patch":
                operation = pathItem.getPatch();
                break;
            default:
                throw new Exception("method is invalid");
        }

        ApiResponses responses = operation.getResponses();
        ApiResponse apiResponse = responses.get(statusCode);
        if (apiResponse == null) throw new Exception("undefined response code: " + statusCode);

        MediaType media = apiResponse.getContent().get(mediaType.toLowerCase());
        if (media == null) throw new Exception("undefined media: " + mediaType);

        Schema bodySchema = media.getSchema();
        return bodySchema;
    }

    static Schema getRefSchema(Schema schema, OpenAPI api) {
        if (schema != null && api != null && StringUtils.isNotBlank(schema.get$ref())) {
            String name = getSimpleRef(schema.get$ref());
            Schema referencedSchema = getSchema(api, name);
            if (referencedSchema != null) {
                return referencedSchema;
            }
        }
        return schema;
    }

    public static String getSimpleRef(String ref) {
        if (ref.startsWith("#/components/")) {
            ref = ref.substring(ref.lastIndexOf("/") + 1);
        } else if (ref.startsWith("#/definitions/")) {
            ref = ref.substring(ref.lastIndexOf("/") + 1);
        } else {
            //throw new RuntimeException("Failed to get the schema: " + ref);
            return null;
        }

        return ref;
    }

    public static Schema getSchema(OpenAPI openAPI, String name) {
        if (name == null) {
            return null;
        }

        return getSchemas(openAPI).get(name);
    }

    public static Map<String, Schema> getSchemas(OpenAPI openAPI) {
        if (openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
            return openAPI.getComponents().getSchemas();
        }
        return Collections.emptyMap();
    }

    public static List<Schema> getInterfaces(ComposedSchema composed) {
        if (composed.getAllOf() != null && !composed.getAllOf().isEmpty()) {
            return composed.getAllOf();
        } else if (composed.getAnyOf() != null && !composed.getAnyOf().isEmpty()) {
            return composed.getAnyOf();
        } else if (composed.getOneOf() != null && !composed.getOneOf().isEmpty()) {
            return composed.getOneOf();
        } else {
            return Collections.<Schema>emptyList();
        }
    }


}
