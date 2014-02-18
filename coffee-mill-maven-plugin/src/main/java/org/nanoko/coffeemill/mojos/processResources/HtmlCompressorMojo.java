package org.nanoko.coffeemill.mojos.processResources;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.maven.WatchingException;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

/**
 * Compress HTML files.
 */
@Mojo(name = "compress-html", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class HtmlCompressorMojo extends AbstractCoffeeMillWatcherMojo {
	
	/**
     * Enables html compression.
     */
    @Parameter(defaultValue="false")
    private boolean skipHtmlCompression;
    
    public void setSkipHtmlCompression(Boolean skip){
    	this.skipHtmlCompression = skip;
    }
	
    /**
     * Enables html compression.
     */
    @Parameter
    protected Map<String,String> htmlCompressionOptions;    
    
    //private List<Pattern> preservePatterns = new ArrayList<Pattern>();
    private boolean preserveLineBreak = true;
    private boolean removeComments = false;
    private boolean removeMultispaces = false;
    private boolean removeFormAttributes = false;
    private boolean removeHttpProtocol = false;
    private boolean removeHttpsProtocol = false;
    private boolean removeInputAttributes = false;
    private boolean removeIntertagSpaces = false;
    private boolean removeJavascriptProtocol = false;
    private boolean removeLinkAttributes = false;
    private boolean removeQuotes = false;
    private boolean removeScriptAttributes = false;
    private boolean removeStyleAttributes = false;
    private boolean simpleBooleanAttributes = false;
    private boolean simpleDocType = false;
    
    public String inputFilename = null;
    
    private HtmlCompressor htmlCompressor;    
    

    public void execute() throws MojoExecutionException {
    	if(isSkipped()) { 
    		return; 
    	}
    	
    	this.configure();
    	
    	try {
	    	Collection<File> files = FileUtils.listFiles(getAssetsDir(), new String[]{"html", "htm"}, true);
	        for (File file : files) {          
				compress(file);
	        }        
    	} catch (WatchingException e) {
    		this.getLog().error(e);
		}
        
    }    

    public boolean accept(File file) {
        return  !isSkipped() 
        		 && ( FSUtils.hasExtension(file, "html") || FSUtils.hasExtension(file, "htm") );
    }
    
    private boolean compress(File file) throws WatchingException {
    	getLog().info("Compress Html file "+file.getName() +" from " + this.getAssetsDir().getAbsolutePath());
    	try {
            String result = htmlCompressor.compress(FileUtils.readFileToString(file));
            //File out = getOutputHtmlFile(file);
            File out = FSUtils.computeRelativeFile(file, getAssetsDir(), getWorkDirectory());
            if(out.exists()){
            	FileUtils.deleteQuietly(out);
            }
            out.getParentFile().mkdirs();
            FileUtils.write(out, result);
            writeStatistics(htmlCompressor, file);
        } catch(Exception e) {
            throw new WatchingException("Error during Html compression on file "+file.getName() + " : " + e.getMessage());
        }

        getLog().info("HTML compression completed.");
        return true;
    }

    public boolean fileCreated(File file) throws WatchingException {
    	compress(file);
        return true;
    }


    public boolean fileUpdated(File file) throws WatchingException {
    	compress(file);
        return true;
    }

    public boolean fileDeleted(File file) throws WatchingException{
    	compress(file);
        return true;
    }
    
    private boolean isSkipped(){
    	if (skipHtmlCompression) {
            getLog().info("\033[31m HTML Compression skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }
    
    
    /**
     * Configure method to define HtmlCompressor and set all compression options
     */
    public void configure(){
    	htmlCompressor = new HtmlCompressor();

    	if(htmlCompressionOptions!=null && !htmlCompressionOptions.isEmpty() ){	    		
	        if(htmlCompressionOptions.containsKey("preserveLineBreak")) { 
	        	preserveLineBreak = Boolean.valueOf(htmlCompressionOptions.get("preserveLineBreak")); 
	        }
	    	if(htmlCompressionOptions.containsKey("removeComments")) { 
	    		removeComments = Boolean.valueOf(htmlCompressionOptions.get("removeComments")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("removeMultispaces")) { 
	    		removeMultispaces = Boolean.valueOf(htmlCompressionOptions.get("removeMultispaces")); 
	    	}    	
	    	if(htmlCompressionOptions.containsKey("removeFormAttributes")) { 
	    		removeFormAttributes = Boolean.valueOf(htmlCompressionOptions.get("removeFormAttributes")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("removeHttpProtocol")) { 
	    		removeHttpProtocol = Boolean.valueOf(htmlCompressionOptions.get("removeHttpProtocol")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("removeHttpsProtocol")) { 
	    		removeHttpsProtocol = Boolean.valueOf(htmlCompressionOptions.get("removeHttpsProtocol")); 
	    	}    	
	    	if(htmlCompressionOptions.containsKey("removeInputAttributes")) { 
	    		removeInputAttributes = Boolean.valueOf(htmlCompressionOptions.get("removeInputAttributes")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("removeIntertagSpaces")) { 
	    		removeIntertagSpaces = Boolean.valueOf(htmlCompressionOptions.get("removeIntertagSpaces")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("removeJavascriptProtocol")) { 
	    		removeJavascriptProtocol = Boolean.valueOf(htmlCompressionOptions.get("removeJavascriptProtocol")); 
	    	}    	
	    	if(htmlCompressionOptions.containsKey("removeLinkAttributes")) { 
	    		removeLinkAttributes = Boolean.valueOf(htmlCompressionOptions.get("removeLinkAttributes")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("removeQuotes")) { 
	    		removeQuotes = Boolean.valueOf(htmlCompressionOptions.get("removeQuotes")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("removeScriptAttributes")) { 
	    		removeScriptAttributes = Boolean.valueOf(htmlCompressionOptions.get("removeScriptAttributes")); 
	    	}    	
	    	if(htmlCompressionOptions.containsKey("removeStyleAttributes")) { 
	    		removeStyleAttributes = Boolean.valueOf(htmlCompressionOptions.get("removeStyleAttributes")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("simpleBooleanAttributes")) { 
	    		simpleBooleanAttributes = Boolean.valueOf(htmlCompressionOptions.get("simpleBooleanAttributes")); 
	    	}
	    	if(htmlCompressionOptions.containsKey("simpleDocType")) { 
	    		simpleDocType = Boolean.valueOf(htmlCompressionOptions.get("simpleDocType")); 
	    	}
    	}	
    	
    	htmlCompressor.setPreserveLineBreaks(preserveLineBreak);
    	htmlCompressor.setRemoveComments(removeComments); 
        htmlCompressor.setRemoveMultiSpaces(removeMultispaces);        
        htmlCompressor.setRemoveFormAttributes(removeFormAttributes);
        htmlCompressor.setRemoveHttpProtocol(removeHttpProtocol);
        htmlCompressor.setRemoveHttpsProtocol(removeHttpsProtocol);        
        htmlCompressor.setRemoveInputAttributes(removeInputAttributes);
        htmlCompressor.setRemoveIntertagSpaces(removeIntertagSpaces);
        htmlCompressor.setRemoveJavaScriptProtocol(removeJavascriptProtocol);        
        htmlCompressor.setRemoveLinkAttributes(removeLinkAttributes);
        htmlCompressor.setRemoveQuotes(removeQuotes);
        htmlCompressor.setRemoveScriptAttributes(removeScriptAttributes);        
        htmlCompressor.setRemoveStyleAttributes(removeStyleAttributes);
        htmlCompressor.setSimpleBooleanAttributes(simpleBooleanAttributes);
        htmlCompressor.setSimpleDoctype(simpleDocType);    
        
        htmlCompressor.setCompressCss(false);
        htmlCompressor.setCompressJavaScript(false);
        htmlCompressor.setEnabled(true);
        htmlCompressor.setGenerateStatistics(true);

        /*if(!preservePatterns.isEmpty())
        	htmlCompressor.setPreservePatterns(preservePatterns);*/
    }
    
    
    
    private void writeStatistics(HtmlCompressor htmlCompressor, File file) {
        boolean si = true;

        int origFilesizeBytes = htmlCompressor.getStatistics().getOriginalMetrics().getFilesize();
        String origFilesize = humanReadableByteCount(origFilesizeBytes, si);
        String origEmptyChars = String.valueOf(htmlCompressor.getStatistics().getOriginalMetrics().getEmptyChars());
        String origInlineEventSize = humanReadableByteCount(htmlCompressor.getStatistics().getOriginalMetrics().getInlineEventSize(), si);
        String origInlineScriptSize =humanReadableByteCount(htmlCompressor.getStatistics().getOriginalMetrics().getInlineScriptSize(), si);
        String origInlineStyleSize =humanReadableByteCount(htmlCompressor.getStatistics().getOriginalMetrics().getInlineStyleSize(), si);

        int compFilesizeBytes = htmlCompressor.getStatistics().getCompressedMetrics().getFilesize();
        String compFilesize =humanReadableByteCount(compFilesizeBytes, si);
        String compEmptyChars = String.valueOf(htmlCompressor.getStatistics().getCompressedMetrics().getEmptyChars());
        String compInlineEventSize =humanReadableByteCount(htmlCompressor.getStatistics().getCompressedMetrics().getInlineEventSize(), si);
        String compInlineScriptSize =humanReadableByteCount(htmlCompressor.getStatistics().getCompressedMetrics().getInlineScriptSize(), si);
        String compInlineStyleSize =humanReadableByteCount(htmlCompressor.getStatistics().getCompressedMetrics().getInlineStyleSize(), si);

        String elapsedTime = getElapsedHMSTime(htmlCompressor.getStatistics().getTime());
        String preservedSize =humanReadableByteCount(htmlCompressor.getStatistics().getPreservedSize(), si);
        Float compressionRatio = new Float(compFilesizeBytes) / new Float(origFilesizeBytes);
        Float spaceSavings = new Float(1) - compressionRatio;

        String format = "%-30s%-30s%-30s%-2s";
        NumberFormat formatter = new DecimalFormat("#0.00");
        String eol = "\n";
        String hr = "+-----------------------------+-----------------------------+-----------------------------+";
        StringBuilder sb = new StringBuilder(file.getName() + " - HTML compression statistics:").append(eol);
        sb.append(hr).append(eol);
        sb.append(String.format(format, "| Category", "| Original", "| Compressed", "|")).append(eol);
        sb.append(hr).append(eol);
        sb.append(String.format(format, "| Filesize", "| " + origFilesize, "| " + compFilesize, "|")).append(eol);
        sb.append(String.format(format, "| Empty Chars", "| " + origEmptyChars, "| " + compEmptyChars, "|")).append(eol);
        sb.append(String.format(format, "| Script Size", "| " + origInlineScriptSize, "| " + compInlineScriptSize, "|")).append(eol);
        sb.append(String.format(format, "| Style Size", "| " + origInlineStyleSize, "| " + compInlineStyleSize, "|")).append(eol);
        sb.append(String.format(format, "| Event Handler Size", "| " + origInlineEventSize, "| " + compInlineEventSize, "|")).append(eol);
        sb.append(hr).append(eol);
        sb.append(String.format("%-90s%-2s",
                String.format("| Time: %s, Preserved: %s, Compression Ratio: %s, Savings: %s%%",
                        elapsedTime, preservedSize, formatter.format(compressionRatio), formatter.format(spaceSavings*100)),
                "|")).append(eol);
        sb.append(hr).append(eol);

        String statistics = sb.toString();
        getLog().info(statistics);
    }

    /*private File getOutputHtmlFile(File input) {
        String path = input.getParentFile().getAbsolutePath().substring(getAssetsDir().getAbsolutePath().length());
        return new File(getWorkDirectory(), path + "/" + input.getName());
    }*/

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) { 
        	return bytes + " B"; 
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getElapsedHMSTime(long elapsedTime) {
        String format = String.format("%%0%dd", 2);
        elapsedTime = elapsedTime / 1000;
        String seconds = String.format(format, elapsedTime % 60);
        String minutes = String.format(format, (elapsedTime % 3600) / 60);
        String hours = String.format(format, elapsedTime / 3600);
        return hours + ":" + minutes + ":" + seconds;
    }

}
