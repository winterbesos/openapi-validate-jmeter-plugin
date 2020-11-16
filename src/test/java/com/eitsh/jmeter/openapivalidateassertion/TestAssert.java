package com.eitsh.jmeter.openapivalidateassertion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.Test;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestAssert {
    @Test
    public void testGetResult() {
        String responseData = "{\n" +
                "  \"result\": {\n" +
                "    \"buildVersion\": \"1.6.2\",\n" +
                "    \"buildVersionNo\": \"225\",\n" +
                "    \"buildShortcutUrl\": \"https://www.pgyer.com/HfgX\",\n" +
                "    \"downloadURL\": \"itms-services://?action=download-manifest&url=https://www.pgyer.com/app/plist/196b4cb118a6bc1392d70d471158a362/update/s.plist\",\n" +
                "    \"forceUpdate\": false\n" +
                "  }\n" +
                "}";

        SampleResult result = mock(SampleResult.class);
        when(result.getResponseCode()).thenReturn("200");
        when(result.getMediaType()).thenReturn("application/json");
        when(result.getResponseDataAsString()).thenReturn(responseData);

        OpenAPIAssertion assertion = mock(OpenAPIAssertion.class);
        when(assertion.getResult(result)).thenCallRealMethod();
        when(assertion.getPropertyAsString(OpenAPIAssertion.OPEN_API_DOCUMENT_PATH))
                .thenReturn("http://10.9.21.226/docs/rulai/openapi.yaml");
        when(assertion.getPropertyAsString(OpenAPIAssertion.REQUEST_METHOD))
                .thenReturn("POST");
        when(assertion.getPropertyAsString(OpenAPIAssertion.REQUEST_PATH))
                .thenReturn("/check");

//        when(assertion.validate(any(), any(), any())).thenCallRealMethod();

//        verify(assertion, times(1)).getResult(result);

    }

    @Test
    public void testValidate() throws Exception {
        String responseData = "{\n" +
                "  \"status\": true,\n" +
                "  \"statusCode\": \"SYS0001\",\n" +
                "  \"message\": \"请求成功\",\n" +
                "  \"result\": {\n" +
                "    \"buildVersion\": \"1.6.2\",\n" +
                "    \"buildVersionNo\": \"225\",\n" +
                "    \"buildShortcutUrl\": \"https://www.pgyer.com/HfgX\",\n" +
                "    \"downloadURL\": \"itms-services://?action=download-manifest&url=https://www.pgyer.com/app/plist/196b4cb118a6bc1392d70d471158a362/update/s.plist\",\n" +
                "    \"forceUpdate\": false\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(responseData);

        OpenAPI openAPI = new OpenAPIV3Parser().read("http://10.9.21.226/docs/rulai/openapi.yaml");

        Schema schema = new Schema();
        schema.set$ref( "#/components/schemas/VersionInfoGet");

        OpenAPIAssertion assertion = new OpenAPIAssertion();

        assertion.validate(schemaNode, openAPI, schema, "ROOT_NODE");

    }

    @Test
    public void test3() throws Exception {
        String responseData = "{\"status\":true,\"message\":\"操作成功\",\"statusCode\":\"SYS000\",\"result\":{\"pageSize\":10,\"pageNum\":1,\"totalCount\":1,\"data\":[{\"id\":\"27\",\"productName\":\"蓝网航空-即日\",\"fromCounty\":{\"code\":\"310118\",\"countyName\":\"青浦区\",\"cityName\":\"上海市\",\"provinceName\":\"上海\"},\"toCounty\":{\"code\":\"110105\",\"countyName\":\"朝阳区\",\"cityName\":\"北京市\",\"provinceName\":\"北京\"},\"takingEndPoint\":{\"name\":\"北京星联01号网点\",\"code\":\"bjxl01\",\"county\":{\"code\":\"310118\",\"countyName\":\"青浦区\",\"cityName\":\"上海市\",\"provinceName\":\"上海\"}},\"sendingEndPoint\":{\"name\":\"上海星联01号网点\",\"code\":\"shxl01\",\"county\":{\"code\":\"110105\",\"countyName\":\"朝阳区\",\"cityName\":\"北京市\",\"provinceName\":\"北京\"}},\"startTime\":\"2020-11-11T10:39:43\",\"endTime\":\"2020-11-11T12:39:43\",\"transferType\":\"空运\",\"validDay\":1,\"signInTime\":\"2020-11-11T13:39:43\",\"startWeight\":10.1,\"startPrice\":5,\"continuedWeight\":1.0,\"continuedPrice\":0,\"throwingCoefficient\":100}]}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(responseData);

        OpenAPI openAPI = new OpenAPIV3Parser().read("http://10.9.21.226/docs/integration/openapi.yaml");

        Schema schema = new Schema();
        schema.set$ref( "#/components/schemas/RESGetBlueProductRoutes");

        OpenAPIAssertion assertion = new OpenAPIAssertion();

        assertion.validate(schemaNode, openAPI, schema, "ROOT_NODE");
    }

    @Test
    public void test4() throws Exception {
        String responseData = "{\"status\":true,\"message\":\"操作成功\",\"statusCode\":\"SYS000\",\"result\":{\"errorItems\":[],\"successItems\":[28]}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(responseData);

        OpenAPI openAPI = new OpenAPIV3Parser().read("http://10.9.21.226/docs/integration/openapi.yaml");

        Schema schema = new Schema();
        schema.set$ref( "#/components/schemas/RESImportBlueProductConfigurationLines");

        OpenAPIAssertion assertion = new OpenAPIAssertion();

        assertion.validate(schemaNode, openAPI, schema, "ROOT_NODE");
    }

    @Test
    public void test5() throws Exception {
        String responseData = "{\"status\":true,\"message\":\"操作成功\",\"statusCode\":\"SYS000\",\"result\":{\"id\":539,\"systemName\":\"一体化物流平台\",\"moduleName\":\"产品线路\",\"fileName\":\"产品线路列表20201116190347.xlsx\",\"fileUrl\":null,\"conditionJson\":\"{\\\"pageNum\\\":1,\\\"pageSize\\\":20,\\\"productId\\\":9,\\\"sorted\\\":{}}\",\"status\":0,\"statusName\":\"等待中\",\"percent\":0,\"createDate\":null}}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(responseData);

        OpenAPI openAPI = new OpenAPIV3Parser().read("http://10.9.21.226/docs/integration/openapi.yaml");

        Schema schema = new Schema();
        schema.set$ref( "#/components/schemas/RESExportResult");

        OpenAPIAssertion assertion = new OpenAPIAssertion();

        assertion.validate(schemaNode, openAPI, schema, "ROOT_NODE");
    }

    @Test
    public void test6() throws Exception {
        String responseData = "{\"status\":true,\"message\":\"操作成功\",\"statusCode\":\"SYS000\"}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(responseData);

        OpenAPI openAPI = new OpenAPIV3Parser().read("http://10.9.21.226/docs/integration/openapi.yaml");

        Schema schema = new Schema();
        schema.set$ref( "#/components/schemas/Result");

        OpenAPIAssertion assertion = new OpenAPIAssertion();

        assertion.validate(schemaNode, openAPI, schema, "ROOT_NODE");
    }
}
