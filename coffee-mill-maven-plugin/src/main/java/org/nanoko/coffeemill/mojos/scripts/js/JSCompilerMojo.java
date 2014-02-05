package org.nanoko.coffeemill.mojos.scripts.js;

import com.google.javascript.jscomp.*;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;

import org.nanoko.maven.WatchingException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Compiles JavaScript files.
 * <ul>
 * <ol>It compiles (checks) JavaScript files from src/main/js.</ol>
 * <ol>It copies these JavaScript files into work directory target/www.</ol>
 * </ul>
 * <p/>
 * This mojo makes the assumption that the files are already copied/generated to their destination directory,
 * when it is executed.
 */
@Mojo(name = "compile-javascript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class JSCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    /**
     * Selects the compilation level for Google Closure among SIMPLE_OPTIMIZATIONS,
     * WHITESPACE_ONLY and ADVANCED_OPTIMIZATIONS.
     * Be aware that ADVANCED_OPTIMIZATIONS modifies the API of your code.
     */
    @Parameter(defaultValue = "SIMPLE_OPTIMIZATIONS")
    public CompilationLevel googleClosureCompilationLevel;
    
    @Parameter(defaultValue = "true")
    public boolean googleClosurePrettyPrint;

    @Parameter(defaultValue = "${skipGoogleClosure}")
    public boolean skipGoogleClosure;

    
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipGoogleClosure) {
            getLog().debug("Skipping Google Closure Compilation");
            removeFromWatching();
            return;
        }
        
        if (this.javaScriptDir.isDirectory()) {
        	try {
	        	getLog().info("Compiling JavaScript files from " + this.javaScriptDir.getAbsolutePath());
	        	Collection<File> files = FileUtils.listFiles(this.javaScriptDir, new String[]{"js"}, true);
	            for(File file : files)
	            	compile(file);
                
            } catch (WatchingException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
        
    }

    
    public boolean accept(File file) {
    	return FSUtils.isInDirectory(file.getName(), this.javaScriptDir) && FSUtils.hasExtension(file, "js");
    }
    

    private void compile(File file) throws WatchingException {
        getLog().info("Compiling JavaScript files from " + this.javaScriptDir.getName() + " using Google Closure");
        
        // Define JS Google Compiler with Options
        PrintStream ps = new PrintStream(System.err, true); // TODO Fix with log.
        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler(ps);
        
        CompilerOptions options = newCompilerOptions();
        getLog().info("Compilation Level set to " + googleClosureCompilationLevel);
        googleClosureCompilationLevel.setOptionsForCompilationLevel(options);
        
        File outputFile = new File(this.workDir.getAbsolutePath() + File.separator + file.getName());
        
        List<SourceFile> inputs = new ArrayList<>();        
        //TODO: Manage externs
        List<SourceFile> externs = new ArrayList<>();
        
        if (file.isFile() )        	
	        inputs.add(SourceFile.fromFile(file));
        
        final Result result = compiler.compile(externs, inputs, options);
        //listErrors(result);
        
        if (!result.success) {
            throw new WatchingException("Error while compile JavaScript files, check log for more details");
        }        

        String[] outputs = compiler.toSourceArray();
        try {
            FileUtils.write(outputFile, outputs[0]);
        } catch (IOException e) {
            throw new WatchingException("Cannot write compiled JavaScript file : " + outputFile, e);
        }

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
        
        options.setPrettyPrint(googleClosurePrettyPrint);
        options.setPrintInputDelimiter(googleClosurePrettyPrint);
        
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
    
    
    public boolean fileCreated(File file) throws WatchingException {
        if (FSUtils.isInDirectory(file.getName(), this.javaScriptDir))
            compile(file);
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
        return fileCreated(file);
    }

    public boolean fileDeleted(File file) {
    	File deleted = new File(this.workDir.getAbsolutePath() + File.separator + file.getName());
        if (deleted.isFile()){
        	getLog().info("deleted File : "+file.getName());    	
        	FileUtils.deleteQuietly(deleted); 
        }
        return true;
    }

}
