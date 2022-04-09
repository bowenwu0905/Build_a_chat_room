package sequentialSolution;

import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.util.Map;

/**
 * Main class of sequential solution
 *
 * @author bowen
 */
public class Main {

  /**
   *
   * @param args, the arguments taken in as the path of files
   * @throws IOException when certain error happens
   * @throws CsvValidationException
   * The function for running run function
   */
  public static void main(String[] args) throws IOException, CsvValidationException {
    Main main = new Main();
    main.run(args);

  }

  private void run(String[] args) throws CsvValidationException, IOException {
    CSVProcessor processor = new CSVProcessor(args);
    Map<String, Map<String, Integer>> csvMap = processor.process();
    CSVGenerator generator = new CSVGenerator();
    generator.generateCSVFiles(csvMap);

  }

  @Override
  public String toString() {
    return "Main{}";
  }
}
