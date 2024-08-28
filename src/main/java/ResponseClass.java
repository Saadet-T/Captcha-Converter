import static burp.api.montoya.core.ByteArray.byteArray;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpHeader;

public class ResponseClass {

	//Making the response again but on the Captcha Convertor Tab this time
	public ByteArray responseMaker(String httpVersion,short statusCode,List<HttpHeader> responseHeaders ,StringBuilder headerStringBuilder, ObjectMapper objectMapper,ByteArray responseBody) throws IOException {
		headerStringBuilder.append(""+httpVersion+" "+statusCode+"\n");//Getting HTTP version and status code
		for (HttpHeader header : responseHeaders) {//Bautifying headers 
			String headerName = header.name();
			String headerValue = header.value(); 

			headerStringBuilder.append(headerName).append(": ").append(headerValue).append("\r\n");
		}
		headerStringBuilder.append("\r\n");
		headerStringBuilder.append("\r\n");
		try {
			JsonNode prettyJson = objectMapper.readTree(responseBody.toString());//Prettying the JSON LoL:D
			String prettyJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyJson);		
			headerStringBuilder.append(prettyJsonString);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		ByteArray output = byteArray(headerStringBuilder.toString());

		return output;

	}
}
