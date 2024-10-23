/**
*
* @author Saadet Elif Tokuoglu/ Lzzap S3curity
* @email saadet.elif@lzzapsecurity.com
*/

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import burp.api.montoya.ui.editor.extension.HttpResponseEditorProvider;

public class CaptchaConvertorExtension implements BurpExtension  {

	private MontoyaApi Outerapi;
	
	@Override
	public void initialize(MontoyaApi api) {
		api.logging().logToOutput("============{####}============");
		api.logging().logToOutput(" Captcha Converter extension has been loaded.");
		api.logging().logToOutput(" Developed by Lzzaps3c.");
		api.logging().logToOutput("============{####}============");
		api.extension().setName("Captcha Converter");
		api.userInterface().registerHttpResponseEditorProvider(new CaptchaTab(api));
		this.Outerapi=api;
		
	}

	class CaptchaTab implements HttpResponseEditorProvider {
		private final MontoyaApi api;
		CaptchaTab(MontoyaApi api) {
			this.api = api;
		}
		
		@Override
		public ExtensionProvidedHttpResponseEditor provideHttpResponseEditor(EditorCreationContext creationContext) {
			return new CaptchaConvert(api, creationContext);
		}

	}

	class CaptchaConvert implements  ExtensionProvidedHttpResponseEditor {
		private JPanel container = new JPanel(new BorderLayout()); //Making container so we can output our image or error
		private JsonNode jSON;//We are using JSON because PNG can be rendered by burp itself we are focusing on base64 image responses which usually in JSON
		private final RawEditor responseEditor;//This is how we output our response back in extension's tab
		String regex = "/9j/4A[^\"]*|iVBORw0KGgo[^\"]*|/9j/2wBDAAMCAgMCAg[^\"]*|/9j/7g[^\"]|/9j/4Q[^\"]";//Regex for Magic Bytes JPEG,JPG,JFIF,PNG
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		BufferedImage bufImage;//Image

		@Override
		public Component uiComponent() {
			return container;//we are using out JPanel as our UI component
		}


		CaptchaConvert(MontoyaApi api, EditorCreationContext creationContext) {//Hey you If you are reading this to develop your own extension
				responseEditor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
		}

		@Override
		public boolean isEnabledFor(HttpRequestResponse requestResponse) {//Enabling CaptchaConverter Tab if response contains JSON data
			ObjectMapper objectMapper = new ObjectMapper();
			ByteArray responseBody = requestResponse.response().body();
			HttpResponse response = requestResponse.response();
			try {
				jSON = objectMapper.readTree(responseBody.toString());
			} catch (JsonMappingException e) {
				Outerapi.logging().logToError(e);
			} catch (JsonProcessingException e) {
				Outerapi.logging().logToError(e);
			}
			String imageData = jSON.toString();
			try {
				jSON = objectMapper.readTree(responseBody.toString());
			} catch (JsonMappingException e) {
				Outerapi.logging().logToError(e);
			} catch (JsonProcessingException e) {
				Outerapi.logging().logToError(e);
			}
			Matcher matcher = pattern.matcher(imageData);//Matching the pattern
			return (response.inferredMimeType().toString() == "JSON" || response.statedMimeType().toString() == "JSON") && pattern.matcher(response.bodyToString()).find();
		}

		@Override
		public void setRequestResponse(HttpRequestResponse requestResponse) {
			ObjectMapper objectMapper = new ObjectMapper();
			ByteArray responseBody = requestResponse.response().body();
			try {
				JsonNode prettyJson = objectMapper.readTree(responseBody.toString());
				String prettyJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(prettyJson);
				HttpResponse prettyResponse = requestResponse.response().withBody(prettyJsonString);
				this.responseEditor.setContents(prettyResponse.toByteArray());
				imageRender();//calling the method of imageRender
			} catch (IOException e) {
				Outerapi.logging().logToError(e);
			}
			
		}
		public void imageRender() throws IOException {
			ImageClass imgClass = new ImageClass();
			String imageData = jSON.toString();
			JPanel CaptchaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			JLabel jLabelImage = new JLabel();			
			Matcher matcher = pattern.matcher(imageData);//Matching the pattern
			if (matcher.find()) {
				String base64Data = matcher.group(0);//Getting images Base64 Data
				imgClass.imageSetter(base64Data, jLabelImage, CaptchaPanel, Outerapi);//Calling imageSetter Message from ImageClass
				container.removeAll();//Cleaning container before setting a new Image
				container.add(CaptchaPanel, BorderLayout.NORTH);
				container.add(responseEditor.uiComponent(), BorderLayout.CENTER);
			}

		}

		@Override
		public String caption() {
			return "CaptchaConverter";//Caption Name for extension
		}

		@Override
		public Selection selectedData() {
			return responseEditor.selection().isPresent() ? responseEditor.selection().get() : null;
		}

		@Override
		public boolean isModified() {
			return responseEditor.isModified();
		}

		@Override
		public HttpResponse getResponse() {
			return null;
		}

	}


}
