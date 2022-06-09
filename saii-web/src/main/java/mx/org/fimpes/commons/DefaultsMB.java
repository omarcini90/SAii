package mx.org.fimpes.commons;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Named(value = "defaultsMB")
@SessionScoped
public class DefaultsMB implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(DefaultsMB.class);
    private static final MimeTypes MIME_TYPES;
    private static final Map<String, String> FROM_2017, FOUR_YEARS_AFTER;
    public static final String SUCCESS_MESSAGE;
    public static final String FAILURE_MESSAGE;

    static {
        MIME_TYPES = MimeTypes.getDefaultMimeTypes();
        SUCCESS_MESSAGE = "EXITOSAMENTE!";
        FAILURE_MESSAGE = "LO SENTIMOS, INTENTELO MÁS TARDE.";
        
        int now = MxDateTime.now().getYear();
        
        FROM_2017 = new TreeMap<String, String>(Collections.reverseOrder());        
        for(int y = 2017; y <= now; y++){
            String year = String.valueOf(y);
            FROM_2017.put(year,year);
        }
        
        FOUR_YEARS_AFTER = new TreeMap<String, String>();
        for(int y = now-1; y <= now+4; y++){
            String year = String.valueOf(y);
            FOUR_YEARS_AFTER.put(year,year);
        }
    }
    
    public static enum PersistAction {
        CREATE, UPDATE, DELETE
    }
    
    /**
     * Creates a new instance of DefaultsMB
     */
    public DefaultsMB() {
    }

    @PostConstruct
    public void init() {
        
    }

    @PreDestroy
    public void destroy() {
    }

    public Date getDate() {
        return MxDateTime.date();
    }
    
    public String getFullFormattedDate(){
        return MxDateTime.FILE_FORMAT.format(getDate());
    }
    
    public TimeZone getCdmxTimeZone() {
    	return MxDateTime.CDMX_TIMEZONE;
    }
    
    public Map<String, String> getFrom2017(){
        return FROM_2017;
    }
    
    public Map<String, String> getFourYearsAfter(){
        return FOUR_YEARS_AFTER;
    }
    
    public String toString(Object o) {
    	return o.toString();
    }
    
    public static MimeType detectMimeType(final InputStream stream) throws IOException, MimeTypeException {
        MediaType mediaType = MIME_TYPES.detect(stream, new Metadata());
        log.info("MediaType: " + mediaType.toString());
        MimeType mimeType = MIME_TYPES.forName(mediaType.toString());        
        log.info("MimeType: " + mimeType.getName() + " Ext: " + mimeType.getExtension());

        return mimeType;
    }

    public static String getProperty(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }
    
    private static String rename(String prefix, boolean appendTime, String ext) throws Exception {    	
    	var filename = new StringBuilder(prefix);
        if(appendTime) {
        	filename.append("_");
            filename.append(MxDateTime.FILE_FORMAT.format(MxDateTime.date()));
        }
        filename.append(".");
        filename.append(ext);
        
        return filename.toString();
    }
    
	public static String saveFileToDisk(String directory, String prefix, boolean appendTime, String ext, final InputStream stream) throws Exception {
        Path path = Paths.get(directory);
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }
        if(ext == null) {
        	MimeType mimeType = detectMimeType(stream);
        	prefix = rename(prefix, appendTime, mimeType.getExtension());
        }else {
        	prefix = rename(prefix, appendTime, ext);
        }
        
        path = Paths.get(directory.concat(prefix));
        byte[] bytes = IOUtils.toByteArray(stream);
        Files.write(path, bytes); //creates, overwrites
        var type = Files.probeContentType(path);
        log.info("Written file: " + path.toString() + " [" + type + " " + bytes.length + " bytes]");

        return path.toString();
    }

    public static StreamedContent readFileFromDisk(String absolutePath) throws IOException, MimeTypeException {
        Path path = Paths.get(absolutePath);
        byte[] bytes = Files.readAllBytes(path);
        InputStream stream = new ByteArrayInputStream(bytes);
        var type = Files.probeContentType(path);        
        StreamedContent file = new DefaultStreamedContent(stream, type, path.getFileName().toString());
        log.info("Read file: " + file.getName() + " [" + type + " " + bytes.length + " bytes]");

        return file;
    }

    public static void removeFileFromDisk(String absolutePath) throws IOException, MimeTypeException {
        Path path = Paths.get(absolutePath);
        Files.delete(path);
        log.info("Deleted file: " + absolutePath);
    }
    
    
	public static void logStream(InputStream stream, Level severity) throws IOException {
		var reader = new BufferedReader(new InputStreamReader(stream));
		var output = new StringBuilder("Stream Output: \n");
		var line = "";
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}
		reader.close();
		
		switch (severity.toInt()) {
			default:
			case Level.INFO_INT:
				log.info(output.toString());
				break;
			case Level.WARN_INT:
				log.warn(output.toString());
				break;
			case Level.ERROR_INT:
				log.error(output.toString());
				break;
		}
	}
    
    public static void exec(String command) throws IOException, InterruptedException {
    	log.info("Executing... " + command);
    	Process process = Runtime.getRuntime().exec(command);
    	logStream(process.getInputStream(), Level.INFO);
    	logStream(process.getErrorStream(), Level.ERROR);
        log.info("Exit code: " + process.waitFor());
    }

    public static String toJson(Object obj) {
        /* IMPORTANT: Verify that the object doesn't have back references, causing infinite recursion
         * From object: @JsonManagedReference, To object: @JsonBackReference
         */
        String json = null;
        try {
            var mapper = new ObjectMapper();
            json = mapper.writeValueAsString(obj);
            // With pretty print:
            /* json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj); */
        } catch (IOException e) {
            log.error("Error en la generacion del JSON a guardar: " + e.getMessage());
        }
        return json;
    }
    
    public static String toPrettyJson(String json) {
        String j = null;
        try {
            var mapper = new ObjectMapper();
            Object ojson = mapper.readValue(json, Object.class);
            j = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ojson);
        } catch (IOException e) {
            log.error("Error en la transformación del JSON: " + e.getMessage());
        }
        return j;
    }

}
