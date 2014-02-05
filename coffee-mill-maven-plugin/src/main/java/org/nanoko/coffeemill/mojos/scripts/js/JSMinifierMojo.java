package org.nanoko.coffeemill.mojos.scripts.js;

import static org.nanoko.java.NPM.npm;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.maven.WatchingException;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;


/**
 * Minifies JavaScript files.
 * <ul>
 * <ol>It minifies the JavaScript files</ol>
 * <ol>It minifies the JavaScript file generated from CoffeeScript</ol>
 * </ul>
 * <p/>
 * This mojo makes the assumption that the files are already copied/generated to their destination directory,
 * when it is executed.
 */
@Mojo(name = "minify-javascript", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.PACKAGE)
public class JSMinifierMojo extends AbstractCoffeeMillWatcherMojo{

	@Parameter(defaultValue = "SIMPLE_OPTIMIZATIONS")
    public CompilationLevel googleClosureCompilationLevel;
    
    @Parameter(defaultValue = "false")
    public boolean googleClosurePrettyPrint;

    @Parameter(defaultValue = "${skipGoogleClosure}")
    public boolean skipGoogleClosure;
    
    
	
	public void execute() throws MojoExecutionException {
		if (skipGoogleClosure) {
            getLog().debug("Skipping Google Closure Compilation");
            removeFromWatching();
            return;
        }
        
        if (getJavaScriptDir().isDirectory()) {
        	try {
            	getLog().info("Compiling JavaScript files from " + getJavaScriptDir().getAbsolutePath());
            	minify();
            } catch (WatchingException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
       
    }

    public boolean accept(File file) {
    	getLog().info("JS MINI : ACCEPT " + FSUtils.hasExtension(file, scriptExtensions));
        return FSUtils.hasExtension(file, scriptExtensions);
    }
	
	public void minify() throws WatchingException {
		getLog().info("Minify JavaScript files from " + this.javaScriptDir.getName() + " using Google Closure");
        
        // Define JS Google Compiler with Options
        PrintStream ps = new PrintStream(System.err, true); // TODO Fix with log.
        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler(ps);
        CompilerOptions options = newCompilerOptions();
        getLog().info("Minification Level set to " + googleClosureCompilationLevel);
        googleClosureCompilationLevel.setOptionsForCompilationLevel(options);
        options.setPrettyPrint(googleClosurePrettyPrint);
        options.setPrintInputDelimiter(googleClosurePrettyPrint);
        
        String fileName = this.project.getArtifactId()+"-"+this.project.getVersion();
        File input = new File( this.getBuildDirectory().getAbsolutePath()+File.separator+fileName+".js");
    	if(!input.exists())
    		return;    	
        
        List<SourceFile> sourceInput = new ArrayList<>();
        sourceInput.add(SourceFile.fromFile(input));
        List<SourceFile> sourceExtern = new ArrayList<>();
        
        final Result result = compiler.compile(sourceExtern, sourceInput, options);
        //listErrors(result);

        if (!result.success) {
            throw new WatchingException("Error while compile JavaScript files, check log for more details");
        }

        String[] outputs = compiler.toSourceArray();
        if(!outputs[0].isEmpty()){
            try {
            	File output = getMinifiedFile(input);
                getLog().info("Minify " + input.getAbsolutePath() + " to " + output.getAbsolutePath());
            	if(output.exists())
            		FileUtils.deleteQuietly(output);
                FileUtils.write(output, outputs[0]);
            } catch (IOException e) {
                throw new WatchingException("Cannot write minified JavaScript file : " + input, e);
            }
        }else
        	getLog().warn("Cannot write minified JavaScript file : " + input + " ==> empty output result");
    	
    }
	
	
	/**
     * @return default {@link CompilerOptions} object to be used by compressor.
     */
    protected CompilerOptions newCompilerOptions() {
        final CompilerOptions options = new CompilerOptions();
        
         // According to John Lenz from the Closure Compiler project, if you are using the Compiler API directly, you
         // should specify a CodingConvention. {@link http://code.google.com/p/wro4j/issues/detail?id=155}
        
        options.setCodingConvention(new ClosureCodingConvention());
        //set it to warning, otherwise compiler will fail
        options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES,
                CheckLevel.WARNING);
        return options;
    }
	
    /**
     * List the errors that google is providing from the compiler output.
     *
     * @param result the results from the compiler
     */
    private void listErrors(final Result result) {
        for (JSError warning : result.warnings)
            getLog().warn(warning.toString());

        for (JSError error : result.errors)
            getLog().error(error.toString());
    }
    
    private boolean isMinified(File file) {
        return file.getName().endsWith("min.js");
    }

    private File getMinifiedFile(File file) {
        String name = file.getName().replace(".js", "-min.js");
        return new File(file.getParentFile(), name);
    }
	
	public boolean fileCreated(File file) throws WatchingException {
        minify();
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
    	minify();
        return true;
    }

    public boolean fileDeleted(File file) throws WatchingException{
    	minify();
        return true;
    }
	
}




