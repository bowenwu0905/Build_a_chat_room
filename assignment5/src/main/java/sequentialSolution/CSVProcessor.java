package sequentialSolution;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * CSVProcessor class of sequential solution, process the existed CVS files and generate a hashMap
 *
 * @author bowen
 */
public class CSVProcessor {
  String courseFilePath;
  String studentFilePath;
  String[] inputArgs;
  private Map<String, Map<String, Integer>> res;

  /**
   *
   * @param courseFilePath, the path of courseFile
   * @param studentFilePath, the path of studentFile
   * @param inputArgs, the path that takes in as argument
   */
  public CSVProcessor(String courseFilePath, String studentFilePath, String[] inputArgs) {
    this.courseFilePath = courseFilePath;
    this.studentFilePath = studentFilePath;
    this.inputArgs = inputArgs;
    this.res = new HashMap<>();
  }


  /**
   *
   * @return the hashmap generated as result
   */
  public Map<String, Map<String, Integer>> getRes() {
    return res;
  }

  /**
   *
   * @param res, set the hashmap
   */
  public void setRes(
      Map<String, Map<String, Integer>> res) {
    this.res = res;
  }

  /**
   *
   * @param path, the path of the parameter taken in
   * @return absolutePath, string
   */
  public String absolutePathChange(String path) {
    File file = new File(path);
    if (file.isAbsolute()) {
      return path;
    } else {
      String filePath = new File("").getAbsolutePath();

      return filePath.concat("/" + path);
    }
  }

  /**
   *
   * @return Map as the csvMap generated
   * @throws CsvValidationException when the csv is not valid
   * @throws IOException when certain error happens
   */
  public Map<String, Map<String,Integer>> process() throws CsvValidationException, IOException {
    readCourse(courseFilePath);
    countCourse(studentFilePath);
    return res;
  }

  /**
   * read the course file and generate the key of csvMap
   * @param courseFilePath, the path of courseFile as a String
   * @throws IOException when certain error happens
   * @throws CsvValidationException when the csv file is not valid
   */
  public void readCourse(String courseFilePath) throws IOException, CsvValidationException {
    FileReader fileReader = new FileReader(absolutePathChange(courseFilePath));
    CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
    String[] nextLine;
    while((nextLine = csvReader.readNext()) != null){
      String key = nextLine[0]+"_"+nextLine[1];
      res.put(key, new HashMap<String, Integer>());
    }
  }

  /**
   * count the courseFile and generate the key of the csvMap
   * @param studentFilePath, the path of courseFile as a String
   * @throws IOException when certain error happens
   * @throws CsvValidationException when the csv file is not valid
   */
  public void countCourse(String studentFilePath) throws IOException, CsvValidationException {
    FileReader fileReader = new FileReader(absolutePathChange(studentFilePath));
    CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
    String[] nextLine;
    while((nextLine = csvReader.readNext()) != null){
      String key = nextLine[0] + "_" + nextLine[1];
      Map<String, Integer> record = res.get(key);
      String date = nextLine[4];
      record.put(date, record.getOrDefault(date, 0) + Integer.parseInt(nextLine[5]));
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CSVProcessor)) {
      return false;
    }
    CSVProcessor that = (CSVProcessor) o;
    return Objects.equals(courseFilePath, that.courseFilePath) && Objects.equals(
        studentFilePath, that.studentFilePath) && Arrays.equals(inputArgs, that.inputArgs)
        && Objects.equals(getRes(), that.getRes());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(courseFilePath, studentFilePath, getRes());
    result = 31 * result + Arrays.hashCode(inputArgs);
    return result;
  }

  @Override
  public String toString() {
    return "CSVProcessor{" +
        "courseFilePath='" + courseFilePath + '\'' +
        ", studentFilePath='" + studentFilePath + '\'' +
        ", inputArgs=" + Arrays.toString(inputArgs) +
        ", res=" + res +
        '}';
  }
}
