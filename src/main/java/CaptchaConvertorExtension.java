
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import burp.api.montoya.ui.editor.extension.HttpResponseEditorProvider;

public class CaptchaConvertorExtension implements BurpExtension  {

	
	@Override
	public void initialize(MontoyaApi api) {
		api.logging().logToOutput("============{####}============");
		api.logging().logToOutput(" Captcha Converter extension has been loaded.");
		api.logging().logToOutput(" Developed by Lzzaps3c.");
		api.logging().logToOutput("============{####}============");
		api.extension().setName("Captcha Converter");
		api.userInterface().registerHttpResponseEditorProvider(new CaptchaTab(api));
		api.extension().registerUnloadingHandler(new ExtensionUnload(api));
		
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
		BufferedImage bufImage;//Image

		@Override
		public Component uiComponent() {
			return container;//we are using out JPanel as our UI component
		}


		CaptchaConvert(MontoyaApi api, EditorCreationContext creationContext) {//Hey you If you are reading this to develop your own extension
			if (creationContext.editorMode() == EditorMode.READ_ONLY) {//You can use EditorCreationContext if you want to display output differently for different tools
				responseEditor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
			} else {
				responseEditor = api.userInterface().createRawEditor();
			}
		}

		@Override
		public boolean isEnabledFor(HttpRequestResponse requestResponse) {//Enabling CaptchaConverter Tab if response contains JSON data
			MimeType isJSONinfer = requestResponse.response().inferredMimeType();
			MimeType isJSONstate = requestResponse.response().statedMimeType();
			return (isJSONinfer.toString().equals("JSON") || isJSONstate.toString().equals("JSON"));

		}

		@Override
		public void setRequestResponse(HttpRequestResponse requestResponse) {
		
			ResponseClass responseMaker = new ResponseClass();
			ObjectMapper objectMapper = new ObjectMapper();
			ByteArray responseBody = requestResponse.response().body();
			String httpVersion= requestResponse.response().httpVersion();
			short statusCode= requestResponse.response().statusCode();
			List<HttpHeader> responseHeaders = requestResponse.response().headers();
			StringBuilder headerStringBuilder = new StringBuilder();
			try {
				jSON = objectMapper.readTree(responseBody.toString()); //Getting response as JSON
				imageRender();//calling the method of imageRender
			} catch (IOException e) {
				e.printStackTrace();
			} 

			try {
				ByteArray output = responseMaker.responseMaker(httpVersion,statusCode,responseHeaders ,headerStringBuilder, objectMapper,responseBody);//Calling Method from ResponseClass
				this.responseEditor.setContents(output);//Setting repsonse to editor(where u see the response in Captcha Converter TAB)
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		
		public void imageRender() throws IOException {
			ImageClass imgClass = new ImageClass();
			String imageData = jSON.toString();
			JPanel CaptchaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			JLabel jLabelImage = new JLabel();
			JLabel jLabelError = new JLabel();
			String regex = "/9j/4A[^\"]*|iVBORw0KGgo[^\"]*|/9j/2wBDAAMCAgMCAg[^\"]*|/9j/7g[^\"]|/9j/4Q[^\"]";//Regex for Magic Bytes JPEG,JPG,JFIF,PNG
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(imageData);//Matching the pattern
			if (matcher.find()) {
				String base64Data = matcher.group(0);//Getting images Base64 Data
				imgClass.imageSetter(base64Data, jLabelImage, CaptchaPanel);//Calling imageSetter Message from ImageClass
				container.removeAll();//Cleaning container before setting a new Image
				container.add(CaptchaPanel, BorderLayout.NORTH);
				container.add(responseEditor.uiComponent(), BorderLayout.CENTER);
			} else {
				imgClass.errorSetter(jLabelError, CaptchaPanel);//Calling errorSetter from imgClass
				container.removeAll();//Cleaning container before setting a new Label
				container.add(CaptchaPanel, BorderLayout.NORTH);
				container.add(this.responseEditor.uiComponent(), BorderLayout.CENTER);
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
