import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;

public class ExtensionUnload implements ExtensionUnloadingHandler{
	   private final MontoyaApi api;

	    public ExtensionUnload(MontoyaApi api)
	    {
	        this.api = api;
	    }

	    @Override
	    public void extensionUnloaded()
	    {
	    	api.logging().logToOutput("============{####}============");//This is just formality
	        api.logging().logToOutput("Extension has been unloaded.");
	        api.logging().logToOutput("============{####}============");
	    }
}
